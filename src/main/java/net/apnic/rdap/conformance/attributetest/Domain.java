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
    boolean checkUnknown = false;
    Set<String> knownAttributes = new HashSet<String>();
    String key = null;
    String pattern = null;

    public Domain(boolean argCheckUnknown)
    {
        checkUnknown = argCheckUnknown;
    }

    public void setSearchDetails(String argKey, String argPattern)
    {
        key = argKey;
        pattern = argPattern;
    }

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        SearchTest domainNames = new DomainNames();
        if (key != null) {
            domainNames.setSearchDetails(key, pattern);
        }

        return Utils.runTestList(
            context, proto, data, knownAttributes, checkUnknown,
            Arrays.asList(
                new ScalarAttribute("handle"),
                domainNames,
                new ArrayAttribute(new Variant(), "variants"),
                new ArrayAttribute(new Nameserver(true), "nameServers"),
                new ScalarAttribute("secureDNS", new SecureDNS()),
                new StandardObject()
            )
        );
    }

    public Set<String> getKnownAttributes()
    {
        return knownAttributes;
    }
}
