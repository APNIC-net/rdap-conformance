package net.apnic.rdap.conformance.test.ip;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.List;
import org.apache.http.conn.util.InetAddressUtils;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ObjectTest;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.contenttest.Status;
import net.apnic.rdap.conformance.contenttest.Country;
import net.apnic.rdap.conformance.contenttest.ScalarAttribute;
import net.apnic.rdap.conformance.contenttest.StandardResponse;
import net.apnic.rdap.conformance.contenttest.UnknownAttributes;

public class Standard implements ObjectTest
{
    String ip = null;
    String url = null;

    public Standard() {}

    public Standard(String ip)
    {
        this.ip = ip;
    }

    public void setUrl(String url)
    {
        ip = null;
        this.url = url;
    }

    private String processIpAddress(Context context, Result proto,
                                    Map root, String key)
    {
        Result ipres = new Result(proto);
        ipres.addNode(key);
        String address = (String) root.get(key);
        if (address == null) {
            ipres.setStatus(Result.Status.Failure);
            ipres.setInfo("not present");
            context.addResult(ipres);
        } else {
            ipres.setStatus(Result.Status.Success);
            ipres.setInfo("present");
            context.addResult(ipres);

            Result ipvalid = new Result(proto);
            ipvalid.addNode(key);
            ipvalid.setStatus(Result.Status.Success);
            ipvalid.setInfo("valid");
            if (!(InetAddressUtils.isIPv4Address(address)
                    || InetAddressUtils.isIPv6Address(address))) {
                ipvalid.setStatus(Result.Status.Failure);
                ipvalid.setInfo("invalid");
                context.addResult(ipvalid);
            } else {
                ipvalid.setStatus(Result.Status.Success);
                ipvalid.setInfo("valid");
                context.addResult(ipvalid);
            }
        }
        return address;
    }

