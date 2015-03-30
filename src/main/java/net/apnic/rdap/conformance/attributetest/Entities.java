package net.apnic.rdap.conformance.attributetest;

import java.util.Map;
import java.util.Set;

import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;

/**
 * <p>Entities class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3
 */
public final class Entities implements AttributeTest {
    private Set<String> knownAttributes;

    /**
     * <p>Constructor for Entities.</p>
     */
    public Entities() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        AttributeTest arrayTest =
            new ArrayAttribute(new Entity(null, true), "entities");

        Result nr = new Result(proto);
        nr.setCode("content");
        /* Only set the reference if it is not already set, since the
         * entities attribute is described separately for each object. */
        if (nr.getDocument() == null) {
            nr.setDocument("rfc7483");
            nr.setReference("5.1");
        }

        knownAttributes = arrayTest.getKnownAttributes();

        return arrayTest.run(context, nr, data);
    }

    /**
     * <p>Getter for the field <code>knownAttributes</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getKnownAttributes() {
        return knownAttributes;
    }
}
