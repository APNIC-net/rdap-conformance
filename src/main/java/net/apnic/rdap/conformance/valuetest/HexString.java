package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;
import net.apnic.rdap.conformance.Utils;

public class HexString implements ValueTest
{
    public HexString() { }

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        String value = Utils.castToString(arg_data);

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
