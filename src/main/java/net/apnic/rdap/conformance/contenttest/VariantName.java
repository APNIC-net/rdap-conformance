package net.apnic.rdap.conformance.contenttest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;

public class VariantName implements ContentTest
{
    Set<String> known_attributes = null;

    public VariantName() {}

    public boolean run(Context context, Result proto, 
                       Object arg_data)
    {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.addNode("status");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("6.3");

        Map<String, Object> data;
        try {
            data = (Map<String, Object>) arg_data;
        } catch (ClassCastException e) {
            nr.setInfo("structure is invalid");
            nr.setStatus(Result.Status.Failure);
            context.addResult(nr);
            return false;
        }

        boolean ret = true;
        List<ContentTest> tests =
            new ArrayList<ContentTest>(Arrays.asList(
                new DomainNames()
            ));

        known_attributes = new HashSet<String>();
        for (ContentTest test : tests) {
            boolean ret_inner = test.run(context, proto, arg_data);
            if (!ret_inner) {
                ret = false;
            }
            known_attributes.addAll(test.getKnownAttributes());
        }

        ContentTest ua = new UnknownAttributes(known_attributes);
        boolean ret2 = ua.run(context, proto, arg_data);
        return (ret && ret2);
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
