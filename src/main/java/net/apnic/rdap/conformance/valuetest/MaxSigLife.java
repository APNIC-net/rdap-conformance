package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;
import net.apnic.rdap.conformance.Utils;

public class MaxSigLife implements ValueTest {
    public MaxSigLife() { }

    public boolean run(Context context, Result proto,
                       Object argData) {
        Integer value = Utils.castToInteger(argData);

        Result nr = new Result(proto);
        nr.setDetails((value != null), "is integer", "not integer");
        context.addResult(nr);

        if (value != null) {
            Result cvr = new Result(proto);
            boolean res = cvr.setDetails((value >= 1),
                                         "positive", "not positive");
            context.addResult(cvr);
            return res;
        } else {
            return false;
        }
    }
}
