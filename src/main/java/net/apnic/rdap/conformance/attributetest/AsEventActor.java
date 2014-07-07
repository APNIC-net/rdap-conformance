package net.apnic.rdap.conformance.attributetest;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;

public class AsEventActor implements AttributeTest
{
    public AsEventActor() {}

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("6.1");

        AttributeTest array_test =
            new ArrayAttribute(new Event(false), "asEventActor");
        return array_test.run(context, nr, arg_data);
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet("asEventActor");
    }
}
