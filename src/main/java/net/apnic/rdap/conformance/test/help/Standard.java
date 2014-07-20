package net.apnic.rdap.conformance.test.help;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.attributetest.RdapConformance;
import net.apnic.rdap.conformance.attributetest.Notices;

/**
 * <p>Standard class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Standard implements Test {
    /**
     * <p>Constructor for Standard.</p>
     */
    public Standard() { }

    /** {@inheritDoc} */
    public boolean run(final Context context) {
        boolean ret = true;

        String path =
            context.getSpecification().getBaseUrl() + "/help";

        Result proto = new Result(Result.Status.Notification, path,
                                  "help",
                                  "content", "",
                                  "draft-ietf-weirds-json-response-07",
                                  "8");

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

        Set<String> knownAttributes = new HashSet<String>(); 
        return Utils.runTestList(
            context, proto, data, knownAttributes, true,
            Arrays.asList(
                new RdapConformance(),
                new Notices()
            )
        );
    }
}
