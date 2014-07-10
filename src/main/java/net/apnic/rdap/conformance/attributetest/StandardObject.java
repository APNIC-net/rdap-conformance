package net.apnic.rdap.conformance.attributetest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;

public class StandardObject implements AttributeTest
{
    Set<String> knownAttributes = new HashSet<String>();

    public StandardObject() { }

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        return Utils.runTestList(
            context, proto, data, knownAttributes, false,
            Arrays.asList(
                new Links(),
                new Events(),
                new Status(),
                /* This appears to be permitted at any level of the response,
                * since the document refers to 'the containing object
                * instance'. */
                new Port43(),
                new PublicIds(),
                new Entities(),
                new Notices("remarks"),
                new Lang()
            )
        );
    }

    public Set<String> getKnownAttributes()
    {
        return knownAttributes;
    }
}
