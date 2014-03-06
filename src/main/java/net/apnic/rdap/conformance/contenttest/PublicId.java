package net.apnic.rdap.conformance.contenttest;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.contenttest.ScalarAttribute;
import net.apnic.rdap.conformance.contenttest.ArrayAttribute;
import net.apnic.rdap.conformance.contenttest.Links;

public class PublicId implements ContentTest
{
    public PublicId() {}

    public boolean run(Context context, Result proto, 
                       Object arg_data)
    {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);

        Map<String, Object> data;
        try {
            data = (Map<String, Object>) arg_data;
        } catch (ClassCastException e) {
            nr.setInfo("structure is invalid");
            nr.setStatus(Status.Failure);
            results.add(nr);
            return false;
        }

        ContentTest sat = new ScalarAttribute("type");
        boolean satres = sat.run(context, proto, arg_data);
        ContentTest iat = new ScalarAttribute("identifier");
        boolean iatres = iat.run(context, proto, arg_data);

        return (satres && iatres);
    }
}
