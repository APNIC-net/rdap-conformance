package net.apnic.rdap.conformance;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.commons.lang.SerializationUtils;

import net.apnic.rdap.conformance.Specification;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.test.domain.BadRequest;
import net.apnic.rdap.conformance.test.domain.Standard;
import net.apnic.rdap.conformance.test.common.RawURIRequest;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.specification.ObjectClass;
import net.apnic.rdap.conformance.specification.ObjectClassSearch;

class Application
{
    private static String getJarName()
    {
        String jar_name = "rdap-conformance.jar";
        try {
            jar_name =
                new java.io.File(Context.class.getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .getPath())
                        .getName();
        } catch (Exception e) {}
        return jar_name;
    }

    private static void runSearchTests(List<Test> tests,
                                       ObjectClass oc,
                                       SearchTest st,
                                       String prefix,
                                       String test_name,
                                       String search_key)
        throws Exception
    {
        ObjectClassSearch ocs = oc.getObjectClassSearch();
        if ((ocs != null) && (ocs.isSupported())) {
            Map<String, List<String>> values = ocs.getValues();
            for (Map.Entry<String, List<String>> entry : values.entrySet()) {
                String key = entry.getKey();
                List<String> key_values = entry.getValue();
                for (String key_value : key_values) {
                    tests.add(
                        new net.apnic.rdap.conformance.test.common.Search(
                            (SearchTest) SerializationUtils.clone(st),
                            prefix,
                            key,
                            key_value,
                            test_name,
                            search_key
                        )
                    );
                }
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        if (args.length != 1) {
            System.out.println("Usage: java -jar " +
                               getJarName() +
                               " <configuration-path>");
            System.exit(10);
        }

        String path = args[0];
        Specification s = null;
        try {
            s = Specification.fromPath(path);
        } catch (Exception e) {
            System.err.println("Unable to load specification " +
                               "path (" + path + "): " +
                               e.toString());
            System.exit(1);
        }
        if (s == null) {
            System.err.println("Specification (" + path + ") is empty.");
            System.exit(1);
        }

        final TrustManager[] trust_all_certs =
            new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(final X509Certificate[] chain,
                                               final String authType ) { }
                @Override
                public void checkServerTrusted(final X509Certificate[] chain,
                                               final String authType ) { }
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            } };

        SSLContext ssl_context = null;
        try {
            ssl_context = SSLContext.getInstance( "SSL" );
            ssl_context.init(null, trust_all_certs,
                             new java.security.SecureRandom());
        } catch (Exception e) {
            System.err.println(e.toString());
            System.exit(1);
        }

        HttpClient hc = HttpClientBuilder.create()
                                         .setSslcontext(ssl_context)
                                         .build();
        Context c = new Context();
        c.setHttpClient(hc);
        c.setSpecification(s);

        List<String> object_types = new ArrayList<String>(
            Arrays.asList("ip", "nameserver", "autnum",
                          "entity", "domain")
        );

        List<Test> tests = new ArrayList();

        for (String object_type : object_types) {
            ObjectClass oc = s.getObjectClass(object_type);
            if ((oc != null)
                    && (!s.getObjectClass(object_type).isSupported())) {
                tests.add(new net.apnic.rdap.conformance.test.common.NotFound(
                            "/" + object_type)
                         );
            }
        }

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
        tests.add(
            new net.apnic.rdap.conformance.test.common.RawURIRequest(
                c.getSpecification().getBaseUrl() + "/domain/example.com",
                absolute,
                true
            )
        );

        /* application/json content-type. This is deliberately using
         * an invalid status code with inverted sense, because so long
         * as the request is 'successful', it's fine. */
        c.setContentType("application/json");
        tests.add(new net.apnic.rdap.conformance.test.common.BasicRequest(
                          0,
                          "/domain/example.com",
                          "common.application-json",
                          true
                  ));
        c.setContentType(null);

        ObjectClass oc_ip = s.getObjectClass("ip");
        if ((oc_ip != null) && (oc_ip.isSupported())) {
            tests.add(new net.apnic.rdap.conformance.test.ip.BadRequest());
            List<String> exists = oc_ip.getExists();
            for (String e : exists) {
                tests.add(new net.apnic.rdap.conformance.test.ip.Standard(e));
            }
            List<String> not_exists = oc_ip.getNotExists();
            for (String e : not_exists) {
                tests.add(new net.apnic.rdap.conformance.test.common.NotFound(
                            "/ip/" + e
                         ));
            }
            List<String> redirects = oc_ip.getRedirects();
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
                              true
                      ));
        }

        ObjectClass oc_an = s.getObjectClass("autnum");
        if ((oc_an != null) && (oc_an.isSupported())) {
            tests.add(new net.apnic.rdap.conformance.test.autnum.BadRequest());
            List<String> exists = oc_an.getExists();
            for (String e : exists) {
                tests.add(
                    new net.apnic.rdap.conformance.test.autnum.Standard(e)
                );
            }
            List<String> not_exists = oc_an.getNotExists();
            for (String e : not_exists) {
                tests.add(new net.apnic.rdap.conformance.test.common.NotFound(
                            "/autnum/" + e
                         ));
            }
            List<String> redirects = oc_an.getRedirects();
            for (String e : redirects) {
                tests.add(new net.apnic.rdap.conformance.test.common.Redirect(
                            new net.apnic.rdap.conformance.test.autnum.Standard(),
                            "/autnum/" + e, "autnum.redirect"
                          ));
            }
            /* Extra query parameter. */
            tests.add(new net.apnic.rdap.conformance.test.common.BasicRequest(
                              HttpStatus.SC_BAD_REQUEST,
                              "/autnum/1234?asdf=zxcv",
                              "autnum.extra-query-parameter",
                              true
                      ));
        }

        ObjectClass oc_ns = s.getObjectClass("nameserver");
        if ((oc_ns != null) && (oc_ns.isSupported())) {
            tests.add(
                new net.apnic.rdap.conformance.test.nameserver.BadRequest()
            );
            List<String> exists = oc_ns.getExists();
            for (String e : exists) {
                tests.add(
                    new net.apnic.rdap.conformance.test.nameserver.Standard(e)
                );
            }
            List<String> not_exists = oc_ns.getNotExists();
            for (String e : not_exists) {
                tests.add(new net.apnic.rdap.conformance.test.common.NotFound(
                            "/nameserver/" + e
                         ));
            }
            List<String> redirects = oc_ns.getRedirects();
            for (String e : redirects) {
                tests.add(new net.apnic.rdap.conformance.test.common.Redirect(
                            new net.apnic.rdap.conformance.test.nameserver.Standard(),
                            "/nameserver/" + e, "nameserver.redirect"
                          ));
            }
            /* Extra query parameter. */
            tests.add(new net.apnic.rdap.conformance.test.common.BasicRequest(
                              HttpStatus.SC_BAD_REQUEST,
                              "/nameserver/example.com?asdf=zxcv",
                              "nameserver.extra-query-parameter",
                              true
                      ));
            runSearchTests(tests, oc_ns,
                           new net.apnic.rdap.conformance.attributetest.Nameserver(true),
                           "nameservers", "nameserver.search",
                           "nameserverSearchResults");
        }

        ObjectClass oc_en = s.getObjectClass("entity");
        if ((oc_en != null) && (oc_en.isSupported())) {
            List<String> exists = oc_en.getExists();
            for (String e : exists) {
                tests.add(
                    new net.apnic.rdap.conformance.test.entity.Standard(e)
                );
            }
            List<String> not_exists = oc_en.getNotExists();
            for (String e : not_exists) {
                tests.add(new net.apnic.rdap.conformance.test.common.NotFound(
                           "/entity/" + e
                          ));
            }
            /* That the entity handle happens to be an IP address should
               not cause a 400 to be returned. */
            tests.add(new net.apnic.rdap.conformance.test.common.BasicRequest(
                              HttpStatus.SC_BAD_REQUEST,
                              "/entity/1.2.3.4",
                              "entity.not-bad-request",
                              true
                      ));
            /* Extra query parameter. */
            tests.add(new net.apnic.rdap.conformance.test.common.BasicRequest(
                              HttpStatus.SC_BAD_REQUEST,
                              "/entity/asdf?asdf=zxcv",
                              "entity.extra-query-parameter",
                              true
                      ));
            runSearchTests(tests, oc_en,
                           new net.apnic.rdap.conformance.attributetest.Entity(),
                           "entities", "entity.search", "entitySearchResults");
        }

        ObjectClass oc_dom = s.getObjectClass("domain");
        if ((oc_dom != null) && (oc_dom.isSupported())) {
            tests.add(new net.apnic.rdap.conformance.test.domain.BadRequest());
            List<String> exists = oc_dom.getExists();
            for (String e : exists) {
                tests.add(new
                    net.apnic.rdap.conformance.test.domain.Standard(e)
                );
            }
            List<String> not_exists = oc_dom.getNotExists();
            for (String e : not_exists) {
                tests.add(new net.apnic.rdap.conformance.test.common.NotFound(
                            "/domain/" + e
                         ));
            }
            List<String> redirects = oc_dom.getRedirects();
            for (String e : redirects) {
                tests.add(new net.apnic.rdap.conformance.test.common.Redirect(
                            new net.apnic.rdap.conformance.test.domain.Standard(),
                            "/domain/" + e, "domain.redirect"
                          ));
            }
            /* Number registries should not return 400 on forward
             * domains. */
            tests.add(new net.apnic.rdap.conformance.test.common.BasicRequest(
                              HttpStatus.SC_BAD_REQUEST,
                              "/domain/example.com",
                              "domain.not-bad-request",
                              true
                      ));
            /* As above, but for name registries and reverse domains. */
            tests.add(new net.apnic.rdap.conformance.test.common.BasicRequest(
                              HttpStatus.SC_BAD_REQUEST,
                              "/domain/202.in-addr.arpa",
                              "domain.not-bad-request",
                              true
                      ));
            /* Extra query parameter. */
            tests.add(new net.apnic.rdap.conformance.test.common.BasicRequest(
                              HttpStatus.SC_BAD_REQUEST,
                              "/domain/example.com?asdf=zxcv",
                              "domain.extra-query-parameter",
                              true
                      ));
            runSearchTests(tests, oc_dom,
                           new net.apnic.rdap.conformance.attributetest.Domain(true),
                           "domains", "domain.search", "domainSearchResults");
        }

        for (Test t : tests) {
            t.run(c);
        }

        c.flushResults();
    }
}
