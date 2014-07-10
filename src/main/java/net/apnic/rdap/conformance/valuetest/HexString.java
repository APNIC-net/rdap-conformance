package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;
import net.apnic.rdap.conformance.Utils;

public final class HexString implements ValueTest {
    public HexString() { }

    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        String value = Utils.castToString(argData);

        Result nr = new Result(proto);
        boolean res = nr.setDetails((value != null),
                                    "is string",
                                    "not string");
        context.addResult(nr);
        if (!res) {
            return false;
        }

        Result nr2 = new Result(proto);
        res = nr2.setDetails(value.matches("^[0-9A-Fa-f ]+$"),
                             "valid",
                             "invalid");
        context.addResult(nr2);
        return res;
    }
}
