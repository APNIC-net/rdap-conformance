package net.apnic.rdap.conformance.contenttest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;

public class VariantRelation implements ContentTest
{
    private static final Set<String> relations =
        Sets.newHashSet("registered",
                        "unregistered",
                        "registration restricted",
                        "open registration",
                        "conjoined");

    public VariantRelation() {}

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.addNode("status");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("6.3");

        Map<String, Object> data;
        try {
            data = (Map<String, Object>) arg_data;
        } catch (ClassCastException e) {
            nr.setInfo("structure is invalid");
            nr.setStatus(Result.Status.Failure);
            context.addResult(nr);
            return false;
        }

        Result nr1 = new Result(nr);
        nr1.setInfo("present");

        Object value = data.get("relation");
        if (value == null) {
            nr1.setStatus(Result.Status.Notification);
            nr1.setInfo("not present");
            results.add(nr1);
            return false;
        } else {
            nr1.setStatus(Result.Status.Success);
            results.add(nr1);
        }

        Result nr2 = new Result(nr);
        nr2.setInfo("is an array");

        List<Object> relation_entries;
        try {
            relation_entries = (List<Object>) value;
        } catch (ClassCastException e) {
            nr2.setStatus(Result.Status.Failure);
            nr2.setInfo("is not an array");
            results.add(nr2);
            return false;
        }

        nr2.setStatus(Result.Status.Success);
        results.add(nr2);

        boolean success = true;
        int i = 0;
        for (Object re : relation_entries) {
            Result r2 = new Result(nr);
            r2.addNode(Integer.toString(i++));
            r2.setReference("11.2.4");
            if (!relation_entries.contains((String) re)) {
                r2.setStatus(Result.Status.Failure);
                r2.setInfo("invalid: " + ((String) re));
                success = false;
            } else {
                r2.setStatus(Result.Status.Success);
                r2.setInfo("valid");
            }
            results.add(r2);
        }

        return success;
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet("relation");
    }
}
