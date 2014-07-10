package net.apnic.rdap.conformance.attributetest;

import java.util.Map;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;

import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.valuetest.Flags;
import net.apnic.rdap.conformance.valuetest.Algorithm;
import net.apnic.rdap.conformance.valuetest.Protocol;
import net.apnic.rdap.conformance.valuetest.PublicKey;

public class KeyData implements AttributeTest
{
    private Set<String> knownAttributes = new HashSet<String>();

    public KeyData() {}

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        return Utils.runTestList(
            context, proto, data, knownAttributes, true,
            Arrays.asList(
                new ScalarAttribute("flags", new Flags()),
                new ScalarAttribute("protocol", new Protocol()),
                new ScalarAttribute("publicKey", new PublicKey()),
                new ScalarAttribute("algorithm", new Algorithm()),
                new Events(),
                new Links()
            )
        );
    }

    public Set<String> getKnownAttributes()
    {
        return knownAttributes;
    }
}
