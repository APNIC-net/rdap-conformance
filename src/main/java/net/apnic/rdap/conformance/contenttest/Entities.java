package net.apnic.rdap.conformance.contenttest;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.ContentTest;

import net.apnic.rdap.conformance.contenttest.Event;

public class Entities implements ContentTest
{
    public Entities() {}

    public boolean run(Context context, Result proto, 
                       Object arg_data)
    {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.addNode("entities");
        /* Only set the reference if it is not already set, since the
         * entities attribute is described separately for each object. */
        if (nr.getDocument() == null) {
            nr.setDocument("draft-ietf-weirds-json-response-06");
            nr.setReference("6.1");
        }

        Map<String, Object> data;
        try {
            data = (Map<String, Object>) arg_data;
        } catch (ClassCastException e) {
            nr.setInfo("structure is invalid");
            nr.setStatus(Status.Failure);
            context.addResult(nr);
            return false;
        }

        Result nr1 = new Result(nr);
        nr1.setInfo("present");

        Object value = data.get("entities");
        if (value == null) {
            nr1.setStatus(Status.Notification);
            nr1.setInfo("not present");
            results.add(nr1);
            return false;
        } else {
            nr1.setStatus(Status.Success);
            results.add(nr1);
        }

        Result nr2 = new Result(nr);
        nr2.setInfo("is an array");

        List<Object> entities;
        try { 
            entities = (List<Object>) value;
        } catch (ClassCastException e) {
            nr2.setStatus(Status.Failure);
            nr2.setInfo("is not an array");
            results.add(nr2);
            return false;
        }

        nr2.setStatus(Status.Success);
        results.add(nr2);

        ContentTest entity = new Entity();
        boolean success = true;
        int i = 0;
        for (Object e : entities) {
            Result proto2 = new Result(nr);
            proto2.addNode(Integer.toString(i++));
            boolean entity_success = entity.run(context, proto2, e);
            if (!entity_success) {
                success = false;
            }
        }

        return success;
    }
}