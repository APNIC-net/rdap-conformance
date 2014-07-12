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

/**
 * <p>DsData class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class DsData implements AttributeTest {
    private Set<String> knownAttributes = new HashSet<String>();

    /**
     * <p>Constructor for DsData.</p>
     */
    public DsData() { }

    /** {@inheritDoc} */
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

    /**
     * <p>Getter for the field <code>knownAttributes</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getKnownAttributes() {
        return knownAttributes;
    }
}
