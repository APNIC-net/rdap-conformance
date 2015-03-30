package net.apnic.rdap.conformance.attributetest;

import java.util.Map;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;

import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.valuetest.Flags;
import net.apnic.rdap.conformance.valuetest.Algorithm;
import net.apnic.rdap.conformance.valuetest.Protocol;
import net.apnic.rdap.conformance.valuetest.PublicKey;

/**
 * <p>KeyData class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.4-SNAPSHOT
 */
public final class KeyData implements AttributeTest {
    private Set<String> knownAttributes = new HashSet<String>();

    /**
     * <p>Constructor for KeyData.</p>
     */
    public KeyData() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        return Utils.runTestList(
            context, proto, data, knownAttributes, true,
            Arrays.asList(
                new ScalarAttribute("flags", new Flags()),
                new ScalarAttribute("protocol", new Protocol()),
                new ScalarAttribute("publicKey", new PublicKey()),
                new ScalarAttribute("algorithm", new Algorithm()),
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
