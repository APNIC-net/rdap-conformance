package net.apnic.rdap.conformance.contenttest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;

import org.apache.http.conn.util.InetAddressUtils;

public class IPv4Address implements ContentTest
{
    public IPv4Address() {}

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("4");

        boolean res = true;
        Result sr = new Result(nr);
        String ipv4_address = Utils.castToString(arg_data);
        if (ipv4_address == null) {
            sr.setStatus(Status.Failure);
            sr.setInfo("not string");
            res = false;
        } else {
            sr.setStatus(Status.Success);
            sr.setInfo("is string");
        }
        context.addResult(sr);
        if (!res) {
            return false;
        }

        Result vr = new Result(nr);
        if (!InetAddressUtils.isIPv4Address(ipv4_address)) {
            vr.setStatus(Status.Failure);
            vr.setInfo("invalid");
            res = false;
        } else {
            vr.setStatus(Status.Success);
            vr.setInfo("valid");
        }
        context.addResult(vr);

        return res;
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet();
    }
}
