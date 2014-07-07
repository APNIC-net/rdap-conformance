package net.apnic.rdap.conformance.attributetest;

import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;

public class Events implements AttributeTest
{
    public Events() {}

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        AttributeTest array_test = new ArrayAttribute(new Event(), "events");

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("5.5");

        return array_test.run(context, nr, arg_data);
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet("events");
    }
}
