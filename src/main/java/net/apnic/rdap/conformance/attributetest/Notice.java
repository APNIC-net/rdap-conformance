package net.apnic.rdap.conformance.attributetest;

import java.util.Map;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.valuetest.StringTest;
import net.apnic.rdap.conformance.valuetest.NoticeType;

/**
 * <p>Notice class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3
 */
public final class Notice implements AttributeTest {
    private Set<String> knownAttributes = new HashSet<String>();

    /**
     * <p>Constructor for Notice.</p>
     */
    public Notice() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        Result nr = new Result(proto);
        nr.setCode("content");

        return Utils.runTestList(
            context, nr, data, knownAttributes, true,
            Arrays.asList(
                new ScalarAttribute("title"),
                new ScalarAttribute("type", new NoticeType()),
                new ArrayAttribute(new StringTest(), "description"),
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
