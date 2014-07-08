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
        nr.addNode(key);

        Result nr1 = new Result(nr);
        nr1.setCode("content");
        nr1.setInfo("present");

        Object value = data.get(key);
        if (value == null) {
            nr1.setStatus(Status.Notification);
            nr1.setInfo("not present");
            context.addResult(nr1);
            return false;
        } else {
            nr1.setStatus(Status.Success);
            context.addResult(nr1);
        }

        Result nr2 = new Result(nr);
        nr2.setInfo("is array");

        List<Object> elements;
        try {
            elements = (List<Object>) value;
        } catch (ClassCastException e) {
            nr2.setStatus(Status.Failure);
            nr2.setInfo("is not array");
            context.addResult(nr2);
            return false;
        }

        nr2.setStatus(Status.Success);
        context.addResult(nr2);

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
