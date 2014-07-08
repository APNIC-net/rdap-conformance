package net.apnic.rdap.conformance.attributetest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import java.math.BigInteger;
import java.math.BigDecimal;
import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;

import net.apnic.rdap.conformance.attributetest.PublicId;

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
