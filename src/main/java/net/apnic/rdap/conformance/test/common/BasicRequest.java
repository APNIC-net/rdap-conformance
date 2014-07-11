package net.apnic.rdap.conformance.test.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
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
 * @version 0.2
 */
public final class BasicRequest implements net.apnic.rdap.conformance.Test {
    private static final int HTTP_ERROR_CODE_LOWER_BOUND = 400;

    private int expectedStatus;
    private String testName;
    private String urlPath;
    private boolean invertStatusTest;
    private Result proto;

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
            testName = "common." +
                        (argInvertStatusTest ? "not-" : "")
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
    public boolean run(final Context context) {
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
        Result r = new Result(proto);
        r.setCode("response");

        HttpRequestBase request = null;
        HttpResponse response = null;
        try {
            request = Utils.httpGetRequest(context, path, true);
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
            (invertStatusTest)
                ? new NotStatusCode(expectedStatus)
                : new StatusCode(expectedStatus);
        boolean scres = sc.run(context, proto, response);
        if (!scres) {
            request.releaseConnection();
            return false;
        }

        Header cth = response.getEntity().getContentType();
        if (cth == null) {
            /* If there is no content-type, then there shouldn't be a body.
             * This is fine, since bodies are optional for error responses. */
            request.releaseConnection();
            return true;
        }

        ResponseTest ct = new ContentType();
        boolean ctres = ct.run(context, proto, response);

        if (!(scres && ctres)) {
            request.releaseConnection();
            return false;
        }

        Map root = null;
        try {
            InputStream is = response.getEntity().getContent();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            root = new Gson().fromJson(isr, Map.class);
        } catch (Exception e) {
            r = new Result(proto);
            r.setInfo(e.toString());
            results.add(r);
            request.releaseConnection();
            return false;
        }
        if (root == null) {
            request.releaseConnection();
            return ctres;
        }

        Set<String> keys = root.keySet();
        if (keys.size() == 0) {
            request.releaseConnection();
            return ctres;
        }

        if ((expectedStatus >= HTTP_ERROR_CODE_LOWER_BOUND)
                && (!invertStatusTest)) {
            AttributeTest ert = new ErrorResponse(expectedStatus);
            request.releaseConnection();
            return ert.run(context, proto, root);
        } else {
            return ctres;
        }
    }
}
