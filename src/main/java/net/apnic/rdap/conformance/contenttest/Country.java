package net.apnic.rdap.conformance.contenttest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Locale;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.ContentTest;

public class Country implements ContentTest
{
    private static final Set<String> country_codes = new HashSet<String>();
    static {
        String[] array_country_codes = Locale.getISOCountries();
        for (String country_code : array_country_codes) {
            country_codes.add(country_code);
        }
    }

    public Country() {}

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.addNode("country");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("4");

        Map<String, Object> data = Utils.castToMap(context, nr, arg_data);
        if (data == null) {
            return false;
        }

        Result nr1 = new Result(nr);
        nr1.setInfo("present");

        String country_value =
            Utils.getStringAttribute(context, nr1, "country",
                                     Result.Status.Notification,
                                     data);
        if (country_value == null) {
            return false;
        }

        Result nr3 = new Result(nr);
        if (!country_codes.contains(country_value)) {
            nr3.setStatus(Result.Status.Failure);
            nr3.setInfo("invalid: " + country_value);
            results.add(nr3);
            return false;
        } else {
            nr3.setStatus(Result.Status.Success);
            nr3.setInfo("valid");
            results.add(nr3);
        }

        return true;
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet("country");
    }
}
