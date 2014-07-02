package net.apnic.rdap.conformance.contenttest;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.contenttest.Notice;
import net.apnic.rdap.conformance.contenttest.Array;

public class Notices implements ContentTest
{
    String key = null;

    public Notices() {}

    public Notices(String arg_key)
    {
        key = arg_key;
    }

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        String mkey = (key != null) ? key : "notices";
        ContentTest array_test = new Array(new Notice(), mkey);

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("5.3");

        return array_test.run(context, nr, arg_data);
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet(key != null ? key : "notices");
    }
}
