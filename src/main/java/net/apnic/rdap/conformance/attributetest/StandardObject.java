package net.apnic.rdap.conformance.attributetest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;

/**
 * <p>StandardObject class.</p>
 *
 * Tests for all the attributes that may be present in a standard
 * object (i.e. an instance of an RDAP object class). Cf.
 * StandardResponse, which covers the top-level RDAP response.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class StandardObject implements AttributeTest {
    private Set<String> knownAttributes = new HashSet<String>();

    /**
     * <p>Constructor for StandardObject.</p>
     */
    public StandardObject() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        return Utils.runTestList(
            context, proto, data, knownAttributes, false,
            Arrays.asList(
                new Links(),
                new Events(),
                new Status(),
                /* This appears to be permitted at any level of the response,
                 * since the document refers to 'the containing object
                 * instance'. */
                new Port43(),
                new PublicIds(),
                new Entities(),
                new Notices("remarks"),
                new Lang()
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
