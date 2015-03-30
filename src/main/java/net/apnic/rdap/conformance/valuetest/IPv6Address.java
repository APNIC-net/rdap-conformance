package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

import org.apache.http.conn.util.InetAddressUtils;

/**
 * <p>IPv6Address class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3
 */
public final class IPv6Address implements ValueTest {
    /**
     * <p>Constructor for IPv6Address.</p>
     */
    public IPv6Address() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("rfc7483");
        nr.setReference("3");

        String ipv6Address = Utils.castToString(argData);

        boolean res = nr.setDetails((ipv6Address != null),
                                    "is string",
                                    "not string");
        context.addResult(nr);
        if (!res) {
            return false;
        }

        Result nr2 = new Result(proto);
        res = nr2.setDetails(InetAddressUtils.isIPv6Address(ipv6Address),
                             "valid",
                             "invalid");
        context.addResult(nr2);
        return res;
    }
}
