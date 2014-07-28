package net.apnic.rdap.conformance.valuetest;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

/**
 * <p>MediaType class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class MediaType implements ValueTest {
    /* The first nine are defined by HTML 4.01, and the last two by
     * CSS 2. */
    private static StringSet stringSet =
        new StringSet(
            Sets.newHashSet("aural",
                            "braille",
                            "handheld",
                            "print",
                            "projection",
                            "screen",
                            "tty",
                            "tv",
                            "all",
                            "embossed",
                            "speech")
        );

    /**
     * <p>Constructor for MediaType.</p>
     */
    public MediaType() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object data) {
        return stringSet.run(context, proto, data);
    }
}
