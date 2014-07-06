package net.apnic.rdap.conformance.contenttest;

import java.util.Map;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.contenttest.VariantRelation;
import net.apnic.rdap.conformance.contenttest.VariantName;
import net.apnic.rdap.conformance.contenttest.UnknownAttributes;

public class Variant implements ContentTest
{
    Set<String> known_attributes = null;

    public Variant() {}

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("6.3");

        boolean res = true;
        Map<String, Object> data;
        try {
            data = (Map<String, Object>) arg_data;
        } catch (ClassCastException e) {
            nr.setInfo("structure is invalid");
            nr.setStatus(Status.Failure);
            context.addResult(nr);
            return false;
        }

        /* In 6.3, regarding idnTable, the document has 'the name of
         * the IDN table of codepoints, such as one listed with the
         * IANA'. Not sure if this means others might be allowed, so
         * leaving it as ScalarAttribute for now. */
        List<ContentTest> tests =
            new ArrayList<ContentTest>(Arrays.asList(
                new VariantRelation(),
                new ScalarAttribute("idnTable"),
                new ArrayAttribute(new VariantName(), "variantNames")
            ));

        known_attributes = new HashSet<String>();
        for (ContentTest test : tests) {
            boolean res_inner = test.run(context, proto, arg_data);
            if (!res_inner) {
                res = false;
            }
            known_attributes.addAll(test.getKnownAttributes());
        }

        ContentTest ua = new UnknownAttributes(known_attributes);
        boolean ret2 = ua.run(context, proto, arg_data);
        return (res && ret2);
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
