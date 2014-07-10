package net.apnic.rdap.conformance.attributetest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;

public class StandardResponse implements AttributeTest
{
    Set<String> knownAttributes = new HashSet<String>();

    public StandardResponse() {}

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        return Utils.runTestList(
            context, proto, data, knownAttributes, false,
            Arrays.asList(
                new RdapConformance(),
                new Notices(),
                new StandardObject()
            )
        );
    }

    public Set<String> getKnownAttributes()
    {
        return knownAttributes;
    }
}
