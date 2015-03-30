package net.apnic.rdap.conformance.attributetest;

import java.util.List;
import java.util.Set;
import java.util.Map;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.ValueTest;
import net.apnic.rdap.conformance.Utils;

/**
 * <p>ArrayAttribute class.</p>
 *
 * Provides for testing that a particular attribute in a map has an
 * array value, and that each element of that array (if present)
 * satisfies a particular ValueTest or AttributeTest.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3
 */
public final class ArrayAttribute implements AttributeTest {
    private Set<String> knownAttributes = null;
    private ValueTest elementValueTest = null;
    private AttributeTest elementAttributeTest = null;
    private String key = null;

    /**
     * <p>Constructor for ArrayAttribute.</p>
     *
     * @param argElementTest a {@link net.apnic.rdap.conformance.ValueTest} object.
     * @param argKey a {@link java.lang.String} object.
     */
    public ArrayAttribute(final ValueTest argElementTest,
                          final String argKey) {
        elementValueTest = argElementTest;
        key = argKey;
        knownAttributes = Sets.newHashSet(argKey);
    }

    /**
     * <p>Constructor for ArrayAttribute.</p>
     *
     * @param argElementTest a {@link net.apnic.rdap.conformance.AttributeTest} object.
     * @param argKey a {@link java.lang.String} object.
     */
    public ArrayAttribute(final AttributeTest argElementTest,
                          final String argKey) {
        elementAttributeTest = argElementTest;
        key = argKey;
        knownAttributes = Sets.newHashSet(argKey);
    }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        Result nr = new Result(proto);
        nr.setCode("content");

        List<Object> elements =
            Utils.getListAttribute(context, nr, key,
                                   Status.Notification,
                                   data);
        if (elements == null) {
            return true;
        }

        nr.addNode(key);

        boolean success = true;
        int i = 0;
        for (Object e : elements) {
            Result proto2 = new Result(nr);
            proto2.addNode(Integer.toString(i++));
            if (elementValueTest != null) {
                boolean elementSuccess =
                    elementValueTest.run(context, proto2, e);
                if (!elementSuccess) {
                    success = false;
                }
            } else {
                Map<String, Object> subdata =
                    Utils.castToMap(context, proto2, e);
                if (subdata == null) {
                    success = false;
                } else {
                    boolean attributeSuccess =
                        elementAttributeTest.run(context, proto2, subdata);
                    if (!attributeSuccess) {
                        success = false;
                    }
                }
            }
        }

        return success;
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
