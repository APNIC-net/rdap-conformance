package net.apnic.rdap.conformance.attributetest;

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
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;

public class Variant implements AttributeTest
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

        /* In 6.3, regarding idnTable, the document has 'the name of
         * the IDN table of codepoints, such as one listed with the
         * IANA'. Not sure if this means others might be allowed, so
         * leaving it as ScalarAttribute for now. */

        return Utils.runTestList(
            context, nr, arg_data, known_attributes, true,
            Arrays.asList(
                new VariantRelation(),
                new ScalarAttribute("idnTable"),
                new ArrayAttribute(new VariantName(), "variantNames")
            )
        );
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
