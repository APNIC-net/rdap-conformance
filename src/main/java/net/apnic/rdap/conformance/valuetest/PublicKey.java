package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

/**
 * <p>PublicKey class.</p>
 *
 * See RFC 4034 [2.1.4].
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
 */
public final class PublicKey implements ValueTest {
    /**
     * <p>Constructor for PublicKey.</p>
     */
    public PublicKey() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        return new HexString().run(context, proto, argData);
    }
}
