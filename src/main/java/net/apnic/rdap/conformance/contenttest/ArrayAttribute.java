package net.apnic.rdap.conformance.contenttest;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.ContentTest;

public class ArrayAttribute implements ContentTest
{
    private String attribute_name;

    public ArrayAttribute(String arg_attribute_name)
    {
        attribute_name = arg_attribute_name;
    }

    public boolean run(Context context, Result proto, Object arg_data)
    {
        List<Result> results = context.getResults();
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.addNode(attribute_name);
        nr.setInfo("present");

        Map<String, Object> data;
        try {
            data = (Map<String, Object>) arg_data;
        } catch (ClassCastException e) {
            nr.setInfo("structure is invalid");
            nr.setStatus(Status.Failure);
            results.add(nr);
            return false;
        }

        Object value = data.get(attribute_name);
        boolean res;
        if (value == null) {
            nr.setStatus(Status.Warning);
            nr.setInfo("not present");
            res = false;
        } else {
            nr.setStatus(Status.Success);
            res = true;
        }
        results.add(nr);

        if (!res) {
            return false;
        }

        Result nr2 = new Result(nr);
        nr2.setInfo("is an array");

        List<Object> list_values = null;
        try { 
            list_values = (List<Object>) value;
        } catch (ClassCastException e) {
            nr2.setStatus(Status.Failure);
            nr2.setInfo("is not an array");
            res = false;
        }
        if (list_values != null) {
            nr2.setStatus(Status.Success);
            res = true;
        }
        results.add(nr2);

        return res;
    }
}
