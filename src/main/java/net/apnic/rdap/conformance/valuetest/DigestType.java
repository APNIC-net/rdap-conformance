package net.apnic.rdap.conformance.valuetest;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

/**
 * <p>DigestType class.</p>
 *
 * See RFC 4034 [A.2] and the documents that update it. The registry
 * is available at
 * http://www.iana.org/assignments/ds-rr-types/ds-rr-types.xml.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3
 */
public final class DigestType implements ValueTest {
    private static final int SHA1        = 1;
    private static final int SHA256      = 2;
    private static final int GOSTR341194 = 3;
    private static final int SHA384      = 4;

    private static IntegerSet integerSet =
        new IntegerSet(Sets.newHashSet(
            SHA1, SHA256, GOSTR341194, SHA384
        ));

    /**
     * <p>Constructor for DigestType.</p>
     */
    public DigestType() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        return integerSet.run(context, proto, argData);
    }
}
