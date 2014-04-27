package net.apnic.rdap.conformance.test.nameserver;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.List;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.contenttest.Nameserver;
import net.apnic.rdap.conformance.contenttest.RdapConformance;
import net.apnic.rdap.conformance.contenttest.Notices;
import net.apnic.rdap.conformance.contenttest.UnknownAttributes;

public class Standard implements net.apnic.rdap.conformance.Test
{
    String nameserver = "";

    public Standard(String arg_nameserver)
    {
        nameserver = arg_nameserver;
    }

    public boolean run(Context context)
    {
        List<Result> results = context.getResults();

        String bu = context.getSpecification().getBaseUrl();
        String path = bu + "/nameserver/" + nameserver;

        Result proto = new Result(Status.Notification, path,
                                  "domain.standard",
                                  "content", "",
                                  "draft-ietf-weirds-json-response-06",
                                  "6.2");
        Result r = new Result(proto);
        r.setCode("response");
        Map root = Utils.standardRequest(context, path, r);
        if (root == null) {
            return false;
        }

        List<ContentTest> tests =
            new ArrayList<ContentTest>(Arrays.asList(
                new Nameserver(),
                new RdapConformance(),
                new Notices()
            ));

        Set<String> known_attributes = new HashSet<String>();

        boolean ret = true;
        for (ContentTest test : tests) {
            boolean res = test.run(context, proto, root);
            if (!res) {
                ret = false;
            }
            known_attributes.addAll(test.getKnownAttributes());
        }

        ContentTest ua = new UnknownAttributes(known_attributes);
        boolean ret2 = ua.run(context, proto, root);
        return (ret && ret2);
    }
}
