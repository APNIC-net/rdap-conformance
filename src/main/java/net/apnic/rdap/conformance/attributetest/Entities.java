package net.apnic.rdap.conformance.attributetest;

import java.util.Map;
import java.util.Set;

import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;

public class Entities implements AttributeTest
{
    Set<String> known_attributes;

    public Entities() {}

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        AttributeTest array_test =
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

        return array_test.run(context, nr, data);
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
