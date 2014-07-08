package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

public class StringTest implements ValueTest
{
    public StringTest() {}

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        Result nr = new Result(proto);
        boolean res = nr.setDetails((Utils.castToString(arg_data) != null),
                                    "is string", "not string");
        context.addResult(nr);
        return res;
    }
}
