package net.apnic.rdap.conformance.test.common;

import java.io.IOException;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.HttpStatus;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;

import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ObjectTest;
import net.apnic.rdap.conformance.ResponseTest;
import net.apnic.rdap.conformance.responsetest.StatusCode;
import net.apnic.rdap.conformance.Utils;

/**
 * <p>Redirect class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
 */
public final class Redirect implements Test {
    private String urlPath;
    private String testName;
    private ObjectTest resultTest;

    /**
     * <p>Constructor for Redirect.</p>
     *
     * @param argUrlPath a {@link java.lang.String} object.
     * @param argTestName a {@link java.lang.String} object.
     */
    public Redirect(final String argUrlPath,
                    final String argTestName) {
        urlPath    = argUrlPath;
        testName   = argTestName;

        if (testName == null) {
            testName = "common.redirect";
        }
    }

    /**
     * <p>Constructor for Redirect.</p>
     *
     * @param argResultTest a {@link net.apnic.rdap.conformance.ObjectTest} object.
     * @param argUrlPath a {@link java.lang.String} object.
     * @param argTestName a {@link java.lang.String} object.
     */
    public Redirect(final ObjectTest argResultTest,
                    final String argUrlPath,
                    final String argTestName) {
        resultTest = argResultTest;
        urlPath    = argUrlPath;
        testName   = argTestName;

        if (testName == null) {
            testName = "common.redirect";
        }
    }

    /** {@inheritDoc} */
    public boolean run(final Context context) {
        List<Result> results = context.getResults();

        String bu = context.getSpecification().getBaseUrl();
        String path = bu + urlPath;

        Result proto = new Result(Status.Notification, path,
                                  testName,
                                  "", "",
                                  "draft-ietf-weirds-using-http-08",
                                  "5.2");
        Result r = new Result(proto);
        r.setCode("response");

        HttpRequestBase request = null;
        HttpResponse response = null;
        try {
            request = Utils.httpGetRequest(context, path, false);
            response = context.executeRequest(request);
        } catch (IOException e) {
            r.setStatus(Status.Failure);
            r.setInfo(e.toString());
            results.add(r);
            if (request != null) {
                request.releaseConnection();
            }
            return false;
        }

        r.setStatus(Status.Success);
        results.add(r);

        ResponseTest sc =
            new StatusCode(
                Sets.newHashSet(HttpStatus.SC_TEMPORARY_REDIRECT,
                                HttpStatus.SC_MOVED_PERMANENTLY,
                                HttpStatus.SC_MOVED_TEMPORARILY,
                                HttpStatus.SC_SEE_OTHER)
            );
        boolean scres = sc.run(context, proto, response);
        request.releaseConnection();
        if (!scres) {
            return false;
        }

        if (resultTest != null) {
            String location = response.getFirstHeader("Location")
                                      .getValue();
            resultTest.setUrl(location);
            return resultTest.run(context);
        } else {
            return true;
        }
    }
}
