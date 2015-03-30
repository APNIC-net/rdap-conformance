package net.apnic.rdap.conformance.attributetest;

import java.util.Set;
import java.util.Map;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;

/**
 * <p>Events class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Events implements AttributeTest {
    /**
     * <p>Constructor for Events.</p>
     */
    public Events() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        AttributeTest arrayTest = new ArrayAttribute(new Event(), "events");

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("rfc7483");
        nr.setReference("4.5");

        return arrayTest.run(context, nr, data);
    }

    /**
     * <p>getKnownAttributes.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getKnownAttributes() {
        return Sets.newHashSet("events");
    }
}
