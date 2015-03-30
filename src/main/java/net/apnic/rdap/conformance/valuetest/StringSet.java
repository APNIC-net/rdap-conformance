package net.apnic.rdap.conformance.valuetest;

import java.util.Set;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;
import net.apnic.rdap.conformance.Utils;

/**
 * <p>StringSet class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3
 */
public final class StringSet implements ValueTest {
    private Set<String> members = null;

    /**
     * <p>Constructor for StringSet.</p>
     *
     * @param members a {@link java.util.Set} object.
     */
    public StringSet(final Set<String> members) {
        this.members = members;
    }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        String value = Utils.castToString(argData);

        Result nr = new Result(proto);
        nr.setDetails((value != null), "is string", "not string");
        context.addResult(nr);

        if (value != null) {
            Result cvr = new Result(proto);
            boolean res = cvr.setDetails(members.contains(value),
                                         "valid",
                                         "invalid: " + value);
            context.addResult(cvr);
            return res;
        } else {
            return false;
        }
    }
}
