package net.apnic.rdap.conformance.test.entity;

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
    String handle = "";

    public Standard(String arg_handle)
    {
        handle = arg_handle;
    }

    public boolean run(Context context)
    {
        List<Result> results = context.getResults();

        String bu = context.getSpecification().getBaseUrl();
        String path = bu + "/entity/" + handle;

        Result proto = new Result(Status.Notification, path,
                                  "entity.standard",
                                  "", "", "", "");
        Result r = new Result(proto);
        r.setCode("response");
        Map root = Utils.standardRequest(context, path, r);
        if (root == null) {
            return false;
        }

        String response_handle = (String) root.get("handle");
        r = new Result(proto);
        r.setStatus(Status.Success);
        r.setInfo("handle element found");
        if (response_handle == null) {
            r.setStatus(Status.Warning);
            r.setInfo("handle element not found");
        } 
        results.add(r);
        if (response_handle != null) {
            /* todo: won't work for a unicode query. */
            Result r2 = new Result(proto);
            r2.setStatus(Status.Success);
            r2.setInfo("response handle element matches requested handle");
            if (!response_handle.equals(handle)) {
                r2.setStatus(Status.Warning);
                r2.setInfo("response handle element does not " +
                           "match requested handle");
            }
            results.add(r2);
        }

        ContentTest srt = new StandardResponse();
        return srt.run(context, proto, root);
    }
}
