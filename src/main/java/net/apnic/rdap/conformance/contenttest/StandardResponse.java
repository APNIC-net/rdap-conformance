package net.apnic.rdap.conformance.contenttest;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.contenttest.Notices;
import net.apnic.rdap.conformance.contenttest.Links;
import net.apnic.rdap.conformance.contenttest.Events;
import net.apnic.rdap.conformance.contenttest.Status;
import net.apnic.rdap.conformance.contenttest.Port43;
import net.apnic.rdap.conformance.contenttest.PublicIds;

public class StandardResponse implements net.apnic.rdap.conformance.ContentTest
{
    public StandardResponse() {}

    public boolean run(Context context, Result proto,
                       Object root)
    {
        List<ContentTest> tests = new ArrayList(Arrays.asList(
            new RdapConformance(),
            new Notices(),
            new Links(),
            new Events(),
            new Status(),
            new Port43(),
            new PublicIds()
        ));

        boolean ret = true;
        for (ContentTest test : tests) {
            boolean res = test.run(context, proto, root);
            if (!res) {
                ret = false;
            }
        }

        return ret;
    }
}
