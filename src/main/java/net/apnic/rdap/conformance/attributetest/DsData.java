package net.apnic.rdap.conformance.attributetest;

import java.util.Map;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;

import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.valuetest.KeyTag;
import net.apnic.rdap.conformance.valuetest.Algorithm;
import net.apnic.rdap.conformance.valuetest.Digest;
import net.apnic.rdap.conformance.valuetest.DigestType;

public final class DsData implements AttributeTest {
    private Set<String> knownAttributes = new HashSet<String>();

    public DsData() { }

    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        return Utils.runTestList(
            context, proto, data, knownAttributes, true,
            Arrays.asList(
                new ScalarAttribute("keyTag", new KeyTag()),
                new ScalarAttribute("algorithm", new Algorithm()),
                new ScalarAttribute("digest", new Digest()),
                new ScalarAttribute("digestType", new DigestType()),
                new Events(),
                new Links()
            )
        );
    }

    public Set<String> getKnownAttributes() {
        return knownAttributes;
    }
}
