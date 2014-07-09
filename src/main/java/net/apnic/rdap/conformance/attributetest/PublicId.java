package net.apnic.rdap.conformance.attributetest;

import java.util.HashSet;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;

public class PublicId implements AttributeTest
{
    private Set<String> known_attributes = new HashSet<String>();

    public PublicId() {}

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        return Utils.runTestList(
            context, proto, data, known_attributes, true,
            Arrays.<AttributeTest>asList(
                new ScalarAttribute("type"),
                new ScalarAttribute("identifier")
            )
        );
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
