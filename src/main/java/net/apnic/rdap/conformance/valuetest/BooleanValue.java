package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

public final class BooleanValue implements ValueTest {
    public BooleanValue() { }

    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        Result nr = new Result(proto);

        Boolean value;
        try {
            value = (Boolean) argData;
        } catch (ClassCastException ce) {
            value = null;
        }

        Boolean res = nr.setDetails((value != null), "is boolean",
                                    "not boolean");

        context.addResult(nr);
        return res;
    }
}
