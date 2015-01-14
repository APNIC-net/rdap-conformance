package net.apnic.rdap.conformance.test.autnum;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.test.common.BasicRequest;
import org.apache.http.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;

/**
 * <p>BadRequest class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class BadRequest implements Test {
    private Test cbr = null;

    /**
     * <p>Constructor for BadRequest.</p>
     */
    public BadRequest() {
        Result proto = new Result();
        proto.setDocument("draft-ietf-weirds-rdap-query-18");
        proto.setReference("3.1.2");
        proto.setTestName("autnum.bad-request");
        cbr = new BasicRequest(
            HttpStatus.SC_BAD_REQUEST,
            "/autnum/...",
            null,
            false,
            proto
        );
    }

    /** {@inheritDoc} */
    public void setContext(final Context c) {
        cbr.setContext(c);
    }

    /** {@inheritDoc} */
    public void setResponse(final HttpResponse hr) {
        cbr.setResponse(hr);
    }

    /** {@inheritDoc} */
    public void setError(final Throwable t) {
        cbr.setError(t);
    }

    /** {@inheritDoc} */
    public HttpRequest getRequest() {
        return cbr.getRequest();
    }

    /** {@inheritDoc} */
    public boolean run() {
        return cbr.run();
    }
}
