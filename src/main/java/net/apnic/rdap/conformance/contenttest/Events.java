package net.apnic.rdap.conformance.contenttest;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.ContentTest;

import net.apnic.rdap.conformance.contenttest.Event;
import net.apnic.rdap.conformance.contenttest.Array;

public class Events implements ContentTest
{
    public Events() {}

    public boolean run(Context context, Result proto, 
                       Object arg_data)
    {
        ContentTest array_test = new Array(new Event(), "events");
        
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.addNode("events");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("5.5");

        return array_test.run(context, nr, arg_data);
    }
}
