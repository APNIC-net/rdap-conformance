package net.apnic.rdap.conformance.test.ip;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.test.common.BasicRequest;
import org.apache.http.HttpStatus;

public class BadRequest implements net.apnic.rdap.conformance.Test
{
    Test cbr = null;

    public BadRequest()
    {
        cbr = new net.apnic.rdap.conformance.test.common.BasicRequest(
            HttpStatus.SC_BAD_REQUEST,
            "/ip/...",
            "ip.bad-request",
            false
        );
    }

    public boolean run(Context context)
    {
        return cbr.run(context);
    }
}
