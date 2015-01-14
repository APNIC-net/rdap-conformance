package net.apnic.rdap.conformance.test.common;

import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;
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
 * @version 0.3-SNAPSHOT
 */
public final class Redirect implements Test {
    private String urlPath;
    private String testName;
    private ObjectTest resultTest;
    private Context context = null;
    private HttpResponse httpResponse = null;
    private Throwable throwable = null;

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
    public void setContext(final Context c) {
        context = c;
    }

    /** {@inheritDoc} */
    public void setResponse(final HttpResponse hr) {
        httpResponse = hr;
    }

    /** {@inheritDoc} */
    public void setError(final Throwable t) {
        throwable = t;
    }

    /** {@inheritDoc} */
    public HttpRequest getRequest() {
        String path = context.getSpecification().getBaseUrl() + urlPath;
        return Utils.httpGetRequest(context, path, false);
    }

    /** {@inheritDoc} */
    public boolean run() {
        List<Result> results = context.getResults();
        String path = context.getSpecification().getBaseUrl() + urlPath;

        Result proto = new Result(Status.Notification, path,
                                  testName,
                                  "", "",
                                  "draft-ietf-weirds-using-http-15",
                                  "5.2");
        if (httpResponse == null) {
            proto.setCode("response");
            proto.setStatus(Status.Failure);
            proto.setInfo((throwable != null) ? throwable.toString() : "");
            context.addResult(proto);
            return false;
        }

        Result r = new Result(proto);
        r.setCode("response");
        r.setStatus(Status.Success);
        results.add(r);

        ResponseTest sc =
            new StatusCode(
                Sets.newHashSet(HttpStatus.SC_TEMPORARY_REDIRECT,
                                HttpStatus.SC_MOVED_PERMANENTLY,
                                HttpStatus.SC_MOVED_TEMPORARILY,
                                HttpStatus.SC_SEE_OTHER)
            );
        boolean scres = sc.run(context, proto, httpResponse);
        if (!scres) {
            return false;
        }

        if (resultTest != null) {
            String location = httpResponse.getFirstHeader("Location")
                                          .getValue();
            resultTest.setUrl(location);
            context.submitTest(resultTest);
        }
        return true;
    }
}
