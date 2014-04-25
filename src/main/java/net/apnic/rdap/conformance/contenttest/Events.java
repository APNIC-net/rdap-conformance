package net.apnic.rdap.conformance.contenttest;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;

public class Events implements ContentTest
{
    public Events() {}

    public boolean run(Context context, Result proto, 
                       Object arg_data)
    {
        ContentTest array_test = new Array(new Event(), "events");
        
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.addNode("events");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("5.5");

        return array_test.run(context, nr, arg_data);
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet("events");
    }
}
