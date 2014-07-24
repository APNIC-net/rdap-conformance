package net.apnic.rdap.conformance.attributetest;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;

/**
 * <p>Autnum class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Autnum implements AttributeTest {
    private BigInteger autnum = null;
    private Set<String> knownAttributes = new HashSet<String>();

    /**
     * <p>Constructor for Autnum.</p>
     */
    public Autnum() { }

    /**
     * <p>Constructor for Autnum.</p>
     *
     * @param argAutnum a {@link java.lang.String} object.
     */
    public Autnum(final String argAutnum) {
        if (argAutnum != null) {
            try {
                this.autnum = new BigInteger(argAutnum);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
    }

    private BigInteger processAutnum(final Context context,
                                     final Result proto,
                                     final Map<String, Object> data,
                                     final String key) {
        Result asnres = new Result(proto);
        Object asnObj = data.get(key);
        if (asnObj == null) {
            asnres.setStatus(Result.Status.Failure);
            asnres.addNode(key);
            asnres.setInfo("not present");
            context.addResult(asnres);
            return null;
        } else {
            asnres.setStatus(Result.Status.Success);
            asnres.addNode(key);
            asnres.setInfo("present");
            context.addResult(asnres);
        }

        Result asn2res = new Result(proto);
        asn2res.setStatus(Result.Status.Success);
        asn2res.addNode(key);
        asn2res.setInfo("valid");
        BigInteger asn = null;
        try {
            asn = BigDecimal.valueOf((Double) data.get(key))
                            .toBigIntegerExact();
        } catch (Exception e) {
            asn2res.setStatus(Result.Status.Failure);
            asn2res.setInfo("invalid: " + e.toString());
            context.addResult(asn2res);
            return null;
        }
        context.addResult(asn2res);

        return asn;
    }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        BigInteger startAddress =
            processAutnum(context, proto, data, "startAutnum");
        BigInteger endAddress =
            processAutnum(context, proto, data, "endAutnum");

        if ((startAddress != null) && (endAddress != null)) {
            Result r = new Result(proto);
            r.addNode("startAutnum");
            r.setStatus(Result.Status.Success);
            r.setInfo("startAutnum less then or equal to endAutnum");
            if (startAddress.compareTo(endAddress) > 0) {
                r.setStatus(Result.Status.Failure);
                r.setInfo("startAutnum more than endAutnum");
            }
            context.addResult(r);

            if (autnum != null) {
                Result r2 = new Result(proto);
                r2.addNode("startAutnum");
                r2.setStatus(Result.Status.Success);
                r2.setInfo("startAutnum and endAutnum bound argument autnum");
                if (!((startAddress.compareTo(autnum) <= 0) 
                     && (endAddress.compareTo(autnum) >= 0))) {
                    r2.setStatus(Result.Status.Failure);
                    r2.setInfo("startAutnum and endAutnum do not bound "
                               + "argument autnum");
                }
                context.addResult(r2);
            }
        }

        knownAttributes = Sets.newHashSet("startAutnum",
                                          "endAutnum");

        return Utils.runTestList(
            context, proto, data, knownAttributes, true,
            Arrays.asList(
                new ScalarAttribute("name"),
                new ScalarAttribute("handle"),
                new ScalarAttribute("type"),
                new Country(),
                new StandardResponse()
            )
        );
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
