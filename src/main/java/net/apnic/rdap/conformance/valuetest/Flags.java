package net.apnic.rdap.conformance.valuetest;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

/**
 * <p>Flags class.</p>
 *
 * See RFC 4034 [2.1.1] and [2.2].
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3
 */
public final class Flags implements ValueTest {
    private static final int NONE = 0;
    private static final int ZSK  = 256;
    private static final int KSK  = 257;

    private static IntegerSet integerSet =
        new IntegerSet(Sets.newHashSet(
            NONE, ZSK, KSK
        ));

    /**
     * <p>Constructor for Flags.</p>
     */
    public Flags() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        return integerSet.run(context, proto, argData);
    }
}
