package net.apnic.rdap.conformance.attributetest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;

public class PublicId implements AttributeTest
{
    public PublicId() {}

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);

        AttributeTest sat = new ScalarAttribute("type");
        boolean satres = sat.run(context, proto, data);
        AttributeTest iat = new ScalarAttribute("identifier");
        boolean iatres = iat.run(context, proto, data);

        AttributeTest ua = new UnknownAttributes(getKnownAttributes());
        boolean ret2 = ua.run(context, proto, data);

        return (satres && iatres && ret2);
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet("type", "identifier");
    }
}
