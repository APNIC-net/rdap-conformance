package net.apnic.rdap.conformance.contenttest;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import java.math.BigInteger;
import java.math.BigDecimal;
import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.Utils;

import net.apnic.rdap.conformance.contenttest.Events;
import net.apnic.rdap.conformance.contenttest.Status;
import net.apnic.rdap.conformance.contenttest.Port43;
import net.apnic.rdap.conformance.contenttest.PublicIds;

public class StandardObject implements net.apnic.rdap.conformance.ContentTest
{
    Set<String> known_attributes;

    public StandardObject() { }

    public boolean run(Context context, Result proto,
                       Object root)
    {
        List<ContentTest> tests = new ArrayList(Arrays.asList(
            new Links(),
            new Events(),
            new Status(),
            /* This appears to be permitted at any level of the response,
             * since the document refers to 'the containing object
             * instance'. */
            new Port43(),
            new PublicIds(),
            new Entities(),
            new Notices("remarks"),
            new Lang()
        ));

        known_attributes = new HashSet<String>();

        boolean ret = true;
        for (ContentTest test : tests) {
            boolean res = test.run(context, proto, root);
            if (!res) {
                ret = false;
            }
            known_attributes.addAll(test.getKnownAttributes());
        }

        return ret;
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
