package net.apnic.rdap.conformance;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import com.google.common.util.concurrent.RateLimiter;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.commons.lang.SerializationUtils;

import net.apnic.rdap.conformance.specification.ObjectClass;
import net.apnic.rdap.conformance.specification.ObjectClassSearch;

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

    private static final TrustManager[] trustAllCerts =
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

    private static void runSearchTests(final List<Test> tests,
                                       final ObjectClass oc,
                                       final SearchTest st,
                                       final String prefix,
                                       final String testName,
                                       final String searchKey)
            throws Exception {
        ObjectClassSearch ocs = oc.getObjectClassSearch();
        if ((ocs != null) && (ocs.isSupported())) {
            Map<String, List<String>> values = ocs.getValues();
            for (Map.Entry<String, List<String>> entry : values.entrySet()) {
                String key = entry.getKey();
                List<String> keyValues = entry.getValue();
                for (String keyValue : keyValues) {
                    tests.add(
                        new net.apnic.rdap.conformance.test.common.Search(
                            (SearchTest) SerializationUtils.clone(st),
                            prefix,
                            key,
                            keyValue,
                            testName,
                            searchKey
                        )
                    );
                }
            }
        }
    }

    private static Result getDocRefProto(String document,
                                         String reference,
                                         String testName)
    {
        Result proto = new Result();
        proto.setDocument(document);
        proto.setReference(reference);
        proto.setTestName(testName);
        return proto;
    }

    private static void runNonRdapTests(Context c, List<Test> tests)
    {
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

    private static Context createContext(Specification s,
                                         RateLimiter rateLimiter) {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts,
                             new java.security.SecureRandom());
        } catch (Exception e) {
            System.err.println(e.toString());
            System.exit(EX_SOFTWARE);
        }

        HttpClient hc = HttpClientBuilder.create()
                                         .setSslcontext(sslContext)
                                         .build();
        Context c = new Context();
        c.setHttpClient(hc);
        c.setSpecification(s);
        c.setRateLimiter(rateLimiter);

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

        List<String> objectTypes = new ArrayList<String>(
            Arrays.asList("ip", "nameserver", "autnum",
                          "entity", "domain")
        );

        List<Test> tests = new ArrayList();

        /* For now, the non-RDAP-specific tests are disabled. These
         * are fairly niche, and in many cases can't easily be fixed
         * by implementers anyway. See e.g.
         * https://bugs.eclipse.org/bugs/show_bug.cgi?id=414636. */
        // runNonRdapTests(c, tests);

        /* Unsupported query types. */
        for (String objectType : objectTypes) {
            Result unsupported = new Result();
            unsupported.setTestName("common.unsupported");
            unsupported.setDocument("draft-ietf-weirds-using-http-08");
            unsupported.setReference("5.4");
            ObjectClass oc = s.getObjectClass(objectType);
            if ((oc == null)
                    || (!s.getObjectClass(objectType).isSupported())) {
                tests.add(
                    new net.apnic.rdap.conformance.test.common.BasicRequest(
                        HttpStatus.SC_BAD_REQUEST,
                        "/" + objectType + "/1.2.3.4",
                        "common.unsupported",
                        false,
                        unsupported
                    ));
            }
        }

        Result extraQueryParam =
            getDocRefProto("draft-ietf-weirds-using-http-08", "4.2",
                           "common.extra-query-parameter");

        ObjectClass ocIp = s.getObjectClass("ip");
        if ((ocIp != null) && (ocIp.isSupported())) {
            tests.add(new net.apnic.rdap.conformance.test.ip.BadRequest());
            List<String> exists = ocIp.getExists();
            for (String e : exists) {
                tests.add(new net.apnic.rdap.conformance.test.ip.Standard(e));
            }
            List<String> notExists = ocIp.getNotExists();
            for (String e : notExists) {
                tests.add(new net.apnic.rdap.conformance.test.common.NotFound(
                            "/ip/" + e
                          ));
            }
            List<String> redirects = ocIp.getRedirects();
            for (String e : redirects) {
                tests.add(new net.apnic.rdap.conformance.test.common.Redirect(
                            new net.apnic.rdap.conformance.test.ip.Standard(),
                            "/ip/" + e, "ip.redirect"
                          ));
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
            tests.add(new net.apnic.rdap.conformance.test.common.BasicRequest(
                              HttpStatus.SC_BAD_REQUEST,
                              "/ip/1.2.3.4?asdf=zxcv",
                              "ip.extra-query-parameter",
                              true,
                              extraQueryParam
                      ));
        }

        ObjectClass ocAn = s.getObjectClass("autnum");
        if ((ocAn != null) && (ocAn.isSupported())) {
            tests.add(new net.apnic.rdap.conformance.test.autnum.BadRequest());
            List<String> exists = ocAn.getExists();
            for (String e : exists) {
                tests.add(
                    new net.apnic.rdap.conformance.test.autnum.Standard(e)
                );
            }
            List<String> notExists = ocAn.getNotExists();
            for (String e : notExists) {
                tests.add(new net.apnic.rdap.conformance.test.common.NotFound(
                            "/autnum/" + e
                          ));
            }
            ObjectTest std =
                new net.apnic.rdap.conformance.test.autnum.Standard();
            List<String> redirects = ocAn.getRedirects();
            for (String e : redirects) {
                tests.add(
                    new net.apnic.rdap.conformance.test.common.Redirect(
                        std,
                        "/autnum/" + e, "autnum.redirect"
                    )
                );
            }
            /* Extra query parameter. */
            tests.add(new net.apnic.rdap.conformance.test.common.BasicRequest(
                              HttpStatus.SC_BAD_REQUEST,
                              "/autnum/1234?asdf=zxcv",
                              "autnum.extra-query-parameter",
                              true,
                              extraQueryParam
                      ));
        }

        ObjectClass ocNs = s.getObjectClass("nameserver");
        if ((ocNs != null) && (ocNs.isSupported())) {
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
                tests.add(new net.apnic.rdap.conformance.test.common.NotFound(
                            "/nameserver/" + e
                          ));
            }
            ObjectTest std =
                new net.apnic.rdap.conformance.test.nameserver.Standard();
            List<String> redirects = ocNs.getRedirects();
            for (String e : redirects) {
                tests.add(
                    new net.apnic.rdap.conformance.test.common.Redirect(
                        std,
                        "/nameserver/" + e, "nameserver.redirect"
                    )
                );
            }
            /* Extra query parameter. */
            tests.add(
                new net.apnic.rdap.conformance.test.common.BasicRequest(
                        HttpStatus.SC_BAD_REQUEST,
                        "/nameserver/example.com?asdf=zxcv",
                        "nameserver.extra-query-parameter",
                        true,
                        extraQueryParam
                )
            );
            runSearchTests(
                tests,
                ocNs,
                new net.apnic.rdap.conformance.attributetest.Nameserver(true),
                "nameservers",
                "nameserver.search",
                "nameserverSearchResults"
            );
        }

        ObjectClass ocEn = s.getObjectClass("entity");
        if ((ocEn != null) && (ocEn.isSupported())) {
            List<String> exists = ocEn.getExists();
            for (String e : exists) {
                tests.add(
                    new net.apnic.rdap.conformance.test.entity.Standard(e)
                );
            }
            List<String> notExists = ocEn.getNotExists();
            for (String e : notExists) {
                tests.add(new net.apnic.rdap.conformance.test.common.NotFound(
                           "/entity/" + e
                          ));
            }
            /* That the entity handle happens to be an IP address should
               not cause a 400 to be returned. */
            tests.add(
                new net.apnic.rdap.conformance.test.common.BasicRequest(
                    HttpStatus.SC_BAD_REQUEST,
                    "/entity/1.2.3.4",
                    null,
                    true,
                    getDocRefProto("draft-ietf-weirds-rdap-query-10", "3.1.5",
                                   "entity.not-bad-request")
                )
            );
            /* Extra query parameter. */
            tests.add(
                new net.apnic.rdap.conformance.test.common.BasicRequest(
                    HttpStatus.SC_BAD_REQUEST,
                    "/entity/asdf?asdf=zxcv",
                    "entity.extra-query-parameter",
                    true,
                    extraQueryParam
                )
            );
            runSearchTests(
                tests,
                ocEn,
                new net.apnic.rdap.conformance.attributetest.Entity(),
                "entities",
                "entity.search",
                "entitySearchResults"
            );
        }

        ObjectClass ocDom = s.getObjectClass("domain");
        if ((ocDom != null) && (ocDom.isSupported())) {
            tests.add(new net.apnic.rdap.conformance.test.domain.BadRequest());
            List<String> exists = ocDom.getExists();
            for (String e : exists) {
                tests.add(new
                    net.apnic.rdap.conformance.test.domain.Standard(e)
                );
            }
            List<String> notExists = ocDom.getNotExists();
            for (String e : notExists) {
                tests.add(
                    new net.apnic.rdap.conformance.test.common.NotFound(
                        "/domain/" + e
                    )
                );
            }
            List<String> redirects = ocDom.getRedirects();
            for (String e : redirects) {
                tests.add(
                    new net.apnic.rdap.conformance.test.common.Redirect(
                        new net.apnic.rdap.conformance.test.domain.Standard(),
                        "/domain/" + e, "domain.redirect"
                    )
                );
            }
            /* Number registries should not return 400 on forward
             * domains. */
            tests.add(
                new net.apnic.rdap.conformance.test.common.BasicRequest(
                    HttpStatus.SC_BAD_REQUEST,
                    "/domain/example.com",
                    null,
                    true,
                    getDocRefProto("draft-ietf-weirds-rdap-query-10", "3.1.3",
                                   "domain.not-bad-request")
                )
            );
            /* As above, but for name registries and reverse domains. */
            tests.add(
                new net.apnic.rdap.conformance.test.common.BasicRequest(
                    HttpStatus.SC_BAD_REQUEST,
                    "/domain/202.in-addr.arpa",
                    null,
                    true,
                    getDocRefProto("draft-ietf-weirds-rdap-query-10", "3.1.3",
                                   "domain.not-bad-request")
                )
            );
            /* Extra query parameter. */
            tests.add(
                new net.apnic.rdap.conformance.test.common.BasicRequest(
                    HttpStatus.SC_BAD_REQUEST,
                    "/domain/example.com?asdf=zxcv",
                    "domain.extra-query-parameter",
                    true,
                    extraQueryParam
                )
            );
            runSearchTests(
                tests,
                ocDom,
                new net.apnic.rdap.conformance.attributetest.Domain(true),
                "domains",
                "domain.search",
                "domainSearchResults"
            );
        }

        /* Eight is a completely arbitrary figure, here. todo later:
         * this should be using asynchronous IO for the HTTP requests. */
        final ExecutorService executorService =
            Executors.newFixedThreadPool(s.getAllowConcurrency() ? 8 : 1);
        for (final Test t : tests) {
            final Context context = createContext(s, rateLimiter);
            executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        t.run(context);
                        synchronized (System.out) {
                            context.flushResults();
                        }
                    }
                }
            );
        }

        /* application/json content-type. This is deliberately using
         * an invalid status code with inverted sense, because so long
         * as the request is 'successful', it's fine. */
        Result ctres = new Result();
        ctres.setTestName("common.application-json");
        ctres.setDocument("draft-ietf-weirds-using-http-08");
        ctres.setReference("4.1");
        final Test test =
            new net.apnic.rdap.conformance.test.common.BasicRequest(
                0,
                "/domain/example.com",
                "common.application-json",
                true,
                ctres
            );
        final Context context = createContext(s, rateLimiter);
        executorService.submit(
            new Runnable() {
                @Override
                public void run() {
                    context.setContentType("application/json");
                    test.run(context);
                    context.setContentType(null);
                    synchronized (System.out) {
                        context.flushResults();
                    }
                }
            }
        );

        executorService.shutdown();
    }
}
