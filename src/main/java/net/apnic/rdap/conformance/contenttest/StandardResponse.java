package net.apnic.rdap.conformance.contenttest;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.Utils;

public class StandardResponse implements net.apnic.rdap.conformance.ContentTest
{
    Set<String> known_attributes = new HashSet<String>();

    public StandardResponse() {}

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        return Utils.runTestList(
            context, proto, arg_data, known_attributes, false,
            Arrays.asList(
                new RdapConformance(),
                new Notices(),
                new StandardObject()
            )
        );
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
