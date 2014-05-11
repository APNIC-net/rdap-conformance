package net.apnic.rdap.conformance.contenttest;

import java.util.Map;
import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.contenttest.Events;
import net.apnic.rdap.conformance.contenttest.Links;
import net.apnic.rdap.conformance.contenttest.Flags;
import net.apnic.rdap.conformance.contenttest.Algorithm;
import net.apnic.rdap.conformance.contenttest.Protocol;
import net.apnic.rdap.conformance.contenttest.PublicKey;

public class KeyData implements ContentTest
{
    private Set<String> known_attributes = null;

    public KeyData() {}

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        boolean ret = true;
        List<ContentTest> tests =
            new ArrayList<ContentTest>(Arrays.asList(
                new ScalarAttribute("flags", new Flags()),
                new ScalarAttribute("protocol", new Protocol()),
                new ScalarAttribute("publicKey", new PublicKey()),
                new ScalarAttribute("algorithm", new Algorithm()),
                new Events(),
                new Links()
            ));

        known_attributes = new HashSet<String>();
        for (ContentTest test : tests) {
            boolean ret_inner = test.run(context, proto, arg_data);
            if (!ret_inner) {
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
