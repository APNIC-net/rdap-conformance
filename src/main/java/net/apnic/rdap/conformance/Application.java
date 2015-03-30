package net.apnic.rdap.conformance;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import com.google.common.util.concurrent.RateLimiter;
import org.apache.http.HttpStatus;
import org.apache.commons.lang.SerializationUtils;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import net.apnic.rdap.conformance.specification.ObjectClass;
import net.apnic.rdap.conformance.specification.ObjectClassSearch;
import net.apnic.rdap.conformance.test.common.Search;
import net.apnic.rdap.conformance.test.common.NotFound;
import net.apnic.rdap.conformance.test.common.Redirect;
import net.apnic.rdap.conformance.test.common.BasicRequest;

/**
 * <p>Application class.</p>
 *
 * The entry point for the validator. Loads the specified
 * configuration file, runs tests accordingly and prints results to
 * stdout.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Application {
    private static final int EX_USAGE = 64;
    private static final int EX_NOINPUT = 66;
    private static final int EX_SOFTWARE = 70;

    private static final int TESTS_RUNNING_CHECK_DELAY_MS = 1000;

    private static final List<String> OBJECT_TYPES =
        Arrays.asList("ip", "nameserver", "autnum",
                      "entity", "domain");

    private static final Result EXTRA_QUERY_PARAM =
        getDocRefProto("rfc7480", "4.3",
                       "common.extra-query-parameter");

    private static final TrustManager[] TRUST_MANAGER =
        new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                    final X509Certificate[] chain,
                    final String authType
                ) { }
                @Override
                public void checkServerTrusted(
                    final X509Certificate[] chain,
                    final String authType
                ) { }
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }
        };

    private Application() { }

    private static String getJarName() {
        String jarName = null;
        try {
            jarName =
                new java.io.File(Context.class.getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .getPath())
                        .getName();
        } catch (Exception e) {
            jarName = "rdap-conformance.jar";
        }
        return jarName;
    }

    private static void addSearchTests(final List<Test> tests,
                                       final ObjectClass oc,
                                       final SearchTest st,
                                       final String prefix,
                                       final String testName,
                                       final String searchKey)
            throws Exception {
        Map<String, ObjectClassSearch> mocs = oc.getSearch();
        if (mocs == null) {
            return;
        }

        for (Map.Entry<String, ObjectClassSearch> entry : mocs.entrySet()) {
            String key = entry.getKey();
            ObjectClassSearch ocps = entry.getValue();
            if (ocps.getSupported()) {
                for (String keyValue : ocps.getExists()) {
                    tests.add(
                        new Search(
                            (SearchTest) SerializationUtils.clone(st),
                            prefix, key, keyValue, testName, searchKey,
                            Search.ExpectedResultType.SOME
                        )
                    );
                }
                for (String keyValue : ocps.getNotExists()) {
                    tests.add(
                        new Search(
                            (SearchTest) SerializationUtils.clone(st),
                            prefix, key, keyValue, testName, searchKey,
                            Search.ExpectedResultType.NONE
                        )
                    );
                }
                for (String keyValue : ocps.getTruncated()) {
                    tests.add(
                        new Search(
                            (SearchTest) SerializationUtils.clone(st),
                            prefix, key, keyValue, testName, searchKey,
                            Search.ExpectedResultType.TRUNCATED
                        )
                    );
                }
            }
        }
    }

    private static Result getDocRefProto(final String document,
                                         final String reference,
                                         final String testName) {
        Result proto = new Result();
        proto.setDocument(document);
        proto.setReference(reference);
        proto.setTestName(testName);
        return proto;
    }

    private static void addNonRdapTests(final Context c,
                                        final List<Test> tests) {
        /* Relative URI in the HTTP request. */
        Result relative = new Result();
        relative.setTestName("common.bad-uri-relative");
        relative.setStatus(Result.Status.Notification);
        tests.add(
            new net.apnic.rdap.conformance.test.common.RawURIRequest(
                "domain/example.com",
                relative,
                false
            )
        );

        /* Unprintable characters in the URI in the HTTP request. */
        Result unprintable = new Result();
        unprintable.setTestName("common.bad-uri-unprintable");
        unprintable.setStatus(Result.Status.Notification);
        tests.add(
            new net.apnic.rdap.conformance.test.common.RawURIRequest(
                "/domain/" + new String(Character.toChars(0)),
                unprintable,
                false
            )
        );

        /* Absolute URI in the HTTP request. */
        Result absolute = new Result();
        absolute.setTestName("common.uri-absolute");
        absolute.setStatus(Result.Status.Notification);
        tests.add(
            new net.apnic.rdap.conformance.test.common.RawURIRequest(
                c.getSpecification().getBaseUrl() + "/domain/example.com",
                absolute,
                true
            )
        );
    }

    private static void addUnsupportedQueryTypeTests(final Specification s,
                                                     final List<Test> tests) {
        /* Previously, this required that the server return a 400 (Bad
         * Request) for unsupported queries, as per using-http [5.4].
         * However, rdap-query now states that for documented query
         * types, servers MUST return 501 if they don't
         * support/implement them. */
        for (String objectType : OBJECT_TYPES) {
            Result unsupported = new Result();
            unsupported.setTestName("common.unsupported");
            unsupported.setDocument("rfc7482");
            unsupported.setReference("1");
            ObjectClass oc = s.getObjectClass(objectType);
            if ((oc == null)
                    || (!s.getObjectClass(objectType).isSupported())) {
                tests.add(
                    new BasicRequest(
                        HttpStatus.SC_NOT_IMPLEMENTED,
                        "/" + objectType + "/1.2.3.4",
                        "common.unsupported",
                        false,
                        unsupported
                    ));
            }
        }
    }

    private static void addIpTests(final Specification s,
                                   final List<Test> tests) {
        ObjectClass ocIp = s.getObjectClass("ip");
        if ((ocIp == null) || !ocIp.isSupported()) {
            return;
        }

        tests.add(new net.apnic.rdap.conformance.test.ip.BadRequest());
        List<String> exists = ocIp.getExists();
        for (String e : exists) {
            tests.add(new net.apnic.rdap.conformance.test.ip.Standard(e));
        }
        List<String> notExists = ocIp.getNotExists();
        for (String e : notExists) {
            tests.add(new NotFound("/ip/" + e));
        }
        List<String> redirects = ocIp.getRedirects();
        for (String e : redirects) {
            tests.add(
                new Redirect(
                    new net.apnic.rdap.conformance.test.ip.Standard(),
                    "/ip/" + e, "ip.redirect"
                )
            );
        }
        /* Unescaped square brackets in the URI. */
        Result unescaped = new Result();
        unescaped.setTestName("ip.bad-uri-unescaped");
        tests.add(
            new net.apnic.rdap.conformance.test.common.RawURIRequest(
                "/ip/[::]",
                unescaped,
                false
            )
        );
        /* Extra query parameter. */
        String ip = (exists.size() > 0) ? exists.get(0) : "1.2.3.4";
        tests.add(
            new BasicRequest(
                HttpStatus.SC_BAD_REQUEST,
                "/ip/" + ip + "?asdf=zxcv",
                "ip.extra-query-parameter",
                true,
                EXTRA_QUERY_PARAM
            )
        );
    }

    private static void addAutnumTests(final Specification s,
                                       final List<Test> tests) {
        ObjectClass ocAn = s.getObjectClass("autnum");
        if ((ocAn == null) || !ocAn.isSupported()) {
            return;
        }

        tests.add(new net.apnic.rdap.conformance.test.autnum.BadRequest());
        List<String> exists = ocAn.getExists();
        for (String e : exists) {
            tests.add(
                new net.apnic.rdap.conformance.test.autnum.Standard(e)
            );
        }
        List<String> notExists = ocAn.getNotExists();
        for (String e : notExists) {
            tests.add(new NotFound("/autnum/" + e));
        }
        ObjectTest std =
            new net.apnic.rdap.conformance.test.autnum.Standard();
        List<String> redirects = ocAn.getRedirects();
        for (String e : redirects) {
            tests.add(new Redirect(std, "/autnum/" + e, "autnum.redirect"));
        }
        /* Extra query parameter. */
        String autnum = (exists.size() > 0) ? exists.get(0) : "1234";
        tests.add(
            new BasicRequest(
                HttpStatus.SC_BAD_REQUEST,
                "/autnum/" + autnum + "?asdf=zxcv",
                "autnum.extra-query-parameter",
                true,
                EXTRA_QUERY_PARAM
            )
        );
    }

    private static void addNameserverTests(final Specification s,
                                           final List<Test> tests)
            throws Exception {
        ObjectClass ocNs = s.getObjectClass("nameserver");
        if ((ocNs == null) || !ocNs.isSupported()) {
            return;
        }

        tests.add(
            new net.apnic.rdap.conformance.test.nameserver.BadRequest()
        );
        List<String> exists = ocNs.getExists();
        for (String e : exists) {
            tests.add(
                new net.apnic.rdap.conformance.test.nameserver.Standard(e)
            );
        }
        List<String> notExists = ocNs.getNotExists();
        for (String e : notExists) {
            tests.add(new NotFound("/nameserver/" + e));
        }
        ObjectTest std =
            new net.apnic.rdap.conformance.test.nameserver.Standard();
        List<String> redirects = ocNs.getRedirects();
        for (String e : redirects) {
            tests.add(
                new Redirect(std, "/nameserver/" + e, "nameserver.redirect")
            );
        }
        /* Extra query parameter. */
        String nameserver =
            (exists.size() > 0) ? exists.get(0) : "example.com";
        tests.add(
            new BasicRequest(
                HttpStatus.SC_BAD_REQUEST,
                "/nameserver/" + nameserver + "?asdf=zxcv",
                "nameserver.extra-query-parameter",
                true,
                EXTRA_QUERY_PARAM
            )
        );
        addSearchTests(
            tests,
            ocNs,
            new net.apnic.rdap.conformance.attributetest.Nameserver(true),
            "nameservers",
            "nameserver.search",
            "nameserverSearchResults"
        );
    }

    private static void addEntityTests(final Specification s,
                                       final List<Test> tests)
            throws Exception {
        ObjectClass ocEn = s.getObjectClass("entity");
        if ((ocEn == null) || !ocEn.isSupported()) {
            return;
        }

        List<String> exists = ocEn.getExists();
        for (String e : exists) {
            tests.add(
                new net.apnic.rdap.conformance.test.entity.Standard(e)
            );
        }
        List<String> notExists = ocEn.getNotExists();
        for (String e : notExists) {
            tests.add(new NotFound("/entity/" + e));
        }
        /* That the entity handle happens to be an IP address should
           not cause a 400 to be returned. */
        tests.add(
            new BasicRequest(
                HttpStatus.SC_BAD_REQUEST,
                "/entity/1.2.3.4",
                null,
                true,
                getDocRefProto("rfc7482", "3.1.5",
                               "entity.not-bad-request")
            )
        );
        /* Extra query parameter. */
        String entity = (exists.size() > 0) ? exists.get(0) : "asdf";
        tests.add(
            new BasicRequest(
                HttpStatus.SC_BAD_REQUEST,
                "/entity/" + entity + "?asdf=zxcv",
                "entity.extra-query-parameter",
                true,
                EXTRA_QUERY_PARAM
            )
        );
        addSearchTests(
            tests,
            ocEn,
            new net.apnic.rdap.conformance.attributetest.Entity(),
            "entities",
            "entity.search",
            "entitySearchResults"
        );
    }

    private static void addDomainTests(final Specification s,
                                       final List<Test> tests)
            throws Exception {
        ObjectClass ocDom = s.getObjectClass("domain");
        if ((ocDom == null) || !ocDom.isSupported()) {
            return;
        }
        tests.add(new net.apnic.rdap.conformance.test.domain.BadRequest());
        List<String> exists = ocDom.getExists();
        for (String e : exists) {
            tests.add(new
                net.apnic.rdap.conformance.test.domain.Standard(e)
            );
        }
        List<String> notExists = ocDom.getNotExists();
        for (String e : notExists) {
            tests.add(new NotFound("/domain/" + e));
        }
        List<String> redirects = ocDom.getRedirects();
        for (String e : redirects) {
            tests.add(
                new Redirect(
                    new net.apnic.rdap.conformance.test.domain.Standard(),
                    "/domain/" + e, "domain.redirect"
                )
            );
        }
        /* Number registries should not return 400 on forward
         * domains. */
        tests.add(
            new BasicRequest(
                HttpStatus.SC_BAD_REQUEST,
                "/domain/example.com",
                null,
                true,
                getDocRefProto("rfc7482", "3.1.3",
                               "domain.not-bad-request")
            )
        );
        /* As above, but for name registries and reverse domains. */
        tests.add(
            new BasicRequest(
                HttpStatus.SC_BAD_REQUEST,
                "/domain/202.in-addr.arpa",
                null,
                true,
                getDocRefProto("rfc7482", "3.1.3",
                               "domain.not-bad-request")
            )
        );
        /* Extra query parameter. */
        String domain = (exists.size() > 0) ? exists.get(0) : "example.com";
        tests.add(
            new BasicRequest(
                HttpStatus.SC_BAD_REQUEST,
                "/domain/" + domain + "?asdf=zxcv",
                "domain.extra-query-parameter",
                true,
                EXTRA_QUERY_PARAM
            )
        );
        addSearchTests(
            tests,
            ocDom,
            new net.apnic.rdap.conformance.attributetest.Domain(true),
            "domains",
            "domain.search",
            "domainSearchResults"
        );
    }

    private static List<CloseableHttpAsyncClient> chacs =
        new ArrayList<CloseableHttpAsyncClient>();

    private static Context createContext(final Specification s,
                                         final RateLimiter rateLimiter,
                                         final ExecutorService executorService,
                                         final AtomicInteger testsRunning) {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, TRUST_MANAGER,
                            new java.security.SecureRandom());
        } catch (Exception e) {
            System.err.println(e.toString());
            System.exit(EX_SOFTWARE);
        }

        CloseableHttpAsyncClient hc =
            HttpAsyncClients.custom()
                            .setSSLContext(sslContext)
                            .build();
        hc.start();
        chacs.add(hc);

        Context c = new Context();
        c.setHttpClient(hc);
        c.setSpecification(s);
        c.setRateLimiter(rateLimiter);
        c.setExecutorService(executorService);
        c.setTestsRunning(testsRunning);
        if (s.getAcceptContentType() != null) {
            c.setContentType(s.getAcceptContentType());
        }

        return c;
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.Exception if any.
     */
    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java -jar "
                               + getJarName()
                               + " <configuration-path>");
            System.exit(EX_USAGE);
        }

        String path = args[0];
        Specification s = null;
        try {
            s = Specification.fromPath(path);
        } catch (Exception e) {
            System.err.println("Unable to load specification "
                               + "path (" + path + "): "
                               + e.toString());
            System.exit(EX_NOINPUT);
        }
        if (s == null) {
            System.err.println("Specification (" + path + ") is empty.");
            System.exit(EX_NOINPUT);
        }

        RateLimiter rateLimiter =
            (s.getRequestsPerSecond() > 0)
                ? RateLimiter.create(s.getRequestsPerSecond())
                : null;

        List<Test> tests = new ArrayList();

        /* For now, the non-RDAP-specific tests are disabled. These
         * are fairly niche, and in many cases can't easily be fixed
         * by implementers anyway. See e.g.
         * https://bugs.eclipse.org/bugs/show_bug.cgi?id=414636. */
        // addNonRdapTests(c, tests);

        /* Certain servers do not return a valid response when the
         * application/rdap+json content type is set in the Accept
         * header, but are still able to return valid JSON. Rather
         * than not testing the responses at all, allow for the
         * supported content type to be set in the configuration, and
         * add a result at the beginning indicating this failure. */
        if (s.getAcceptContentType() != null) {
            Result ctres = new Result();
            ctres.setTestName("common.rdap-specific-content-type");
            ctres.setDocument("rfc7480");
            ctres.setReference("4.1");
            ctres.setStatus(Result.Status.Failure);
            ctres.setInfo("not supported by server in accept header");
            System.out.println(ctres.toString());
        }

        addUnsupportedQueryTypeTests(s, tests);
        addIpTests(s, tests);
        addAutnumTests(s, tests);
        addNameserverTests(s, tests);
        addEntityTests(s, tests);
        addDomainTests(s, tests);

        tests.add(new net.apnic.rdap.conformance.test.help.Standard());
        AtomicInteger testsRunning = new AtomicInteger(0);

        final ExecutorService executorService =
            Executors.newFixedThreadPool(Runtime.getRuntime()
                                                .availableProcessors());
        for (final Test t : tests) {
            final Context context =
                createContext(s, rateLimiter, executorService, testsRunning);
            context.submitTest(t);
        }

        /* application/json content-type. This is deliberately using
         * an invalid status code with inverted sense, because so long
         * as the request is 'successful', it's fine. */
        Result ctres = new Result();
        ctres.setTestName("common.application-json");
        ctres.setDocument("rfc7480");
        ctres.setReference("4.2");
        final Test test =
            new BasicRequest(
                0,
                "/domain/example.com",
                "common.application-json",
                true,
                ctres
            );
        final Context context =
            createContext(s, rateLimiter, executorService, testsRunning);
        context.setContentType("application/json");
        context.submitTest(test);

        while (true) {
            if (testsRunning.get() != 0) {
                Thread.sleep(TESTS_RUNNING_CHECK_DELAY_MS);
            } else {
                break;
            }
        }
        for (CloseableHttpAsyncClient chac : chacs) {
            chac.close();
        }

        executorService.shutdown();
    }
}
