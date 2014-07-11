package net.apnic.rdap.conformance.test.ip;

import java.util.Map;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ObjectTest;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.attributetest.Ip;

/**
 * <p>Standard class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
 */
public final class Standard implements ObjectTest {
    private String ip = null;
    private String url = null;

    /**
     * <p>Constructor for Standard.</p>
     */
    public Standard() { }

    /**
     * <p>Constructor for Standard.</p>
     *
     * @param ip a {@link java.lang.String} object.
     */
    public Standard(final String ip) {
        this.ip = ip;
    }

    /** {@inheritDoc} */
    public void setUrl(final String url) {
        ip = null;
        this.url = url;
    }

    /** {@inheritDoc} */
    public boolean run(final Context context) {
        boolean ret = true;

        String path =
            (url != null)
                ? url
                : context.getSpecification().getBaseUrl() + "/ip/" + ip;

        Result proto = new Result(Result.Status.Notification, path,
                                  "ip.standard",
                                  "content", "",
                                  "draft-ietf-weirds-json-response-07",
                                  "6.4");

        proto.setCode("content");
        Result r = new Result(proto);
        r.setCode("response");
        Map root = Utils.standardRequest(context, path, r);
        if (root == null) {
            return false;
        }
        Map<String, Object> data = Utils.castToMap(context, proto, root);
        if (data == null) {
            return false;
        }

        AttributeTest ipTest = new Ip(ip);
        return ipTest.run(context, proto, data);
    }
}
