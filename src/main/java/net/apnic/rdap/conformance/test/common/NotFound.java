package net.apnic.rdap.conformance.test.common;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.test.common.BasicRequest;
import org.apache.http.HttpStatus;

public class NotFound implements net.apnic.rdap.conformance.Test
{
    Test cbr = null;

    public NotFound(String path)
    {
        cbr = new net.apnic.rdap.conformance.test.common.BasicRequest(
            HttpStatus.SC_NOT_FOUND,
            path,
            null,
            false
        );
    }

    public boolean run(Context context)
    {
        return cbr.run(context);
    }
}
