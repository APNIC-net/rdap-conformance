package net.apnic.rdap.conformance.valuetest;

import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

public class DigestType implements ValueTest
{
    private static IntegerSet integerSet =
        new IntegerSet(Sets.newHashSet(
            1, 2, 3, 4
        ));

    public DigestType() { }

    public boolean run(Context context, Result proto,
                       Object argData)
    {
        return integerSet.run(context, proto, argData);
    }
}
