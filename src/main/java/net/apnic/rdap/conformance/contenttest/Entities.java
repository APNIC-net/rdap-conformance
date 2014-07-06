package net.apnic.rdap.conformance.contenttest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.contenttest.Entity;
import net.apnic.rdap.conformance.contenttest.ArrayAttribute;

public class Entities implements ContentTest
{
    Set<String> known_attributes;

    public Entities() {}

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        ContentTest array_test =
            new ArrayAttribute(new Entity(null, true), "entities");

        Result nr = new Result(proto);
        nr.setCode("content");
        /* Only set the reference if it is not already set, since the
         * entities attribute is described separately for each object. */
        if (nr.getDocument() == null) {
            nr.setDocument("draft-ietf-weirds-json-response-06");
            nr.setReference("6.1");
        }

        known_attributes = array_test.getKnownAttributes();

        return array_test.run(context, nr, arg_data);
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
