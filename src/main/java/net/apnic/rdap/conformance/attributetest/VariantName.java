package net.apnic.rdap.conformance.attributetest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;

public class VariantName implements AttributeTest
{
    Set<String> known_attributes = null;

    public VariantName() {}

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.addNode("status");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("6.3");

        boolean ret = true;
        List<AttributeTest> tests =
            new ArrayList<AttributeTest>(Arrays.asList(
                new DomainNames()
            ));

        known_attributes = new HashSet<String>();
        for (AttributeTest test : tests) {
            boolean ret_inner = test.run(context, proto, data);
            if (!ret_inner) {
                ret = false;
            }
            known_attributes.addAll(test.getKnownAttributes());
        }

        AttributeTest ua = new UnknownAttributes(known_attributes);
        boolean ret2 = ua.run(context, proto, data);
        return (ret && ret2);
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
