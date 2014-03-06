package net.apnic.rdap.conformance.test.domain;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Test;

public class BadRequest implements net.apnic.rdap.conformance.Test
{
    Test cbr = null;

    public BadRequest()
    {
        cbr = new net.apnic.rdap.conformance.test.common.BadRequest(
            "domain.bad-request",
            "/domain/..."
        );
    }

    public boolean run(Context context)
    {
        return cbr.run(context);
    }
}
