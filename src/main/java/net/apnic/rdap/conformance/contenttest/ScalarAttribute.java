package net.apnic.rdap.conformance.contenttest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import java.math.BigInteger;
import java.math.BigDecimal;
import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.Utils;

import java.util.Set;

import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;

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

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet(attribute_name);
    }
}
