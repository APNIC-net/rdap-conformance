package net.apnic.rdap.conformance.valuetest;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

/**
 * <p>Algorithm class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
 */
public final class Algorithm implements ValueTest {
    private static IntegerSet integerSet =
        new IntegerSet(Sets.newHashSet(
            3, 5, 6, 7, 8, 10, 12, 13, 14, 253, 254
        ));

    /**
     * <p>Constructor for Algorithm.</p>
     */
    public Algorithm() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        return integerSet.run(context, proto, argData);
    }
}
