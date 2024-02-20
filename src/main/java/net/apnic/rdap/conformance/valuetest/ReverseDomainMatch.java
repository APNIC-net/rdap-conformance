package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;
import net.apnic.rdap.conformance.Utils;

import net.ripe.ipresource.IpResource;

import java.util.*;

/**
 * <p>ReverseDomainMatch class.</p>
 *
 * See RFC 9083 [5.3].
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.7-SNAPSHOT
 */
public final class ReverseDomainMatch implements ValueTest {
    /**
     * <p>Constructor for ReverseDomainMatch.</p>
     */
    public ReverseDomainMatch() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        Map<String, Object> object =
            Utils.castToMap(context, proto, argData);
        if (object == null) {
            return false;
        }

        /* Get the domain name for this object. */
        String domainName = Utils.castToString(object.get("ldhName"));
        if (domainName == null) {
            return true;
        }
        if (!domainName.matches(".*\\.arpa\\.?")) {
            return true;
        }
        String prefix = Utils.arpaToPrefix(domainName);
        if (prefix.equals("")) {
            Result res = new Result(proto);
            res.setStatus(Result.Status.Failure);
            res.setInfo("invalid reverse domain name");
            context.addResult(res);
            return false;
        }

        IpResource domainIp;
        try {
            domainIp = IpResource.parse(prefix);
        } catch (Exception e) {
            Result res = new Result(proto);
            res.setStatus(Result.Status.Failure);
            res.setInfo("invalid reverse domain name");
            context.addResult(res);
            return false;
        }

        /* Get the network for this object (if present). */
        Map<String, Object> networkObject =
            Utils.castToMap(context, proto, object.get("network"));
        if (networkObject == null) {
            return true;
        }

        String startAddress =
            Utils.castToString(networkObject.get("startAddress"));
        if (startAddress == null) {
            Result res = new Result(proto);
            res.setStatus(Result.Status.Failure);
            res.setInfo("network does not contain start address");
            context.addResult(res);
            return false;
        }
        String endAddress =
            Utils.castToString(networkObject.get("endAddress"));
        if (endAddress == null) {
            Result res = new Result(proto);
            res.setStatus(Result.Status.Failure);
            res.setInfo("network does not contain end address");
            context.addResult(res);
            return false;
        }

        IpResource networkIp;
        try {
            networkIp = IpResource.parse(startAddress + "-" +
                                         endAddress);
        } catch (Exception e) {
            Result res = new Result(proto);
            res.setStatus(Result.Status.Failure);
            res.setInfo("invalid network start/end address");
            context.addResult(res);
            return false;
        }

        /* Confirm that the domain range is contained within the
         * network. */
        Result res = new Result(proto);
        boolean contains = networkIp.contains(domainIp);
        if (contains) {
            res.setStatus(Result.Status.Success);
        } else {
            res.setStatus(Result.Status.Failure);
        }
        res.setInfo("network range contains domain range");
        context.addResult(res);
        return contains;
    }
}
