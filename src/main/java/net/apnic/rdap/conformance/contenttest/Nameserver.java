package net.apnic.rdap.conformance.contenttest;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;

public class Nameserver implements ContentTest
{
    Set<String> known_attributes = null; 

    public Nameserver() {}

    public boolean run(Context context, Result proto, 
                       Object arg_data)
    {
        List<ContentTest> tests =
            new ArrayList<ContentTest>(Arrays.asList(
                new ScalarAttribute("handle"),
                new ScalarAttribute("ldhName"),
                new ScalarAttribute("unicodeName"),
                new ScalarAttribute("ipAddresses"),
                new StandardObject()
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
