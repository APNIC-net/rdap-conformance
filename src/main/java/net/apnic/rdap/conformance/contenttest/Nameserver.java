package net.apnic.rdap.conformance.contenttest;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.contenttest.DomainNames;

public class Nameserver implements ContentTest
{
    Set<String> known_attributes = null; 

    public Nameserver() {}

    public boolean run(Context context, Result proto, 
                       Object arg_data)
    {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("6.2"); 

        Map<String, Object> data;
        try {
            data = (Map<String, Object>) arg_data;
        } catch (ClassCastException e) {
            nr.setInfo("structure is invalid");
            nr.setStatus(Status.Failure);
            context.addResult(nr);
            return false;
        }

        List<ContentTest> tests =
            new ArrayList<ContentTest>(Arrays.asList(
                new ScalarAttribute("handle"),
                new DomainNames(),
                new StandardObject()
            ));

        known_attributes = new HashSet<String>();

        boolean ret = true;
        for (ContentTest test : tests) {
            boolean res = test.run(context, proto, arg_data);
            if (!res) {
                ret = false;
            }
            known_attributes.addAll(test.getKnownAttributes());
        }

        Map<String, Object> ip_addresses = 
            Utils.getMapAttribute(context, proto, "ipAddresses",
                                  Status.Notification, data);
        if (ip_addresses != null) {
            Result nr2 = new Result(nr);
            nr2.addNode("ipAddresses");
            ContentTest v4 = new Array(new IPv4Address(), "v4");
            ContentTest v6 = new Array(new IPv6Address(), "v6");
            boolean v4res = v4.run(context, nr2, ip_addresses);
            boolean v6res = v6.run(context, nr2, ip_addresses);
            if (!v4res || !v6res) {
                ret = false;
            }
            known_attributes.addAll(v4.getKnownAttributes());
            known_attributes.addAll(v6.getKnownAttributes());
        }
        known_attributes.add("ipAddresses");

        ContentTest ua = new UnknownAttributes(known_attributes);
        boolean ret2 = ua.run(context, proto, arg_data);
        return (ret && ret2);
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
