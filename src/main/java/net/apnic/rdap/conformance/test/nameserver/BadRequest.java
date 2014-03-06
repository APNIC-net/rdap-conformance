package net.apnic.rdap.conformance.test.nameserver;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Test;

public class BadRequest implements net.apnic.rdap.conformance.Test
{
    Test cbr = null;

    public BadRequest()
    {
        cbr = new net.apnic.rdap.conformance.test.common.BadRequest(
            "nameserver.bad-request",
            "/nameserver/..."
        );
    }

    public boolean run(Context context)
    {
        return cbr.run(context);
    }
}
