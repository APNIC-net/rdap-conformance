package net.apnic.rdap.conformance.attributetest;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.valuetest.BooleanValue;

/**
 * <p>ResultsTruncated class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class ResultsTruncated implements AttributeTest {
    /**
     * <p>Constructor for ResultsTruncated.</p>
     */
    public ResultsTruncated() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("10");

        Object rt = Utils.getAttribute(context, nr,
                                       "resultsTruncated",
                                       Status.Notification,
                                       data);
        if (rt == null) {
            return false;
        }

        nr.addNode("resultsTruncated");
        return new BooleanValue().run(context, nr, rt);
    }

    /**
     * <p>getKnownAttributes.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getKnownAttributes() {
        return Sets.newHashSet("resultsTruncated");
    }
}
