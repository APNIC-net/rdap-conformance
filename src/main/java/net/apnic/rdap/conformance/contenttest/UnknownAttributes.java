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

public class UnknownAttributes implements ContentTest
{
    Set<String> known_attributes = null;

    public UnknownAttributes() {}

    public UnknownAttributes(Set<String> arg_known_attributes)
    {
        known_attributes = arg_known_attributes;
    }

    public boolean run(Context context, Result proto, 
                       Object arg_data)
    {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("3.2");

        Map<String, Object> root;
        try {
            root = (Map<String, Object>) arg_data;
        } catch (ClassCastException e) {
            nr.setInfo("structure is invalid");
            nr.setStatus(Status.Failure);
            context.addResult(nr);
            return false;
        }

        boolean success = true;

        Set<String> attributes = root.keySet();
        Sets.SetView<String> unknown_attributes = 
            Sets.difference(attributes, known_attributes);
        for (String unknown_attribute : unknown_attributes) {
            if (unknown_attribute.indexOf('_') == -1) {
                Result ua = new Result(nr);
                ua.setStatus(Status.Failure);
                ua.addNode(unknown_attribute);
                ua.setInfo("attribute is non-standard and does not " +
                           "contain an underscore");
                context.addResult(ua);
                success = false;
            }
        }

        return success;
    }

    public Set<String> getKnownAttributes()
    {
        return new HashSet<String>();
    }
} 
