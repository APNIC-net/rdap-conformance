package net.apnic.rdap.conformance.attributetest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import java.math.BigInteger;
import java.math.BigDecimal;
import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;

import java.net.Socket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;

public class Port43 implements AttributeTest
{
    private static Set<String> hosts_checked = new HashSet<String>();

    public Port43() {}

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("5.7");

        String port43 = Utils.getStringAttribute(context, nr, "port43",
                                                 Status.Notification,
                                                 data);
        if (port43 == null) {
            return true;
        }
        if (hosts_checked.contains(port43)) {
            return true;
        }
        hosts_checked.add(port43);

        Result nr3 = new Result(nr);
        nr3.setStatus(Status.Success);
        nr3.setInfo("server is accessible");
        boolean found = true;
        try {
            Socket socket = new Socket();
            InetAddress addr = InetAddress.getByName(port43);
            socket.connect(new InetSocketAddress(addr, 43), 1000);
            socket.close();
        } catch (Exception ex) {
            nr3.setStatus(Status.Failure);
            nr3.setInfo("server is inaccessible: " + ex.toString());
            found = false;
        }
        results.add(nr3);

        return found;
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet("port43");
    }
}
