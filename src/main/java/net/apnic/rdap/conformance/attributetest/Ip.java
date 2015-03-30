package net.apnic.rdap.conformance.attributetest;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.List;
import org.apache.http.conn.util.InetAddressUtils;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.valuetest.StringTest;

/**
 * <p>Ip class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.4-SNAPSHOT
 */
public final class Ip implements AttributeTest {
    private String ip = null;
    private Set<String> knownAttributes = new HashSet<String>();

    /**
     * <p>Constructor for Ip.</p>
     */
    public Ip() { }

    /**
     * <p>Constructor for Ip.</p>
     *
     * @param ip a {@link java.lang.String} object.
     */
    public Ip(final String ip) {
        this.ip = ip;
    }

    private String processIpAddress(final Context context,
                                    final Result proto,
                                    final Map<String, Object> data,
                                    final String key) {
        Result ipres = new Result(proto);
        ipres.addNode(key);
        String address = (String) data.get(key);
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

    private long bytesToLong(final byte[] bytes) {
        long value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value = (value << 8) + (bytes[i] & 0xff);
        }
        return value;
    }

    private BigInteger bytesToBigInteger(final byte[] bytes) {
        BigInteger value = BigInteger.valueOf(0);
        for (int i = 0; i < bytes.length; i++) {
            value = value.shiftLeft(8);
            value = value.add(BigInteger.valueOf(bytes[i] & 0xff));
        }
        return value;
    }

    private long addressStringToLong(final String addr) {
        InetAddress obj = null;
        try {
            obj = InetAddress.getByName(addr);
        } catch (UnknownHostException uhe) {
            return -1;
        }
        return bytesToLong(obj.getAddress());
    }

    private BigInteger addressStringToBigInteger(final String addr) {
        InetAddress obj = null;
        try {
            obj = InetAddress.getByName(addr);
        } catch (UnknownHostException uhe) {
            return BigInteger.valueOf(-1);
        }
        return bytesToBigInteger(obj.getAddress());
    }

