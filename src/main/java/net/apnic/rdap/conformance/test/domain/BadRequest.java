package net.apnic.rdap.conformance.test.domain;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.test.common.BasicRequest;
import org.apache.http.HttpStatus;

public class BadRequest implements Test
{
    Test cbr = null;

    public BadRequest()
    {
        cbr = new BasicRequest(
            HttpStatus.SC_BAD_REQUEST,
            "/domain/...",
            "domain.bad-request",
            false
        );
    }

    public boolean run(Context context)
    {
        return cbr.run(context);
    }
}
