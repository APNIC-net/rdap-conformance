package net.apnic.rdap.conformance.test.nameserver;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.test.common.BasicRequest;
import org.apache.http.HttpStatus;

/**
 * <p>BadRequest class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
 */
public final class BadRequest implements Test {
    private Test cbr = null;

    /**
     * <p>Constructor for BadRequest.</p>
     */
    public BadRequest() {
        Result proto = new Result();
        proto.setDocument("draft-ietf-weirds-rdap-query-10");
        proto.setReference("3.1.4");
        proto.setTestName("nameserver.bad-request");
        cbr = new BasicRequest(
            HttpStatus.SC_BAD_REQUEST,
            "/nameserver/...",
            null,
            false,
            proto
        );
    }

    /** {@inheritDoc} */
    public boolean run(final Context context) {
        return cbr.run(context);
    }
}
