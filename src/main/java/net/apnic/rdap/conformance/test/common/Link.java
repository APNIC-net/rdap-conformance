package net.apnic.rdap.conformance.test.common;

import java.util.Set;
import java.util.HashSet;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;

import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ResponseTest;
import net.apnic.rdap.conformance.responsetest.NotStatusCode;
import net.apnic.rdap.conformance.Utils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;

/**
 * <p>Link class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.4-SNAPSHOT
 */
final public class Link implements Test {
    private String url;
    private Result proto;
    private String expectedContentType;
    private Context context = null;
    private HttpResponse httpResponse = null;
    private Throwable throwable = null;
    private static Set<String> requests = new HashSet<String>();

    /**
     * <p>Constructor for Link.</p>
     *
     * @param argUrl a {@link java.lang.String} object.
     * @param argProto a {@link net.apnic.rdap.conformance.Result} object.
     * @param argExpectedContentType a {@link java.lang.String} object.
     */
    public Link(final String argUrl,
                final Result argProto,
                final String argExpectedContentType) {
        url = argUrl;
        proto = argProto;
        expectedContentType = argExpectedContentType;
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

        if (expectedContentType != null) {
            Header contentType =
                httpResponse.getFirstHeader("Content-Type");
            boolean res = contentType.getValue().equals(expectedContentType);

            Result r2 = new Result(proto);
            r2.setCode("response");
            r2.setStatus(res ? Status.Success : Status.Warning);
            r2.setInfo("link content type matches received content type");
            context.addResult(r2);
        }

        return scres;
    }
}
