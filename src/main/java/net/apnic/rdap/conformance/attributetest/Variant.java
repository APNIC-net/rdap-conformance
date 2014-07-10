package net.apnic.rdap.conformance.valuetest;

import java.util.Map;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.attributetest.VariantRelation;
import net.apnic.rdap.conformance.attributetest.ScalarAttribute;
import net.apnic.rdap.conformance.attributetest.ArrayAttribute;
import net.apnic.rdap.conformance.attributetest.VariantName;

public class Variant implements ValueTest
{
    public Variant() {}

    public boolean run(Context context, Result proto,
                       Object argData)
    {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("6.3");

        /* In 6.3, regarding idnTable, the document has 'the name of
         * the IDN table of codepoints, such as one listed with the
         * IANA'. Not sure if this means others might be allowed, so
         * leaving it as ScalarAttribute for now. */

        Map<String, Object> data = Utils.castToMap(context, nr, argData);
        if (data == null) {
            return false;
        }

        Set<String> knownAttributes = new HashSet<String>();
        return Utils.runTestList(
            context, nr, data, knownAttributes, true,
            Arrays.asList(
                new VariantRelation(),
                new ScalarAttribute("idnTable"),
                new ArrayAttribute(new VariantName(), "variantNames")
            )
        );
    }
}
