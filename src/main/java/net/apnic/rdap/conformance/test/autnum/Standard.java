package net.apnic.rdap.conformance.test.autnum;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.contenttest.StandardResponse;

public class Standard implements net.apnic.rdap.conformance.Test
{
    String autnum = "";

    public Standard(String arg_autnum)
    {
        autnum = arg_autnum;
    }

    private BigInteger processAutnum(Context context, Result proto,
                                     Map root, String key)
    {
        Result asnres = new Result(proto);
        Object asn_obj = root.get(key);
        if (asn_obj == null) {
            asnres.setStatus(Status.Failure);
            asnres.setInfo(key + " element not found");
            context.addResult(asnres);
            return null;
        } else {
            asnres.setStatus(Status.Success);
            asnres.setInfo(key + " element found");
            context.addResult(asnres);
        }

        Result asn2res = new Result(proto);
        asn2res.setStatus(Status.Success);
        asn2res.setInfo(key + " element is valid");
        BigInteger asn = null;
        try { 
            asn = BigDecimal.valueOf((Double) root.get(key))
                            .toBigIntegerExact();
        } catch (Exception e) {
            asn2res.setStatus(Status.Failure);
            asn2res.setInfo(key + " element is not valid " + e.toString());
            context.addResult(asn2res);
            return null;
        }
        context.addResult(asn2res);

        return asn;
    }

    public boolean run(Context context)
    {
        List<Result> results = context.getResults();

        String bu = context.getSpecification().getBaseUrl();
        String path = bu + "/autnum/" + autnum;
        
        Result proto = new Result(Status.Notification, path,
                                  "autnum.standard",
                                  "", "", "", "");
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
            r.setStatus(Status.Success);
            r.setInfo("startAutnum less then or equal to endAutnum");
            if (start_address.compareTo(end_address) > 0) {
                r.setStatus(Status.Failure);
                r.setInfo("startAutnum more than endAutnum");
                results.add(r);
            } else {
                results.add(r);
            }
        }

        ContentTest srt = new StandardResponse();
        return srt.run(context, proto, root);
    }
}
