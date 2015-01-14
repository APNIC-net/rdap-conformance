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

/**
 * <p>Variant class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Variant implements ValueTest {
    /**
     * <p>Constructor for Variant.</p>
     */
    public Variant() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-14");
        nr.setReference("5.3");

        /* In 6.3, regarding idnTable, the document has 'the name of
         * the IDN table of codepoints, such as one listed with the
         * IANA'. Not sure if this means others might be allowed, so
         * leaving it as ScalarAttribute for now. */

        final Map<String, Object> data = Utils.castToMap(context, nr, argData);
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
