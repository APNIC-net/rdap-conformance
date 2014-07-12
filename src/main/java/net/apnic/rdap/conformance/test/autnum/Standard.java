package net.apnic.rdap.conformance.test.autnum;

import java.util.Map;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ObjectTest;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.attributetest.Autnum;

/**
 * <p>Standard class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
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

    /** {@inheritDoc} */
    public boolean run(final Context context) {
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
        Map<String, Object> data = Utils.castToMap(context, proto, root);
        if (data == null) {
            return false;
        }

        AttributeTest autnumTest = new Autnum(autnum);
        return autnumTest.run(context, proto, data);
    }
}
