package net.apnic.rdap.conformance.contenttest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.Utils;

public class MaxSigLife implements ContentTest
{
    public MaxSigLife() { }

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        Result nr = new Result(proto);

        boolean res = true;
        Integer value = null;
        try {
            value = (Integer) arg_data;
        } catch (ClassCastException ce) {
        }

        if (value == null) {
            nr.setStatus(Status.Failure);
            nr.setInfo("not integer");
            res = false;
        } else {
            nr.setStatus(Status.Success);
            nr.setInfo("is integer");
        }
        context.addResult(nr);

        Result cvr = new Result(proto);
        if (value < 1) {
            cvr.setStatus(Status.Failure);
            cvr.setInfo("not positive");
            res = false;
        } else {
            cvr.setStatus(Status.Success);
            cvr.setInfo("positive");
        }
        context.addResult(cvr);

        return res;
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet();
    }
}
