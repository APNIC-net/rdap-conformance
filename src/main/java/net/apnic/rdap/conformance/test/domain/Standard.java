package net.apnic.rdap.conformance.test.domain;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ObjectTest;
import net.apnic.rdap.conformance.attributetest.StandardResponse;
import net.apnic.rdap.conformance.attributetest.Domain;

/**
 * <p>Standard class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
 */
public final class Standard implements ObjectTest {
    private String domain = null;
    private String url = null;

    /**
     * <p>Constructor for Standard.</p>
     */
    public Standard() { }

    /**
     * <p>Constructor for Standard.</p>
     *
     * @param domain a {@link java.lang.String} object.
     */
    public Standard(final String domain) {
        this.domain = domain;
    }

    /** {@inheritDoc} */
    public void setUrl(final String url) {
        domain = null;
        this.url = url;
    }

    /** {@inheritDoc} */
    public boolean run(final Context context) {
        String path =
            (url != null)
                ? url
                : context.getSpecification().getBaseUrl()
                    + "/domain/" + domain;

        Result proto = new Result(Status.Notification, path,
                                  "domain.standard",
                                  "content", "",
                                  "draft-ietf-weirds-json-response-07",
                                  "6.3");
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

        Set<String> knownAttributes = new HashSet<String>();
        return Utils.runTestList(
            context, proto, data, knownAttributes, true,
            Arrays.asList(
                new Domain(false),
                new StandardResponse()
            )
        );
    }
}
