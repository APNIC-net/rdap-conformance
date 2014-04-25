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

public class StandardResponse implements net.apnic.rdap.conformance.ContentTest
{
    Set<String> known_attributes;

    public StandardResponse() {}

    public boolean run(Context context, Result proto,
                       Object root)
    {
        List<ContentTest> tests = new ArrayList(Arrays.asList(
            new RdapConformance(),
            new Notices(),
            new StandardObject()
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
