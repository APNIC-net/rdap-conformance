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

public class ArrayAttribute implements AttributeTest
{
    Set<String> knownAttributes = null;
    ValueTest elementValueTest = null;
    AttributeTest elementAttributeTest = null;
    String key = null;

    public ArrayAttribute(ValueTest argElementTest,
                          String argKey)
    {
        elementValueTest = argElementTest;
        key = argKey;
        knownAttributes = Sets.newHashSet(argKey);
    }

    public ArrayAttribute(AttributeTest argElementTest,
                          String argKey)
    {
        elementAttributeTest = argElementTest;
        key = argKey;
        knownAttributes = Sets.newHashSet(argKey);
    }

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        Result nr = new Result(proto);
        nr.setCode("content");

        List<Object> elements =
            Utils.getListAttribute(context, nr, key,
                                   Status.Notification,
                                   data);
        if (elements == null) {
            return false;
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
                Map<String, Object> subdata = Utils.castToMap(context, proto2, e);
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

    public Set<String> getKnownAttributes()
    {
        return knownAttributes;
    }
}
