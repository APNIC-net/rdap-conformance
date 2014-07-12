package net.apnic.rdap.conformance.valuetest;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

/**
 * <p>Algorithm class.</p>
 *
 * See RFC 4034 [A.1] and the documents that update it. The registry
 * is available at
 * http://www.iana.org/assignments/dns-sec-alg-numbers/dns-sec-alg-numbers.xml.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Algorithm implements ValueTest {
    private static final int DSA        = 3;
    private static final int RSASHA1    = 5;
    private static final int PRIVATEDNS = 253;
    private static final int PRIVATEOID = 254;

    private static final int DSA_NSEC3_SHA1     = 6;
    private static final int RSASHA1_NSEC3_SHA1 = 7;
    private static final int RSASHA256          = 8;
    private static final int RSASHA512          = 10;
    private static final int ECC_GOST           = 12;
    private static final int ECDSAP256SHA256    = 13;
    private static final int ECDSAP384SHA384    = 14;

    private static IntegerSet integerSet =
        new IntegerSet(Sets.newHashSet(
            DSA, RSASHA1, PRIVATEDNS, PRIVATEOID,
            DSA_NSEC3_SHA1, RSASHA1_NSEC3_SHA1, RSASHA256,
            RSASHA512, ECC_GOST, ECDSAP256SHA256, ECDSAP384SHA384
        ));

    /**
     * <p>Constructor for Algorithm.</p>
     */
    public Algorithm() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        return integerSet.run(context, proto, argData);
    }
}
