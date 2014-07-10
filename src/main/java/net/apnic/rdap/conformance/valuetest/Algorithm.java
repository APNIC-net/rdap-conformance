package net.apnic.rdap.conformance.valuetest;

import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

public class Algorithm implements ValueTest
{
    private static IntegerSet integerSet =
        new IntegerSet(Sets.newHashSet(
            3, 5, 6, 7, 8, 10, 12, 13, 14, 253, 254
        ));

    public Algorithm() { }

    public boolean run(Context context, Result proto,
                       Object argData)
    {
        return integerSet.run(context, proto, argData);
    }
}
