package net.apnic.rdap.conformance.test.domain;

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
    String domain = "";

    public Standard(String arg_domain)
    {
        domain = arg_domain;
    }

    public boolean run(Context context)
    {
        List<Result> results = context.getResults();

        String bu = context.getSpecification().getBaseUrl();
        String path = bu + "/domain/" + domain;

        Result proto = new Result(Status.Notification, path,
                                  "domain.standard",
                                  "", "", "", "");
        Result r = new Result(proto);
        r.setCode("response");
        Map root = Utils.standardRequest(context, path, r);
        if (root == null) {
            return false;
        }

        String ldhName = (String) root.get("ldhName");
        r = new Result(proto);
        r.setStatus(Status.Success);
        r.setInfo("ldhName element found");
        if (ldhName == null) {
            r.setStatus(Status.Warning);
            r.setInfo("ldhName element not found");
        } 
        results.add(r);
        if (ldhName != null) {
            /* todo: won't work for a unicode query. */
            Result r2 = new Result(proto);
            r2.setStatus(Status.Success);
            r2.setInfo("ldhName element matches requested domain");
            if (!ldhName.equals(domain)) {
                r2.setStatus(Status.Warning);
                r2.setInfo("ldhName element does not match requested domain");
            }
            results.add(r2);
        }

        ContentTest srt = new StandardResponse();
        return srt.run(context, proto, root);
    }
}
