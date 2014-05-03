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

        Object value = data.get("country");
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
        String country_value = Utils.castToString(value);
        if (country_value == null) {
            nr2.setStatus(Result.Status.Failure);
            nr2.setInfo("is not string");
            results.add(nr2);
            return false;
        } else {
            nr2.setStatus(Result.Status.Success);
            nr2.setInfo("is string");
            results.add(nr2);
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
