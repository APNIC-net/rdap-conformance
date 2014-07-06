package net.apnic.rdap.conformance.contenttest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Locale;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.ContentTest;

import java.util.IllformedLocaleException;
import java.util.Locale;

public class Lang implements ContentTest
{
    public Lang() {}

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.addNode("lang");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("5.4");

        Map<String, Object> data = Utils.castToMap(context, nr, arg_data);
        if (data == null) {
            return false;
        }

        String lang = Utils.getStringAttribute(context,
                                               nr, "lang",
                                               null,
                                               data);
        if (lang == null) {
            return false;
        }

        Result vr = new Result(nr);
        boolean res = true;
        try {
            Locale.Builder hlt =
                new Locale.Builder().setLanguageTag(lang);
        } catch (IllformedLocaleException e) {
            vr.setStatus(Status.Failure);
            vr.setInfo("invalid: " + e.toString());
            res = false;
        }
        if (res) {
            vr.setStatus(Status.Success);
            vr.setInfo("valid");
        }
        context.addResult(vr);

        return res;
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet("lang");
    }
}
