package net.apnic.rdap.conformance.attributetest;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;

public class PublicIds implements AttributeTest
{
    public PublicIds() {}

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        AttributeTest array_test =
            new ArrayAttribute(new PublicId(), "publicIds");

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("5.8");

        return array_test.run(context, nr, data);
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet("publicIds");
    }
}
