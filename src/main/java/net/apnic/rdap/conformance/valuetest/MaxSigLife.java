package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;
import net.apnic.rdap.conformance.Utils;

/**
 * <p>MaxSigLife class.</p>
 *
 * See RFC 5910 [3.3].
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3
 */
public final class MaxSigLife implements ValueTest {
    /**
     * <p>Constructor for MaxSigLife.</p>
     */
    public MaxSigLife() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object argData) {
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
