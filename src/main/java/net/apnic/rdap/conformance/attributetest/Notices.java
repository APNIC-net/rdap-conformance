package net.apnic.rdap.conformance.attributetest;

import java.util.Set;
import java.util.Map;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;

public final class Notices implements AttributeTest {
    private String key = null;

    public Notices() { }

    public Notices(final String argKey) {
        key = argKey;
    }

    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        String mkey = (key != null) ? key : "notices";
        AttributeTest arrayTest = new ArrayAttribute(new Notice(), mkey);

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("5.3");

        return arrayTest.run(context, nr, data);
    }

    public Set<String> getKnownAttributes() {
        return Sets.newHashSet(key != null ? key : "notices");
    }
}
