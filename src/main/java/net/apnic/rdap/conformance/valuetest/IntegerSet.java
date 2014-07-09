package net.apnic.rdap.conformance.valuetest;

import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

public class IntegerSet implements ValueTest
{
    Set<Integer> members = null;

    public IntegerSet(Set<Integer> members)
    {
        this.members = members;
    }

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        Integer value = null;
        try {
            Double dvalue = (Double) arg_data;
            if ((dvalue != null) && (dvalue == Math.rint(dvalue))) {
                value = Integer.valueOf((int) Math.round(dvalue));
            }
        } catch (ClassCastException ce) {
            value = null;
        }

        Result nr = new Result(proto);
        nr.setDetails((value != null), "is integer", "not integer");
        context.addResult(nr);

        if (value != null) {
            Result cvr = new Result(proto);
            boolean res = cvr.setDetails(members.contains(value),
                                         "valid", "invalid");
            context.addResult(cvr);
            return res;
        } else {
            return false;
        }
    }
}
