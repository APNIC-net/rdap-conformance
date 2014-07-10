package net.apnic.rdap.conformance.attributetest;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;

public class VariantName implements AttributeTest {
    Set<String> knownAttributes = new HashSet<String>();

    public VariantName() { }

    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.addNode("status");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("6.3");

        return Utils.runTestList(
            context, nr, data, knownAttributes, true,
            Arrays.<AttributeTest>asList(
                new DomainNames()
            )
        );
    }

    public Set<String> getKnownAttributes() {
        return knownAttributes;
    }
}
