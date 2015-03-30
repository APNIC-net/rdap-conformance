package net.apnic.rdap.conformance.attributetest;

import java.util.HashSet;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;

/**
 * <p>PublicId class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.4-SNAPSHOT
 */
public final class PublicId implements AttributeTest {
    private Set<String> knownAttributes = new HashSet<String>();

    /**
     * <p>Constructor for PublicId.</p>
     */
    public PublicId() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        return Utils.runTestList(
            context, proto, data, knownAttributes, true,
            Arrays.<AttributeTest>asList(
                new ScalarAttribute("type"),
                new ScalarAttribute("identifier")
            )
        );
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
