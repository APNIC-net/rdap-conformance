package net.apnic.rdap.conformance.valuetest;

import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

public class Flags implements ValueTest
{
    private static IntegerSet integerSet =
        new IntegerSet(Sets.newHashSet(
            0, 256, 257
        ));

    public Flags() { }

    public boolean run(Context context, Result proto,
                       Object argData)
    {
        return integerSet.run(context, proto, argData);
    }
}
