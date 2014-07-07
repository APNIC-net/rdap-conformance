package net.apnic.rdap.conformance.attributetest;

import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;

public class Protocol implements AttributeTest
{
    public Protocol() { }

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        Result nr = new Result(proto);

        boolean res = true;
        Integer value = null;
        try {
            Double dvalue = (Double) arg_data;
            if ((dvalue != null) && (dvalue == Math.rint(dvalue))) {
                value = new Integer((int) Math.round(dvalue));
            }
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

        if (value != null) {
            Result cvr = new Result(proto);
            if (value != 3) {
                cvr.setStatus(Status.Failure);
                cvr.setInfo("invalid");
                res = false;
            } else {
                cvr.setStatus(Status.Success);
                cvr.setInfo("valid");
            }
            context.addResult(cvr);
        }

        return res;
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet();
    }
}
