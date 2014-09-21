package net.apnic.rdap.conformance.attributetest;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.ValueTest;
import net.apnic.rdap.conformance.Utils;

/**
 * <p>ScalarAttribute class.</p>
 *
 * Provides for testing that a particular attribute in a map has a scalar
 * value, and that that value satisfies a particular ValueTest or
 * AttributeTest.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class ScalarAttribute implements AttributeTest {
    private String attributeName = null;
    private ValueTest valueTest = null;
    private AttributeTest attributeTest = null;

    /**
     * <p>Constructor for ScalarAttribute.</p>
     *
     * @param argAttributeName a {@link java.lang.String} object.
     */
    public ScalarAttribute(final String argAttributeName) {
        attributeName = argAttributeName;
    }

    /**
     * <p>Constructor for ScalarAttribute.</p>
     *
     * @param argAttributeName a {@link java.lang.String} object.
     * @param argAttributeTest a {@link net.apnic.rdap.conformance.AttributeTest} object.
     */
    public ScalarAttribute(final String argAttributeName,
                           final AttributeTest argAttributeTest) {
        attributeName = argAttributeName;
        attributeTest = argAttributeTest;
    }

    /**
     * <p>Constructor for ScalarAttribute.</p>
     *
     * @param argAttributeName a {@link java.lang.String} object.
     * @param argValueTest a {@link net.apnic.rdap.conformance.ValueTest} object.
     */
    public ScalarAttribute(final String argAttributeName,
                           final ValueTest argValueTest) {
        attributeName = argAttributeName;
        valueTest = argValueTest;
    }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setInfo("present");

        Object value = Utils.getAttribute(context, nr, attributeName,
                                          Status.Notification, data);
        boolean res = (value != null);

        Result nr2 = new Result(proto);
        nr2.setCode("content");
        nr2.addNode(attributeName);

        if (valueTest != null) {
            return (res && valueTest.run(context, nr2, value));
        } else if (attributeTest != null) {
            Map<String, Object> subdata =
                Utils.castToMap(context, nr2, value);
            if (subdata == null) {
                return false;
            } else {
                return (res && attributeTest.run(context, nr2, subdata));
            }
        }

        return res;
    }

    /**
     * <p>getKnownAttributes.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getKnownAttributes() {
        return Sets.newHashSet(attributeName);
    }
}