    private long bytesToLong(byte[] bytes)
    {
        long value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value = (value << 8) + (bytes[i] & 0xff);
        }
        return value;
    }

    private BigInteger bytesToBigInteger(byte[] bytes)
    {
        BigInteger value = BigInteger.valueOf(0);
        for (int i = 0; i < bytes.length; i++) {
            value.shiftLeft(8);
            value.add(BigInteger.valueOf(bytes[i] & 0xff));
        }
        return value;
    }

    private long addressStringToLong(String addr)
    {
        InetAddress obj = null;
        try {
            obj = InetAddress.getByName(addr);
        } catch (UnknownHostException uhe) {
            return -1;
        }
        return bytesToLong(obj.getAddress());
    }

    private BigInteger addressStringToBigInteger(String addr)
    {
        InetAddress obj = null;
        try {
            obj = InetAddress.getByName(addr);
        } catch (UnknownHostException uhe) {
            return BigInteger.valueOf(-1);
        }
        return bytesToBigInteger(obj.getAddress());
    }

    private boolean confirmLessThanOrEqualTo(Context context, Result proto,
                                             String start_address,
                                             String end_address,
                                             int version)
    {
        boolean ret = true;
        if (version == 4) {
            long start = addressStringToLong(start_address);
            long end   = addressStringToLong(end_address);
            if ((start == -1) || (end == -1)) {
                return false;
            }
            ret = (start <= end);
        } else if (version == 6) {
            BigInteger start = addressStringToBigInteger(start_address);
            BigInteger end   = addressStringToBigInteger(end_address);
            if ((start.equals(-1)) || (end.equals(-1))) {
                return false;
            }
            ret = (start.compareTo(end) <= 0);
        }

        Result res = new Result(proto);
        res.addNode("startAddress");
        if (ret) {
            res.setStatus(Result.Status.Success);
            res.setInfo("start address is less than or " +
                        "equal to end address");
        } else {
            res.setStatus(Result.Status.Failure);
            res.setInfo("start address is greater than end address");
        }
        context.addResult(res);

        return ret;
    }

    private boolean confirmParentHandleMatchesParent(Context context,
                                                     Result proto,
                                                     Map root)
    {
        Result pres = new Result(proto);
        pres.addNode("parentHandle");
        String parent_handle = Utils.castToString(root.get("parentHandle"));
        if (parent_handle == null) {
            return true;
        }

        List<Map<String, String>> links = null;
        try {
            links = (List<Map<String, String>>) root.get("links");
        } catch (ClassCastException cce) {
            return true;
        }

        Map<String, String> up_link = null;
        for (Map<String, String> link : links) {
            String rel = link.get("rel");
            if ((rel != null) && rel.equals("up")) {
                up_link = link;
                break;
            }
        }

        /* Inability to fetch the link, or link invalidity, will be
         * caught by the Links content test. */
        if (up_link == null) {
            return true;
        }

        String href = up_link.get("href");
        if (href == null) {
            return true;
        }

        Result parent_proto = new Result(proto);
        parent_proto.setPath(href);
        Map proot = Utils.standardRequest(context, href, parent_proto);
        if (proot == null) {
            return true;
        }

        String handle = (String) proot.get("handle");
        if (handle == null) {
            return true;
        }

        Result res = new Result(proto);
        res.addNode("parentHandle");
        if (handle.equals(parent_handle)) {
            res.setStatus(Result.Status.Success);
            res.setInfo("parentHandle matches parent handle");
            context.addResult(res);
            return true;
        } else {
            res.setStatus(Result.Status.Failure);
            res.setInfo("parentHandle does not match parent handle");
            context.addResult(res);
            return false;
        }
    }

    public boolean run(Context context)
    {
        boolean ret = true;
        List<Result> results = context.getResults();

        String path =
            (url != null)
                ? url
                : context.getSpecification().getBaseUrl() + "/ip/" + ip;

        Result proto = new Result(Result.Status.Notification, path,
                                  "ip.standard",
                                  "content", "",
                                  "draft-ietf-weirds-json-response-06",
                                  "6.4");

        proto.setCode("content");
        Result r = new Result(proto);
        r.setCode("response");
        Map root = Utils.standardRequest(context, path, r);
        if (root == null) {
            return false;
        }

        int version = 0;
        String start_address =
            processIpAddress(context, proto, root, "startAddress");
        String end_address =
            processIpAddress(context, proto, root, "endAddress");
        if ((start_address != null) && (end_address != null)) {
            Result types_match = new Result(proto);
            types_match.addNode("startAddress");
            types_match.setInfo("start and end address types match");
            types_match.setStatus(Result.Status.Success);
            if (InetAddressUtils.isIPv4Address(start_address)
                    && InetAddressUtils.isIPv4Address(end_address)) {
                context.addResult(types_match);
                version = 4;
            } else if (InetAddressUtils.isIPv6Address(start_address)
                    && InetAddressUtils.isIPv6Address(end_address)) {
                context.addResult(types_match);
                version = 6;
            } else {
                types_match.setInfo(
                    "start and end address types do not match"
                );
                types_match.setStatus(Result.Status.Failure);
                context.addResult(types_match);
                ret = false;
            }
        }

        if (version != 0) {
            boolean cltret =
                confirmLessThanOrEqualTo(
                    context, proto, start_address, end_address, version
                );
            if (!cltret) {
                ret = false;
            }
            /* Issue #12 (IP objects), specified that the
             * "parentHandle should match the handle of the
             * next-largest object". However, it's not possible in all
             * cases to determine the parent object for a given IP
             * object from the start and end addresses alone, so the
             * following method relies on the "up" link (if present)
             * to determine that instead. */
            boolean pret =
                confirmParentHandleMatchesParent(
                    context, proto, root
                );
            if (!pret) {
                ret = false;
            }
        }

        Result vres = new Result(proto);
        vres.addNode("ipVersion");
        String ipversion = Utils.castToString(root.get("ipVersion"));
        if (ipversion == null) {
            vres.setStatus(Result.Status.Failure);
            vres.setInfo("not present");
            context.addResult(vres);
            ret = false;
        } else {
            vres.setStatus(Result.Status.Success);
            vres.setInfo("present");
            context.addResult(vres);
            Result vres2 = new Result(vres);
            int check_version = ipversion.equals("v4") ? 4
                              : ipversion.equals("v6") ? 6
                                                       : 0;
            if (check_version == 0) {
                vres2.setStatus(Result.Status.Failure);
                vres2.setInfo("invalid: " + ipversion);
                context.addResult(vres2);
                ret = false;
            } else {
                vres2.setStatus(Result.Status.Success);
                vres2.setInfo("valid");
                context.addResult(vres2);
                if (version != 0) {
                    Result vres3 = new Result(vres);
                    if (version == check_version) {
                        vres3.setStatus(Result.Status.Success);
                        vres3.setInfo("matches address version");
                        context.addResult(vres3);
                    } else {
                        vres3.setStatus(Result.Status.Failure);
                        vres3.setInfo("does not match address version");
                        context.addResult(vres3);
                        ret = false;
                    }
                }
            }
        }

        List<ContentTest> tests =
            new ArrayList<ContentTest>(Arrays.asList(
                new ScalarAttribute("name"),
                new ScalarAttribute("handle"),
                new ScalarAttribute("type"),
                new Country(),
                new ScalarAttribute("parentHandle"),
                new StandardResponse()
            ));

        Set<String> known_attributes = new HashSet<String>();

        for (ContentTest test : tests) {
            boolean res = test.run(context, proto, root);
            if (!res) {
                ret = false;
            }
            known_attributes.addAll(test.getKnownAttributes());
        }
        known_attributes.addAll(Sets.newHashSet("startAddress",
                                                "endAddress", "ipVersion"));

        ContentTest ua = new UnknownAttributes(known_attributes);
        boolean ret2 = ua.run(context, proto, root);
        return (ret && ret2);
    }
}
