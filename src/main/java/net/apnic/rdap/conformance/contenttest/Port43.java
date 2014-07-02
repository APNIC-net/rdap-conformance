package net.apnic.rdap.conformance.contenttest;

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
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.Utils;

import java.net.Socket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;

public class Port43 implements ContentTest
{
    private static Set<String> hosts_checked = new HashSet<String>();

    public Port43() {}

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.addNode("port43");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("5.7");

        Map<String, Object> data;
        try {
            data = (Map<String, Object>) arg_data;
        } catch (ClassCastException e) {
            nr.setInfo("structure is invalid");
            nr.setStatus(Status.Failure);
            context.addResult(nr);
            return false;
        }

        Result nr1 = new Result(nr);
        nr1.setInfo("present");

        Object value = data.get("port43");
        if (value == null) {
            nr1.setStatus(Status.Notification);
            nr1.setInfo("not present");
            results.add(nr1);
            return false;
        } else {
            nr1.setStatus(Status.Success);
            results.add(nr1);
        }

        Result nr2 = new Result(nr);
        nr2.setInfo("is a string");

        String port43;
        try {
            port43 = (String) value;
        } catch (ClassCastException e) {
            nr2.setStatus(Status.Failure);
            nr2.setInfo("is not a string");
            results.add(nr2);
            return false;
        }
        nr2.setStatus(Status.Success);
        results.add(nr2);

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
