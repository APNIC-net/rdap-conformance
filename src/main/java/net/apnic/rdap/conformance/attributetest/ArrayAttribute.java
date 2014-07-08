package net.apnic.rdap.conformance.attributetest;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.ValueTest;
import net.apnic.rdap.conformance.Utils;

import net.apnic.rdap.conformance.attributetest.Event;

public class ArrayAttribute implements AttributeTest
{
    Set<String> known_attributes = null;
    ValueTest element_value_test = null;
    AttributeTest element_attribute_test = null;
    String key = null;

    public ArrayAttribute(ValueTest arg_element_test,
                          String arg_key)
    {
        element_value_test = arg_element_test;
        key = arg_key;
        known_attributes = Sets.newHashSet(arg_key);
    }

    public ArrayAttribute(AttributeTest arg_element_test,
                          String arg_key)
    {
        element_attribute_test = arg_element_test;
        key = arg_key;
        known_attributes = Sets.newHashSet(arg_key);
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
            if (element_value_test != null) {
                boolean element_success =
                    element_value_test.run(context, proto2, e);
                if (!element_success) {
                    success = false;
                }
            } else {
                Map<String, Object> subdata = Utils.castToMap(context, proto2, e);
                if (subdata == null) {
                    success = false;
                } else {
                    boolean attribute_success =
                        element_attribute_test.run(context, proto2, subdata);
                    if (!attribute_success) {
                        success = false;
                    }
                }
            }
        }

        return success;
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
