package net.apnic.rdap.conformance.contenttest;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import ezvcard.*;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.SearchTest;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.contenttest.StandardObject;
import net.apnic.rdap.conformance.contenttest.StandardResponse;
import net.apnic.rdap.conformance.contenttest.DomainNames;
import net.apnic.rdap.conformance.contenttest.SecureDNS;
import net.apnic.rdap.conformance.contenttest.Nameserver;
import net.apnic.rdap.conformance.contenttest.Variant;
import net.apnic.rdap.conformance.contenttest.Array;
import net.apnic.rdap.conformance.contenttest.ScalarAttribute;
import net.apnic.rdap.conformance.contenttest.UnknownAttributes;
import net.apnic.rdap.conformance.contenttest.Domain;

public class Domain implements SearchTest
{
    Set<String> known_attributes = null;
    String key = null;
    String pattern = null;

    public Domain() {}

    public void setSearchDetails(String key, String pattern)
    {
    }

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        List<Result> results = context.getResults();

        List<ContentTest> tests =
            new ArrayList<ContentTest>(Arrays.asList(
                new ScalarAttribute("handle"),
                new DomainNames(),
                new Array(new Variant(), "variants"),
                new Array(new Nameserver(true), "nameServers"),
                new ScalarAttribute("secureDNS", new SecureDNS())
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

        ContentTest ua = new UnknownAttributes(known_attributes);
        boolean ret2 = ua.run(context, proto, arg_data);
        return (ret && ret2);
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
