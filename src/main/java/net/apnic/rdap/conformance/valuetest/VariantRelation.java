package net.apnic.rdap.conformance.valuetest;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

/**
 * <p>VariantRelation class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class VariantRelation implements ValueTest {
    private static StringSet stringSet =
        new StringSet(
            Sets.newHashSet("registered",
                            "unregistered",
                            "registration restricted",
                            "open registration",
                            "conjoined")
        );

    /**
     * <p>Constructor for VariantRelation.</p>
     */
    public VariantRelation() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object data) {
        return stringSet.run(context, proto, data);
    }
}
