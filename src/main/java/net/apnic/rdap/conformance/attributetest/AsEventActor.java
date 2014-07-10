package net.apnic.rdap.conformance.attributetest;

import java.util.Set;
import java.util.Map;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;

public final class AsEventActor implements AttributeTest {
    public AsEventActor() { }

    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> argData) {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("6.1");

        AttributeTest arrayTest =
            new ArrayAttribute(new Event(false), "asEventActor");
        return arrayTest.run(context, nr, argData);
    }

    public Set<String> getKnownAttributes() {
        return Sets.newHashSet("asEventActor");
    }
}
