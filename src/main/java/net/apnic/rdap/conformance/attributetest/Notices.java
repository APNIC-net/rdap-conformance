package net.apnic.rdap.conformance.attributetest;

import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.attributetest.Notice;
import net.apnic.rdap.conformance.attributetest.ArrayAttribute;

public class Notices implements AttributeTest
{
    String key = null;

    public Notices() {}

    public Notices(String arg_key)
    {
        key = arg_key;
    }

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        String mkey = (key != null) ? key : "notices";
        AttributeTest array_test = new ArrayAttribute(new Notice(), mkey);

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("5.3");

        return array_test.run(context, nr, data);
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet(key != null ? key : "notices");
    }
}
