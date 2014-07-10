package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;
import net.apnic.rdap.conformance.Utils;

public class KeyTag implements ValueTest {
    public KeyTag() { }

    public boolean run(Context context, Result proto,
                       Object argData) {
        Integer value = Utils.castToInteger(argData);

        Result nr = new Result(proto);
        nr.setDetails((value != null), "is integer", "not integer");
        context.addResult(nr);

        if (value != null) {
            Result cvr = new Result(proto);
            boolean res = cvr.setDetails(((value >= 0) && (value <= 65535)),
                                         "valid", "invalid");
            context.addResult(cvr);
            return res;
        } else {
            return false;
        }
    }
}
