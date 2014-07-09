package net.apnic.rdap.conformance.test.autnum;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.Map;
import java.util.List;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ObjectTest;
import net.apnic.rdap.conformance.attributetest.Country;
import net.apnic.rdap.conformance.attributetest.ScalarAttribute;
import net.apnic.rdap.conformance.attributetest.StandardResponse;

public class Standard implements ObjectTest
{
    String autnum = null;
    String url = null;

    public Standard() {}

    public Standard(String autnum)
    {
        this.autnum = autnum;
    }

    public void setUrl(String url)
    {
        autnum = null;
        this.url = url;
    }

    private BigInteger processAutnum(Context context, Result proto,
                                     Map root, String key)
    {
        Result asnres = new Result(proto);
        Object asn_obj = root.get(key);
        if (asn_obj == null) {
            asnres.setStatus(Result.Status.Failure);
            asnres.addNode(key);
            asnres.setInfo("not present");
            context.addResult(asnres);
            return null;
        } else {
            asnres.setStatus(Result.Status.Success);
            asnres.addNode(key);
            asnres.setInfo("present");
            context.addResult(asnres);
        }

        Result asn2res = new Result(proto);
        asn2res.setStatus(Result.Status.Success);
        asn2res.addNode(key);
        asn2res.setInfo("valid");
        BigInteger asn = null;
        try {
            asn = BigDecimal.valueOf((Double) root.get(key))
                            .toBigIntegerExact();
        } catch (Exception e) {
            asn2res.setStatus(Result.Status.Failure);
            asn2res.setInfo("invalid: " + e.toString());
            context.addResult(asn2res);
            return null;
        }
        context.addResult(asn2res);

        return asn;
    }

    public boolean run(Context context)
    {
        List<Result> results = context.getResults();

        String path =
            (url != null)
                ? url
                : context.getSpecification().getBaseUrl()
                    + "/autnum/" + autnum;

        Result proto = new Result(Result.Status.Notification, path,
                                  "autnum.standard",
                                  "content", "",
                                  "draft-ietf-weirds-json-response-06",
                                  "6.5");
        Result r = new Result(proto);
        r.setCode("response");
        Map root = Utils.standardRequest(context, path, r);
        if (root == null) {
            return false;
        }

        BigInteger start_address =
            processAutnum(context, proto, root, "startAutnum");
        BigInteger end_address =
            processAutnum(context, proto, root, "endAutnum");

        if ((start_address != null) && (end_address != null)) {
            r = new Result(proto);
            r.addNode("startAutnum");
            r.setStatus(Result.Status.Success);
            r.setInfo("startAutnum less then or equal to endAutnum");
            if (start_address.compareTo(end_address) > 0) {
                r.setStatus(Result.Status.Failure);
                r.setInfo("startAutnum more than endAutnum");
                results.add(r);
            } else {
                results.add(r);
            }
        }

        Set<String> known_attributes = Sets.newHashSet("startAutnum",
                                                       "endAutnum");
        Map<String, Object> data = Utils.castToMap(context, proto, root);
        if (data == null) {
            return false;
        }
        return Utils.runTestList(
            context, proto, data, known_attributes, true,
            Arrays.asList(
                new ScalarAttribute("name"),
                new ScalarAttribute("handle"),
                new ScalarAttribute("type"),
                new Country(),
                new StandardResponse()
            )
        );
    }
}
