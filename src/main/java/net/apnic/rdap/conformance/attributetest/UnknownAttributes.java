package net.apnic.rdap.conformance.attributetest;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;

public class UnknownAttributes implements AttributeTest
{
    Set<String> known_attributes = null;

    public UnknownAttributes() {}

    public UnknownAttributes(Set<String> arg_known_attributes)
    {
        known_attributes = arg_known_attributes;
    }

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("3.2");

        boolean success = true;

        Set<String> attributes = data.keySet();
        Sets.SetView<String> unknown_attributes =
            Sets.difference(attributes, known_attributes);
        for (String unknown_attribute : unknown_attributes) {
            if (unknown_attribute.indexOf('_') == -1) {
                Result ua = new Result(nr);
                ua.setStatus(Status.Failure);
                ua.addNode(unknown_attribute);
                ua.setInfo("attribute is not permitted here or is " +
                           "non-standard and does not " +
                           "contain an underscore");
                context.addResult(ua);
                success = false;
            }
        }

        return success;
    }

    public Set<String> getKnownAttributes()
    {
        return new HashSet<String>();
    }
}
