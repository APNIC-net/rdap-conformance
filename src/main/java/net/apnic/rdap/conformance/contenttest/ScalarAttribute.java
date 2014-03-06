package net.apnic.rdap.conformance.contenttest;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.ContentTest;

public class ScalarAttribute implements ContentTest
{
    private String attribute_name;

    public ScalarAttribute(String arg_attribute_name)
    {
        attribute_name = arg_attribute_name;
    }

    public boolean run(Context context, Result proto, 
                       Object arg_data)
    {
        Result nr = new Result(proto);
        String ucattr_name = 
            Character.toUpperCase(attribute_name.charAt(0)) + 
            attribute_name.substring(1);
        nr.setCode("content");
        nr.addNode(attribute_name);
        nr.setInfo("present");

        Map<String, Object> data;
        try {
            data = (Map<String, Object>) arg_data;
        } catch (ClassCastException e) {
            nr.setInfo("structure is invalid");
            nr.setStatus(Status.Failure);
            context.addResult(nr);
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

        context.addResult(nr);
        
        return res;
    }
}
