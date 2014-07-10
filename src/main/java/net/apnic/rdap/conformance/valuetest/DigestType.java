package net.apnic.rdap.conformance.valuetest;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

public final class DigestType implements ValueTest {
    private static IntegerSet integerSet =
        new IntegerSet(Sets.newHashSet(
            1, 2, 3, 4
        ));

    public DigestType() { }

    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        return integerSet.run(context, proto, argData);
    }
}
