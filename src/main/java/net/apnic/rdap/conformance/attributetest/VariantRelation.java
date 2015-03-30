package net.apnic.rdap.conformance.attributetest;

import java.util.Map;
import java.util.Set;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.ValueTest;

/**
 * <p>VariantRelation class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3
 */
public final class VariantRelation implements AttributeTest {
    private final ValueTest variantRelationValueTest =
        new net.apnic.rdap.conformance.valuetest.VariantRelation();
    private final AttributeTest arrayAttributeTest =
        new ArrayAttribute(variantRelationValueTest, "relation");

    /**
     * <p>Constructor for VariantRelation.</p>
     */
    public VariantRelation() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.addNode("status");
        nr.setDocument("rfc7483");
        nr.setReference("5.3");

        return arrayAttributeTest.run(context, nr, data);
    }

    /**
     * <p>getKnownAttributes.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getKnownAttributes() {
        return arrayAttributeTest.getKnownAttributes();
    }
}
