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
    Set<String> known_attributes = new HashSet<String>();

    public StandardObject() { }

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        return Utils.runTestList(
            context, proto, arg_data, known_attributes, false,
            Arrays.asList(
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
            )
        );
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
