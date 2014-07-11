package net.apnic.rdap.conformance.test.autnum;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.Map;
import java.util.List;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ObjectTest;
import net.apnic.rdap.conformance.attributetest.Country;
import net.apnic.rdap.conformance.attributetest.ScalarAttribute;
import net.apnic.rdap.conformance.attributetest.StandardResponse;

/**
 * <p>Standard class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
 */
public final class Standard implements ObjectTest {
    private String autnum = null;
    private String url = null;

    /**
     * <p>Constructor for Standard.</p>
     */
    public Standard() { }

    /**
     * <p>Constructor for Standard.</p>
     *
     * @param autnum a {@link java.lang.String} object.
     */
    public Standard(final String autnum) {
        this.autnum = autnum;
    }

    /** {@inheritDoc} */
    public void setUrl(final String url) {
        autnum = null;
        this.url = url;
    }

    private BigInteger processAutnum(final Context context,
                                     final Result proto,
                                     final Map root,
                                     final String key) {
        Result asnres = new Result(proto);
        Object asnObj = root.get(key);
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
            asn = BigDecimal.valueOf((Double) root.get(key))
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
    public boolean run(final Context context) {
        List<Result> results = context.getResults();

        String path =
            (url != null)
                ? url
                : context.getSpecification().getBaseUrl()
                    + "/autnum/" + autnum;

        Result proto = new Result(Result.Status.Notification, path,
                                  "autnum.standard",
                                  "content", "",
                                  "draft-ietf-weirds-json-response-07",
                                  "6.5");
        Result r = new Result(proto);
        r.setCode("response");
        Map root = Utils.standardRequest(context, path, r);
        if (root == null) {
            return false;
        }

        BigInteger startAddress =
            processAutnum(context, proto, root, "startAutnum");
        BigInteger endAddress =
            processAutnum(context, proto, root, "endAutnum");

        if ((startAddress != null) && (endAddress != null)) {
            r = new Result(proto);
            r.addNode("startAutnum");
            r.setStatus(Result.Status.Success);
            r.setInfo("startAutnum less then or equal to endAutnum");
            if (startAddress.compareTo(endAddress) > 0) {
                r.setStatus(Result.Status.Failure);
                r.setInfo("startAutnum more than endAutnum");
                results.add(r);
            } else {
                results.add(r);
            }
        }

        Set<String> knownAttributes = Sets.newHashSet("startAutnum",
                                                       "endAutnum");
        Map<String, Object> data = Utils.castToMap(context, proto, root);
        if (data == null) {
            return false;
        }
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
}
