package net.apnic.rdap.conformance.test.ip;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.test.common.BasicRequest;
import org.apache.http.HttpStatus;

public class BadRequest implements Test {
    private Test cbr = null;

    public BadRequest() {
        cbr = new BasicRequest(
            HttpStatus.SC_BAD_REQUEST,
            "/ip/...",
            "ip.bad-request",
            false
        );
    }

    public boolean run(final Context context) {
        return cbr.run(context);
    }
}
