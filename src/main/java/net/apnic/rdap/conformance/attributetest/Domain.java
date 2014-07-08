package net.apnic.rdap.conformance.attributetest;

import java.util.HashSet;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import ezvcard.*;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.SearchTest;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.valuetest.Variant;

public class Domain implements SearchTest
{
    boolean check_unknown = false;
    Set<String> known_attributes = new HashSet<String>();
    String key = null;
    String pattern = null;

    public Domain(boolean arg_check_unknown)
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
        SearchTest domain_names = new DomainNames();
        if (key != null) {
            domain_names.setSearchDetails(key, pattern);
        }

        return Utils.runTestList(
            context, proto, data, known_attributes, check_unknown,
            Arrays.asList(
                new ScalarAttribute("handle"),
                domain_names,
                new ArrayAttribute(new Variant(), "variants"),
                new ArrayAttribute(new Nameserver(true), "nameServers"),
                new ScalarAttribute("secureDNS", new SecureDNS()),
                new StandardObject()
            )
        );
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
