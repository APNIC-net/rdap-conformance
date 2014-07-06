package net.apnic.rdap.conformance.contenttest;

import java.util.Map;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.Utils;

public class Notice implements ContentTest
{
    public Notice() {}

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("content");

        Map<String, Object> data = Utils.castToMap(context, nr, arg_data);
        if (data == null) {
            return false;
        }

        ContentTest sat = new ScalarAttribute("title");
        boolean satres = sat.run(context, nr, arg_data);
        ContentTest aat = new ArrayAttribute(new StringTest(), "description");
        boolean aatres = aat.run(context, nr, arg_data);
        ContentTest lst = new Links();
        boolean lstres = lst.run(context, nr, arg_data);

        ContentTest ua = new UnknownAttributes(getKnownAttributes());
        boolean ret2 = ua.run(context, proto, arg_data);

        return (satres && aatres && lstres && ret2);
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet("title", "description", "links");
    }
}
