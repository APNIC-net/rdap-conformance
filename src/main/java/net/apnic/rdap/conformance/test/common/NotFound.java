package net.apnic.rdap.conformance.test.common;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Test;
import org.apache.http.HttpStatus;

public final class NotFound implements Test {
    private Test cbr = null;

    public NotFound(final String path) {
        cbr = new BasicRequest(
            HttpStatus.SC_NOT_FOUND,
            path,
            null,
            false
        );
    }

    public boolean run(final Context context) {
        return cbr.run(context);
    }
}
