package net.apnic.rdap.conformance.valuetest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;
import net.apnic.rdap.conformance.Utils;

public class PublicKey implements ValueTest
{
    public PublicKey() { }

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        Result nr = new Result(proto);

        boolean res = true;
        String value = Utils.castToString(arg_data);
        if (value == null) {
            nr.setStatus(Status.Failure);
            nr.setInfo("not string");
            res = false;
        } else {
            nr.setStatus(Status.Success);
            nr.setInfo("is string");
        }
        context.addResult(nr);

        Result cvr = new Result(proto);
        if (!value.matches("^[0-9A-Fa-f ]+$")) {
            cvr.setStatus(Status.Failure);
            cvr.setInfo("invalid");
            res = false;
        } else {
            cvr.setStatus(Status.Success);
            cvr.setInfo("valid");
        }
        context.addResult(cvr);

        return res;
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet();
    }
}
