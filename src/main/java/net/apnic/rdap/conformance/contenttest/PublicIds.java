package net.apnic.rdap.conformance.contenttest;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.ContentTest;

import net.apnic.rdap.conformance.contenttest.PublicId;
import net.apnic.rdap.conformance.contenttest.Array;

public class PublicIds implements ContentTest
{
    public PublicIds() {}

    public boolean run(Context context, Result proto, 
                       Object arg_data)
    {
        ContentTest array_test = new Array(new PublicId(), "publicIds");

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.addNode("publicIds");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("5.8");

        return array_test.run(context, nr, arg_data);
    }
}
