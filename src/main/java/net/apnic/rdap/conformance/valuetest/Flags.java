package net.apnic.rdap.conformance.valuetest;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

public final class Flags implements ValueTest {
    private static IntegerSet integerSet =
        new IntegerSet(Sets.newHashSet(
            0, 256, 257
        ));

    public Flags() { }

    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        return integerSet.run(context, proto, argData);
    }
}
