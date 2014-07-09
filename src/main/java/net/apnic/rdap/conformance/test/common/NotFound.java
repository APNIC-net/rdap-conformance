package net.apnic.rdap.conformance.test.common;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Test;
import org.apache.http.HttpStatus;

public class NotFound implements Test
{
    Test cbr = null;

    public NotFound(String path)
    {
        cbr = new BasicRequest(
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
