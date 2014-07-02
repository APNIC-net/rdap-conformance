package net.apnic.rdap.conformance.test.domain;

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
import net.apnic.rdap.conformance.ObjectTest;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.contenttest.DomainNames;
import net.apnic.rdap.conformance.contenttest.SecureDNS;
import net.apnic.rdap.conformance.contenttest.Nameserver;
import net.apnic.rdap.conformance.contenttest.Variant;
import net.apnic.rdap.conformance.contenttest.Array;
import net.apnic.rdap.conformance.contenttest.ScalarAttribute;
import net.apnic.rdap.conformance.contenttest.StandardResponse;
import net.apnic.rdap.conformance.contenttest.UnknownAttributes;

public class Standard implements ObjectTest
{
    String domain = null;
    String url = null;

    public Standard() {}

    public Standard(String domain)
    {
        this.domain = domain;
    }

    public void setUrl(String url)
    {
        domain = null;
        this.url = url;
    }

    public boolean run(Context context)
    {
        List<Result> results = context.getResults();

        String path =
            (url != null)
                ? url
                : context.getSpecification().getBaseUrl()
                    + "/domain/" + domain;

        Result proto = new Result(Status.Notification, path,
                                  "domain.standard",
                                  "content", "",
                                  "draft-ietf-weirds-json-response-06",
                                  "6.3");
        Result r = new Result(proto);
        r.setCode("response");
        Map root = Utils.standardRequest(context, path, r);
        if (root == null) {
            return false;
        }

        List<ContentTest> tests =
            new ArrayList<ContentTest>(Arrays.asList(
                new ScalarAttribute("handle"),
                new DomainNames(),
                new Array(new Variant(), "variants"),
                new Array(new Nameserver(true), "nameServers"),
                new ScalarAttribute("secureDNS", new SecureDNS()),
                new StandardResponse()
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
