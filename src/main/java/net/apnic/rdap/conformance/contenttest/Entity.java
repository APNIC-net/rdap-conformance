package net.apnic.rdap.conformance.contenttest;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.contenttest.StandardObject;
import net.apnic.rdap.conformance.contenttest.StandardResponse;

public class Entity implements ContentTest
{
    String handle = null;

    public Entity() {}

    public Entity(String arg_handle) 
    {
        handle = arg_handle;
    }

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.addNode("entity");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("6.1");

        Map<String, Object> root;
        try {
            root = (Map<String, Object>) arg_data;
        } catch (ClassCastException e) {
            nr.setInfo("structure is invalid");
            nr.setStatus(Status.Failure);
            context.addResult(nr);
            return false;
        }

        String response_handle = Utils.castToString(root.get("handle"));
        Result r = new Result(proto);
        r.setStatus(Status.Success);
        r.setInfo("handle element found");
        if (response_handle == null) {
            r.setStatus(Status.Warning);
            r.setInfo("handle element not found");
        } 
        context.addResult(r);
        if ((response_handle != null) && (handle != null)) {
            Result r2 = new Result(proto);
            r2.setStatus(Status.Success);
            r2.setInfo("response handle element matches requested handle");
            if (!response_handle.equals(handle)) {
                r2.setStatus(Status.Warning);
                r2.setInfo("response handle element does not " +
                           "match requested handle");
            }
            context.addResult(r2);
        }

        ContentTest srt = new StandardObject();
        return srt.run(context, proto, root);
    }
}
