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

public final class ScalarAttribute implements AttributeTest {
    private String attributeName = null;
    private ValueTest valueTest = null;
    private AttributeTest attributeTest = null;

    public ScalarAttribute(final String argAttributeName) {
        attributeName = argAttributeName;
    }

    public ScalarAttribute(final String argAttributeName,
                           final AttributeTest argAttributeTest) {
        attributeName = argAttributeName;
        attributeTest = argAttributeTest;
    }

    public ScalarAttribute(final String argAttributeName,
                           final ValueTest argValueTest) {
        attributeName = argAttributeName;
        valueTest = argValueTest;
    }

    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setInfo("present");

        Object value = Utils.getAttribute(context, nr, attributeName,
                                          Status.Notification, data);
        boolean res = (value != null);

        if (valueTest != null) {
            return (res && valueTest.run(context, nr, value));
        } else if (attributeTest != null) {
            Map<String, Object> subdata =
                Utils.castToMap(context, nr, value);
            if (subdata == null) {
                return false;
            } else {
                return (res && attributeTest.run(context, nr, subdata));
            }
        }

        return res;
    }

    public Set<String> getKnownAttributes() {
        return Sets.newHashSet(attributeName);
    }
}