    private boolean confirmLessThanOrEqualTo(final Context context,
                                             final Result proto,
                                             final String startAddress,
                                             final String endAddress,
                                             final int version) {
        boolean ret = true;
        if (version == 4) {
            long start = addressStringToLong(startAddress);
            long end   = addressStringToLong(endAddress);
            if ((start == -1) || (end == -1)) {
                return false;
            }
            ret = (start <= end);
        } else if (version == 6) {
            BigInteger start = addressStringToBigInteger(startAddress);
            BigInteger end   = addressStringToBigInteger(endAddress);
            if ((start.compareTo(BigInteger.valueOf(-1)) == 0)
               || (end.compareTo(BigInteger.valueOf(-1)) == 0)) {
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

        if (ip != null) {
            int slash = ip.indexOf('/');
            if (version == 4) {
                long start = addressStringToLong(startAddress);
                long end   = addressStringToLong(endAddress);
                if (slash == -1) {
                    long iplong = addressStringToLong(ip);
                    if (iplong != -1) {
                        Result res2 = new Result(proto);
                        res.addNode("startAddress");
                        if ((iplong >= start) && (iplong <= end)) {
                            res2.setStatus(Result.Status.Success);
                            res2.setInfo("startAddress and endAddress bound "
                                         + "argument ip");
                        } else {
                            res2.setStatus(Result.Status.Failure);
                            res2.setInfo("startAddress and endAddress do not bound "
                                         + "argument ip");
                        }
                        context.addResult(res2);
                    }
                } else {
                    String addr = ip.substring(0, slash);
                    long iplong = addressStringToLong(addr);
                    if (iplong != -1) {
                        String prefix = ip.substring(slash + 1);
                        int prefixlen = Integer.valueOf(prefix);
                        long lastip =
                            (iplong + (1 << (32 - prefixlen))) - 1;
                        Result res3 = new Result(proto);
                        res.addNode("startAddress");
                        if ((iplong >= start)
                                && (iplong <= end)
                                && (lastip >= start)
                                && (lastip <= end)) {
                            res3.setStatus(Result.Status.Success);
                            res3.setInfo("startAddress and endAddress bound "
                                         + "argument ip range");
                        } else {
                            res3.setStatus(Result.Status.Failure);
                            res3.setInfo("startAddress and endAddress do not bound "
                                         + "argument ip range");
                        }
                        context.addResult(res3);
                    }
                }
            } else {
                BigInteger start = addressStringToBigInteger(startAddress);
                BigInteger end   = addressStringToBigInteger(endAddress);
                if (slash == -1) {
                    BigInteger iplong = addressStringToBigInteger(ip);
                    if (!(iplong.compareTo(BigInteger.valueOf(-1)) == 0)) {
                        Result res2 = new Result(proto);
                        res.addNode("startAddress");
                        if ((iplong.compareTo(start) >= 0) && (iplong.compareTo(end) <= 0)) {
                            res2.setStatus(Result.Status.Success);
                            res2.setInfo("startAddress and endAddress bound "
                                         + "argument ip");
                        } else {
                            res2.setStatus(Result.Status.Failure);
                            res2.setInfo("startAddress and endAddress do not bound "
                                         + "argument ip");
                        }
                        context.addResult(res2);
                    }
                } else {
                    String addr = ip.substring(0, slash);
                    BigInteger iplong = addressStringToBigInteger(addr);
                    if (!(iplong.compareTo(BigInteger.valueOf(-1)) == 0)) {
                        String prefix = ip.substring(slash + 1);
                        int prefixlen = Integer.valueOf(prefix);
                        BigInteger size = BigInteger.valueOf(1);
                        BigInteger lastip = iplong.add(size.shiftLeft(128 - prefixlen))
                                                  .subtract(BigInteger.valueOf(1));
                        Result res3 = new Result(proto);
                        res.addNode("startAddress");
                        if ((iplong.compareTo(start) >= 0)
                                && (iplong.compareTo(end) <= 0)
                                && (lastip.compareTo(start) >= 0)
                                && (lastip.compareTo(end) <= 0)) {
                            res3.setStatus(Result.Status.Success);
                            res3.setInfo("startAddress and endAddress bound "
                                         + "argument ip range");
                        } else {
                            res3.setStatus(Result.Status.Failure);
                            res3.setInfo("startAddress and endAddress do not bound "
                                         + "argument ip range");
                        }
                        context.addResult(res3);
                    }
                }
            }
        }

        return ret;
    }

    private boolean confirmParentHandleMatchesParent(
                final Context context,
                final Result proto,
                final Map<String, Object> data) {
        Result pres = new Result(proto);
        pres.addNode("parentHandle");
        String parentHandle = Utils.castToString(data.get("parentHandle"));
        if (parentHandle == null) {
            return true;
        }

        List<Map<String, String>> links = null;
        try {
            links = (List<Map<String, String>>) data.get("links");
        } catch (ClassCastException cce) {
            return true;
        }
        if (links == null) {
            return true;
        }

        Map<String, String> upLink = null;
        for (Map<String, String> link : links) {
            String rel = link.get("rel");
            if ((rel != null) && rel.equals("up")) {
                upLink = link;
                break;
            }
        }

        /* Inability to fetch the link, or link invalidity, will be
         * caught by the Links content test. */
        if (upLink == null) {
            return true;
        }

        String href = upLink.get("href");
        if (href == null) {
            return true;
        }

        Result parentProto = new Result(proto);
        parentProto.setPath(href);
        Map proot = Utils.standardRequest(context, href, parentProto);
        if (proot == null) {
            return true;
        }

        String handle = (String) proot.get("handle");
        if (handle == null) {
            return true;
        }

        Result res = new Result(proto);
        res.addNode("parentHandle");
        if (handle.equals(parentHandle)) {
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

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        boolean ret = true;

        int version = 0;
        String startAddress =
            processIpAddress(context, proto, data, "startAddress");
        String endAddress =
            processIpAddress(context, proto, data, "endAddress");
        if ((startAddress != null) && (endAddress != null)) {
            Result typesMatch = new Result(proto);
            typesMatch.addNode("startAddress");
            typesMatch.setInfo("start and end address types match");
            typesMatch.setStatus(Result.Status.Success);
            if (InetAddressUtils.isIPv4Address(startAddress)
                    && InetAddressUtils.isIPv4Address(endAddress)) {
                context.addResult(typesMatch);
                version = 4;
            } else if (InetAddressUtils.isIPv6Address(startAddress)
                    && InetAddressUtils.isIPv6Address(endAddress)) {
                context.addResult(typesMatch);
                version = 6;
            } else {
                typesMatch.setInfo(
                    "start and end address types do not match"
                );
                typesMatch.setStatus(Result.Status.Failure);
                context.addResult(typesMatch);
                ret = false;
            }
        }

        if (version != 0) {
            boolean cltret =
                confirmLessThanOrEqualTo(
                    context, proto, startAddress, endAddress, version
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
                    context, proto, data
                );
            if (!pret) {
                ret = false;
            }
        }

        Result vres = new Result(proto);
        vres.addNode("ipVersion");
        String ipversion = Utils.castToString(data.get("ipVersion"));
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
            int checkVersion = ipversion.equals("v4") ? 4
                             : ipversion.equals("v6") ? 6
                                                      : 0;
            if (checkVersion == 0) {
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
                    if (version == checkVersion) {
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

        knownAttributes = Sets.newHashSet(
            "startAddress", "endAddress", "ipVersion"
        );

        return (Utils.runTestList(
            context, proto, data, knownAttributes, false,
            Arrays.asList(
                new ScalarAttribute("objectClassName",
                                    new StringTest("ip network"),
                                    Result.Status.Failure),
                new ScalarAttribute("name"),
                new ScalarAttribute("handle"),
                new ScalarAttribute("type"),
                new Country(),
                new ScalarAttribute("parentHandle"),
                new StandardObject()
            )
        ) && ret);
    }

    /**
     * <p>Getter for the field <code>knownAttributes</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getKnownAttributes() {
        return knownAttributes;
    }
}
