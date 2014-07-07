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
import net.apnic.rdap.conformance.ObjectTest;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.attributetest.Nameserver;
import net.apnic.rdap.conformance.attributetest.RdapConformance;
import net.apnic.rdap.conformance.attributetest.Notices;
import net.apnic.rdap.conformance.attributetest.UnknownAttributes;

public class Standard implements ObjectTest
{
    String nameserver = null;
    String url = null;

    public Standard() {}

    public Standard(String nameserver)
    {
        this.nameserver = nameserver;
    }

    public void setUrl(String url)
    {
        nameserver = null;
        this.url = url;
    }

    public boolean run(Context context)
    {
        List<Result> results = context.getResults();

        String path =
            (url != null)
                ? url
                : context.getSpecification().getBaseUrl()
                    + "/nameserver/" + nameserver;

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

        List<AttributeTest> tests =
            new ArrayList<AttributeTest>(Arrays.asList(
                new Nameserver(false),
                new RdapConformance(),
                new Notices()
            ));

        Set<String> known_attributes = new HashSet<String>();

        boolean ret = true;
        for (AttributeTest test : tests) {
            boolean res = test.run(context, proto, root);
            if (!res) {
                ret = false;
            }
            known_attributes.addAll(test.getKnownAttributes());
        }

        AttributeTest ua = new UnknownAttributes(known_attributes);
        boolean ret2 = ua.run(context, proto, root);
        return (ret && ret2);
    }
}
