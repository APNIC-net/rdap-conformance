package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

import org.apache.http.conn.util.InetAddressUtils;

public class IPv4Address implements ValueTest
{
    public IPv4Address() {}

    public boolean run(Context context, Result proto,
                       Object argData)
    {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("4");

        String ipv4Address = Utils.castToString(argData);

        boolean res = nr.setDetails((ipv4Address != null),
                                    "is string",
                                    "not string");
        context.addResult(nr);
        if (!res) {
            return false;
        }

        Result nr2 = new Result(proto);
        res = nr2.setDetails(InetAddressUtils.isIPv4Address(ipv4Address),
                             "valid",
                             "invalid");
        context.addResult(nr2);
        return res;
    }
}
