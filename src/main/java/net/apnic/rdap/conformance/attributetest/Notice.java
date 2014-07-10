package net.apnic.rdap.conformance.attributetest;

import java.util.Map;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.valuetest.StringTest;

public class Notice implements AttributeTest
{
    private Set<String> knownAttributes = new HashSet<String>();

    public Notice() {}

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        Result nr = new Result(proto);
        nr.setCode("content");

        return Utils.runTestList(
            context, nr, data, knownAttributes, true,
            Arrays.asList(
                new ScalarAttribute("title"),
                new ArrayAttribute(new StringTest(), "description"),
                new Links()
            )
        );
    }

    public Set<String> getKnownAttributes()
    {
        return knownAttributes;
    }
}
