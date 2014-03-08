package net.apnic.rdap.conformance.contenttest;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.ContentTest;

import net.apnic.rdap.conformance.contenttest.Entity;
import net.apnic.rdap.conformance.contenttest.Array;

public class Entities implements ContentTest
{
    public Entities() {}

    public boolean run(Context context, Result proto, 
                       Object arg_data)
    {
        ContentTest array_test = new Array(new Entity(), "entities");

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.addNode("entities");
        /* Only set the reference if it is not already set, since the
         * entities attribute is described separately for each object. */
        if (nr.getDocument() == null) {
            nr.setDocument("draft-ietf-weirds-json-response-06");
            nr.setReference("6.1");
        }

        return array_test.run(context, nr, arg_data);
    }
}
