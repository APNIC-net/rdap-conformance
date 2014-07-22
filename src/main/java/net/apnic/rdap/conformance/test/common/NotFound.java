package net.apnic.rdap.conformance.test.common;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Test;
import org.apache.http.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;

/**
 * <p>NotFound class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class NotFound implements Test {
    private Test cbr = null;

    /**
     * <p>Constructor for NotFound.</p>
     *
     * @param path a {@link java.lang.String} object.
     */
    public NotFound(final String path) {
        cbr = new BasicRequest(
            HttpStatus.SC_NOT_FOUND,
            path,
            null,
            false
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
    public HttpRequest getRequest() {
        return cbr.getRequest();
    }

    /** {@inheritDoc} */
    public boolean run() {
        return cbr.run();
    }
}
