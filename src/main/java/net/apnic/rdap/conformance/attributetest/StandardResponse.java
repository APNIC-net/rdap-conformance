package net.apnic.rdap.conformance.attributetest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;

/**
 * <p>StandardResponse class.</p>
 *
 * Tests for all the attributes that may be present in a standard
 * response, except for those that may also be present in individual
 * objects, as per StandardObject.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.4-SNAPSHOT
 */
public final class StandardResponse implements AttributeTest {
    private Set<String> knownAttributes = new HashSet<String>();

    /**
     * <p>Constructor for StandardResponse.</p>
     */
    public StandardResponse() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        return Utils.runTestList(
            context, proto, data, knownAttributes, false,
            Arrays.asList(
                new RdapConformance(),
                new Notices()
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
