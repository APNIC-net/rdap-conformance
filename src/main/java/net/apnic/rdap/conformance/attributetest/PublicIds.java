package net.apnic.rdap.conformance.attributetest;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;

/**
 * <p>PublicIds class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3
 */
public final class PublicIds implements AttributeTest {
    /**
     * <p>Constructor for PublicIds.</p>
     */
    public PublicIds() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        AttributeTest arrayTest =
            new ArrayAttribute(new PublicId(), "publicIds");

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("rfc7483");
        nr.setReference("4.8");

        return arrayTest.run(context, nr, data);
    }

    /**
     * <p>getKnownAttributes.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getKnownAttributes() {
        return Sets.newHashSet("publicIds");
    }
}
