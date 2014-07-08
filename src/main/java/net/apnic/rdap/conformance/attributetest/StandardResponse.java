package net.apnic.rdap.conformance.attributetest;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;

public class StandardResponse implements net.apnic.rdap.conformance.AttributeTest
{
    Set<String> known_attributes = new HashSet<String>();

    public StandardResponse() {}

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        return Utils.runTestList(
            context, proto, data, known_attributes, false,
            Arrays.asList(
                new RdapConformance(),
                new Notices(),
                new StandardObject()
            )
        );
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
