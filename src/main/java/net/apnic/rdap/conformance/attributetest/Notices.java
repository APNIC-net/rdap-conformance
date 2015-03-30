package net.apnic.rdap.conformance.attributetest;

import java.util.Set;
import java.util.Map;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;

/**
 * <p>Notices class.</p>
 *
 * The key argument in the constructor is present so that both
 * 'remarks' and 'notices' elements can be tested by way of this
 * class.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Notices implements AttributeTest {
    private String key = null;

    /**
     * <p>Constructor for Notices.</p>
     */
    public Notices() { }

    /**
     * <p>Constructor for Notices.</p>
     *
     * @param argKey a {@link java.lang.String} object.
     */
    public Notices(final String argKey) {
        key = argKey;
    }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        String mkey = (key != null) ? key : "notices";
        AttributeTest arrayTest = new ArrayAttribute(new Notice(), mkey);

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("rfc7483");
        nr.setReference("4.3");

        return arrayTest.run(context, nr, data);
    }

    /**
     * <p>getKnownAttributes.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getKnownAttributes() {
        return Sets.newHashSet(key != null ? key : "notices");
    }
}
