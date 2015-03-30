package net.apnic.rdap.conformance.valuetest;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

/**
 * <p>Role class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Role implements ValueTest {
    private static StringSet stringSet =
        new StringSet(
            Sets.newHashSet("registrant",
                            "technical",
                            "administrative",
                            "abuse",
                            "billing",
                            "registrar",
                            "reseller",
                            "sponsor",
                            "proxy",
                            "notifications",
                            "noc")
        );

    /**
     * <p>Constructor for Role.</p>
     */
    public Role() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object data) {
        Result r = new Result(proto);
        r.setDocument("rfc7483");
        r.setReference("10.2.4");
        return stringSet.run(context, r, data);
    }
}
