package net.apnic.rdap.conformance.attributetest;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.SearchTest;
import net.apnic.rdap.conformance.valuetest.IPv4Address;
import net.apnic.rdap.conformance.valuetest.IPv6Address;

public class Nameserver implements SearchTest
{
    private String key = null;
    private String pattern = null;
    private boolean check_unknown = false;
    Set<String> known_attributes = null;

    public Nameserver(boolean arg_check_unknown)
    {
        check_unknown = arg_check_unknown;
    }

    public void setSearchDetails(String arg_key, String arg_pattern)
    {
        key = arg_key;
        pattern = arg_pattern;
    }

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("6.2");

        SearchTest domain_names = new DomainNames();
        if (key != null) {
            domain_names.setSearchDetails(key, pattern);
        }

        List<AttributeTest> tests =
            new ArrayList<AttributeTest>(Arrays.asList(
                new ScalarAttribute("handle"),
                domain_names,
                new StandardObject()
            ));

        known_attributes = new HashSet<String>();

        boolean ret = true;
        for (AttributeTest test : tests) {
            boolean res = test.run(context, nr, data);
            if (!res) {
                ret = false;
            }
            known_attributes.addAll(test.getKnownAttributes());
        }

        Map<String, Object> ip_addresses =
            Utils.getMapAttribute(context, nr, "ipAddresses",
                                  Status.Notification, data);
        if (ip_addresses != null) {
            Result nr2 = new Result(nr);
            nr2.addNode("ipAddresses");
            AttributeTest v4 = new ArrayAttribute(new IPv4Address(), "v4");
            AttributeTest v6 = new ArrayAttribute(new IPv6Address(), "v6");
            boolean v4res = v4.run(context, nr2, ip_addresses);
            boolean v6res = v6.run(context, nr2, ip_addresses);
            if (!v4res || !v6res) {
                ret = false;
            }
            known_attributes.addAll(v4.getKnownAttributes());
            known_attributes.addAll(v6.getKnownAttributes());
        }
        known_attributes.add("ipAddresses");

        boolean ret2 = true;
        if (check_unknown) {
            AttributeTest ua = new UnknownAttributes(known_attributes);
            ret2 = ua.run(context, nr, data);
        }

        return (ret && ret2);
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
