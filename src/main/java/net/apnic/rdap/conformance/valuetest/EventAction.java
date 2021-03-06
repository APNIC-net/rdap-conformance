package net.apnic.rdap.conformance.valuetest;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

/**
 * <p>EventAction class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.4-SNAPSHOT
 */
public final class EventAction implements ValueTest {
    private static StringSet stringSet =
        new StringSet(
            Sets.newHashSet("registration",
                            "reregistration",
                            "last changed",
                            "expiration",
                            "deletion",
                            "reinstantiation",
                            "transfer",
                            "locked",
                            "unlocked")
        );

    /**
     * <p>Constructor for EventAction.</p>
     */
    public EventAction() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object data) {
        Result r = new Result(proto);
        r.setDocument("rfc7483");
        r.setReference("10.2.3");
        return stringSet.run(context, r, data);
    }
}
