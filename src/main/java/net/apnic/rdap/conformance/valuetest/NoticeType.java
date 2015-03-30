package net.apnic.rdap.conformance.valuetest;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

/**
 * <p>NoticeType class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.4-SNAPSHOT
 */
public final class NoticeType implements ValueTest {
    private static StringSet stringSet =
        new StringSet(
            Sets.newHashSet(
                "result set truncated due to authorization",
                "result set truncated due to excessive load",
                "result set truncated due to unexplainable reasons",
                "object truncated due to authorization",
                "object truncated due to excessive load",
                "object truncated due to unexplainable reasons"
            )
        );

    /**
     * <p>Constructor for NoticeType.</p>
     */
    public NoticeType() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object data) {
        Result r = new Result(proto);
        r.setDocument("rfc7483");
        r.setReference("10.2.1");
        return stringSet.run(context, proto, data);
    }
}
