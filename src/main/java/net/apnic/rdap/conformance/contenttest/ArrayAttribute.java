package net.apnic.rdap.conformance.contenttest;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.ContentTest;

import net.apnic.rdap.conformance.contenttest.Event;

public class ArrayAttribute implements ContentTest
{
    Set<String> known_attributes = null;
    ContentTest element_test = null;
    String key = null;

    public ArrayAttribute(ContentTest arg_element_test,
                          String arg_key)
    {
        element_test = arg_element_test;
        key = arg_key;
        known_attributes = Sets.newHashSet(arg_key);
    }

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        Result nr = new Result(proto);
        nr.addNode(key);

        Map<String, Object> data;
        try {
            data = (Map<String, Object>) arg_data;
        } catch (ClassCastException e) {
            nr.setInfo("structure is invalid");
            nr.setStatus(Status.Failure);
            context.addResult(nr);
            return false;
        }

        Result nr1 = new Result(nr);
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
            boolean element_success = element_test.run(context, proto2, e);
            if (!element_success) {
                success = false;
            }
        }

        return success;
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
