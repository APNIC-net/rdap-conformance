package net.apnic.rdap.conformance.contenttest;

import java.util.Map;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.Utils;

public class Notice implements ContentTest
{
    private Set<String> known_attributes = new HashSet<String>();

    public Notice() {}

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("content");

        return Utils.runTestList(
            context, nr, arg_data, known_attributes, true,
            Arrays.asList(
                new ScalarAttribute("title"),
                new ArrayAttribute(new StringTest(), "description"),
                new Links()
            )
        );
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
