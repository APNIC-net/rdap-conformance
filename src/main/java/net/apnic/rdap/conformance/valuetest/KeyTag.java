package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;
import net.apnic.rdap.conformance.Utils;

/**
 * <p>KeyTag class.</p>
 *
 * See RFC 4034 [3.1.6].
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.4-SNAPSHOT
 */
public final class KeyTag implements ValueTest {
    private static final int MAX_16BIT = 65535;

    /**
     * <p>Constructor for KeyTag.</p>
     */
    public KeyTag() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        Integer value = Utils.castToInteger(argData);

        Result nr = new Result(proto);
        nr.setDetails((value != null), "is integer", "not integer");
        context.addResult(nr);

        if (value != null) {
            Result cvr = new Result(proto);
            boolean res =
                cvr.setDetails(((value >= 0) && (value <= MAX_16BIT)),
                               "valid", "invalid");
            context.addResult(cvr);
            return res;
        } else {
            return false;
        }
    }
}
