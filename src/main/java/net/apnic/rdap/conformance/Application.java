package net.apnic.rdap.conformance;

import java.io.FileNotFoundException;
import java.util.List;
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

import net.apnic.rdap.conformance.Specification;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.test.domain.BadRequest;
import net.apnic.rdap.conformance.test.domain.Standard;
import net.apnic.rdap.conformance.test.common.RawURIRequest;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.specification.ObjectClass;

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

    public static void main(String[] args)
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
        /* For storing tests that will be re-run with an Accept
         * content-type of application/json. */
        List<Test> ct_tests = new ArrayList();

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

        ObjectClass oc_ip = s.getObjectClass("ip");
        if ((oc_ip != null) && (oc_ip.isSupported())) {
            tests.add(new net.apnic.rdap.conformance.test.ip.BadRequest());
            List<String> exists = oc_ip.getExists();
            for (String e : exists) {
                tests.add(new net.apnic.rdap.conformance.test.ip.Standard(e));
            }
            if (exists.size() >= 1) {
                ct_tests.add(new
                    net.apnic.rdap.conformance.test.ip.Standard(
                        exists.get(0)
                    )
                );
            }
            List<String> not_exists = oc_ip.getNotExists();
            for (String e : not_exists) {
                tests.add(new net.apnic.rdap.conformance.test.common.NotFound(
                            "/ip/" + e
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
                              "ip.not-bad-request",
                              true
                      ));
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
            if (exists.size() >= 1) {
                ct_tests.add(new
                    net.apnic.rdap.conformance.test.domain.Standard(
                        exists.get(0)
                    )
                );
            }
            List<String> not_exists = oc_dom.getNotExists();
            for (String e : not_exists) {
                tests.add(new net.apnic.rdap.conformance.test.common.NotFound(
                            "/domain/" + e
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
        }

        for (Test t : tests) {
            t.run(c);
        }

        c.setContentType("application/json");
        for (Test ct : ct_tests) {
            ct.run(c);
        }

        c.flushResults();
    }
}
