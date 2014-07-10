package net.apnic.rdap.conformance.attributetest;

import java.util.Map;
import java.util.Set;

import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;

public final class Entities implements AttributeTest {
    private Set<String> knownAttributes;

    public Entities() { }

    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        AttributeTest arrayTest =
            new ArrayAttribute(new Entity(null, true), "entities");

        Result nr = new Result(proto);
        nr.setCode("content");
        /* Only set the reference if it is not already set, since the
         * entities attribute is described separately for each object. */
        if (nr.getDocument() == null) {
            nr.setDocument("draft-ietf-weirds-json-response-06");
            nr.setReference("6.1");
        }

        knownAttributes = arrayTest.getKnownAttributes();

        return arrayTest.run(context, nr, data);
    }

    public Set<String> getKnownAttributes() {
        return knownAttributes;
    }
}
