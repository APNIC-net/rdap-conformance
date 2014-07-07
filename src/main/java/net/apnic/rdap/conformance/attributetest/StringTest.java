package net.apnic.rdap.conformance.attributetest;

import java.util.HashSet;
import java.util.Set;

import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;

public class StringTest implements AttributeTest
{
    public StringTest() {}

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        Result nr = new Result(proto);
        boolean success = true;
        nr.setStatus(Status.Success);
        nr.setInfo("is string");

        if (Utils.castToString(arg_data) == null) {
            nr.setStatus(Status.Failure);
            nr.setInfo("is not string");
            success = false;
        }

        context.addResult(nr);
        return success;
    }

    public Set<String> getKnownAttributes()
    {
        return new HashSet<String>();
    }
}
