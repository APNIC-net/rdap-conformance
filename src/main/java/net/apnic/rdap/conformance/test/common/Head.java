package net.apnic.rdap.conformance.test.common;

import java.util.Set;
import java.util.HashSet;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ResponseTest;
import net.apnic.rdap.conformance.responsetest.StatusCode;
import net.apnic.rdap.conformance.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;

/**
 * <p>Head class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public class Head implements Test {
    private String url;
    private int statusCode;
    private Context context = null;
    private HttpResponse httpResponse = null;
    private Throwable throwable = null;
    private static Set<String> requests = new HashSet<String>();

    /**
     * <p>Constructor for Head.</p>
     *
     * @param argUrl a {@link java.lang.String} object.
     */
    public Head(final String argUrl,
                final int argStatusCode) {
        url = argUrl;
        statusCode = argStatusCode;
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
        return Utils.httpHeadRequest(context, url,
                                     !((statusCode >= 300) && (statusCode < 400)));
    }

    /** {@inheritDoc} */
    public boolean run() {
        Result proto = new Result(Status.Notification, url,
                                  "head.standard",
                                  "response", "",
                                  "draft-ietf-weirds-rdap-query-10",
                                  "2");

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
        ResponseTest sc = new StatusCode(statusCode);
        boolean scres = sc.run(context, proto, httpResponse);
        if (!scres) {
            return false;
        }
        return true;
    }
}
