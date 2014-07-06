package net.apnic.rdap.conformance.contenttest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;

public class Links implements ContentTest
{
    public Links() {}

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        ContentTest array_test = new ArrayAttribute(new Link(), "links");

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("5.2");

        return array_test.run(context, nr, arg_data);
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet("links");
    }
}
