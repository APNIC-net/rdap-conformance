package net.apnic.rdap.conformance.test.common;

import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;

import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.attributetest.ArrayAttribute;
import net.apnic.rdap.conformance.attributetest.ScalarAttribute;
import net.apnic.rdap.conformance.attributetest.StandardResponse;
import net.apnic.rdap.conformance.attributetest.ResultsTruncated;
import net.apnic.rdap.conformance.ResponseTest;
import net.apnic.rdap.conformance.responsetest.StatusCode;
import net.apnic.rdap.conformance.responsetest.NotStatusCode;
import net.apnic.rdap.conformance.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;

/**
 * <p>Link class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public class Link implements Test {
    private String url;
    private Result proto;
    private Context context = null;
    private HttpResponse httpResponse = null;
    private Throwable throwable = null;
    private static Set<String> requests = new HashSet<String>();

    /**
     * <p>Constructor for Link.</p>
     *
     * @param argUrl a {@link java.lang.String} object.
     * @param argProto a {@link net.apnic.rdap.conformance.Result} object.
     */
    public Link(final String argUrl,
                final Result argProto) {
        url = argUrl;
        proto = argProto;
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
        if (requests.contains(url)) {
            return null;
        }
        requests.add(url);
        return Utils.httpGetRequest(context, url, true);
    }

    /** {@inheritDoc} */
    public boolean run() {
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
        context.addResult(r);
        ResponseTest sc = new NotStatusCode(0);
        boolean scres = sc.run(context, proto, httpResponse);
        if (!scres) {
            return false;
        }
        return true;
    }
}
