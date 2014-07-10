package net.apnic.rdap.conformance.attributetest;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.AttributeTest;

import java.util.IllformedLocaleException;
import java.util.Locale;

public final class Lang implements AttributeTest {
    public Lang() { }

    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.addNode("lang");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("5.4");

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

    public Set<String> getKnownAttributes() {
        return Sets.newHashSet("lang");
    }
}
