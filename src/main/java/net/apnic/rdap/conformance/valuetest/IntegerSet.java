package net.apnic.rdap.conformance.valuetest;

import java.util.Set;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;
import net.apnic.rdap.conformance.Utils;

/**
 * <p>IntegerSet class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
 */
public final class IntegerSet implements ValueTest {
    private Set<Integer> members = null;

    /**
     * <p>Constructor for IntegerSet.</p>
     *
     * @param members a {@link java.util.Set} object.
     */
    public IntegerSet(final Set<Integer> members) {
        this.members = members;
    }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        Integer value = Utils.castToInteger(argData);

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
