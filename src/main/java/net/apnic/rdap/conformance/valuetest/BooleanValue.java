package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

public class BooleanValue implements ValueTest
{
    public BooleanValue() { }

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        Result nr = new Result(proto);

        Boolean value = null;
        try {
            value = (Boolean) arg_data;
        } catch (ClassCastException ce) {
        }

        Boolean res = nr.setDetails((value != null), "is boolean",
                                    "not boolean");

        context.addResult(nr);
        return res;
    }
}
