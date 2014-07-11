package net.apnic.rdap.conformance.attributetest;

import java.net.Socket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;

/**
 * <p>Port43 class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
 */
public final class Port43 implements AttributeTest {
    private static Set<String> hostsChecked = new HashSet<String>();
    private static final int TIMEOUT_MS = 1000;
    private static final int WHOIS_PORT = 43;

    /**
     * <p>Constructor for Port43.</p>
     */
    public Port43() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("5.7");

        String port43 = Utils.getStringAttribute(context, nr, "port43",
                                                 Status.Notification,
                                                 data);
        if (port43 == null) {
            return true;
        }
        if (hostsChecked.contains(port43)) {
            return true;
        }
        hostsChecked.add(port43);

        Result nr3 = new Result(nr);
        nr3.setStatus(Status.Success);
        nr3.setInfo("server is accessible");
        boolean found = true;
        try {
            Socket socket = new Socket();
            InetAddress addr = InetAddress.getByName(port43);
            socket.connect(new InetSocketAddress(addr, WHOIS_PORT),
                           TIMEOUT_MS);
            socket.close();
        } catch (Exception ex) {
            nr3.setStatus(Status.Failure);
            nr3.setInfo("server is inaccessible: " + ex.toString());
            found = false;
        }
        results.add(nr3);

        return found;
    }

    /**
     * <p>getKnownAttributes.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getKnownAttributes() {
        return Sets.newHashSet("port43");
    }
}
