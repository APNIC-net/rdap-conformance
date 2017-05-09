package net.apnic.rdap.conformance.test.common;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;
import org.apache.http.Header;
import org.apache.http.HttpStatus;

import com.google.gson.Gson;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ResponseTest;
import net.apnic.rdap.conformance.responsetest.StatusCode;
import net.apnic.rdap.conformance.responsetest.NotStatusCode;
import net.apnic.rdap.conformance.responsetest.ContentType;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.attributetest.ErrorResponse;
import net.apnic.rdap.conformance.Utils;

/**
 * <p>BasicRequest class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.4-SNAPSHOT
 */
public final class BasicRequest implements net.apnic.rdap.conformance.Test {
    private static final int HTTP_ERROR_CODE_LOWER_BOUND = 400;

    private int expectedStatus;
    private String testName;
    private String urlPath;
    private boolean invertStatusTest;
    private Result proto;
    private Context context = null;
    private HttpResponse httpResponse = null;
    private Throwable throwable = null;

    /**
     * <p>Constructor for BasicRequest.</p>
     *
     * @param argExpectedStatus a int.
     * @param argUrlPath a {@link java.lang.String} object.
     * @param argTestName a {@link java.lang.String} object.
     * @param argInvertStatusTest a boolean.
     * @param argProto a {@link Result} object.
     */
    public BasicRequest(final int argExpectedStatus,
                        final String argUrlPath,
                        final String argTestName,
                        final boolean argInvertStatusTest,
                        final Result argProto) {
        expectedStatus = argExpectedStatus;
        testName = argTestName;
        urlPath  = argUrlPath;
        invertStatusTest = argInvertStatusTest;
        proto = argProto;

        if (testName == null) {
            testName = "common."
                        + (argInvertStatusTest ? "not-" : "")
                        + ((expectedStatus == HttpStatus.SC_NOT_FOUND)
                            ? "not-found"
                            : expectedStatus);
        }
    }

    /**
     * <p>Constructor for BasicRequest.</p>
     *
     * @param argExpectedStatus a int.
     * @param argUrlPath a {@link java.lang.String} object.
     * @param argTestName a {@link java.lang.String} object.
     * @param argInvertStatusTest a boolean.
     */
    public BasicRequest(final int argExpectedStatus,
                        final String argUrlPath,
                        final String argTestName,
                        final boolean argInvertStatusTest) {
        this(argExpectedStatus, argUrlPath, argTestName,
             argInvertStatusTest, null);
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
        String bu = context.getSpecification().getBaseUrl();
        String path = bu + urlPath;
        return Utils.httpGetRequest(context, path, true);
    }

    /** {@inheritDoc} */
    public boolean run() {
        List<Result> results = context.getResults();

        String bu = context.getSpecification().getBaseUrl();
        String path = bu + urlPath;

        if (proto == null) {
            proto = new Result(Status.Notification, path, testName,
                               "", "", "", "");
        } else {
            proto = new Result(proto);
            proto.setPath(path);
        }

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
            (invertStatusTest)
                ? new NotStatusCode(expectedStatus)
                : new StatusCode(expectedStatus);
        boolean scres = sc.run(context, proto, httpResponse);
        if (!scres) {
            return false;
        }

        Header cth = httpResponse.getEntity().getContentType();
        if (cth == null) {
            /* If there is no content-type, then there shouldn't be a body.
             * This is fine, since bodies are optional for error responses. */
            return true;
        }

        ResponseTest ct = new ContentType();
        boolean ctres = ct.run(context, proto, httpResponse);

        if (!(scres && ctres)) {
            return false;
        }

        Map root = null;
        try {
            InputStream is = httpResponse.getEntity().getContent();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            root = new Gson().fromJson(isr, Map.class);
        } catch (Exception e) {
            r = new Result(proto);
            r.setInfo(e.toString());
            results.add(r);
            return false;
        }
        if (root == null) {
            return ctres;
        }

        Set<String> keys = root.keySet();
        if (keys.size() == 0) {
            return ctres;
        }

        if ((expectedStatus >= HTTP_ERROR_CODE_LOWER_BOUND)
                && (!invertStatusTest)) {
            AttributeTest ert = new ErrorResponse(expectedStatus);
            return ert.run(context, proto, root);
        } else {
            return ctres;
        }
    }
}
