package net.apnic.rdap.conformance.test.nameserver;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ObjectTest;
import net.apnic.rdap.conformance.attributetest.Nameserver;
import net.apnic.rdap.conformance.attributetest.RdapConformance;
import net.apnic.rdap.conformance.attributetest.Notices;

public class Standard implements ObjectTest {
    String nameserver = null;
    String url = null;

    public Standard() { }

    public Standard(String nameserver) {
        this.nameserver = nameserver;
    }

    public void setUrl(String url) {
        nameserver = null;
        this.url = url;
    }

    public boolean run(Context context) {
        String path =
            (url != null)
                ? url
                : context.getSpecification().getBaseUrl()
                    + "/nameserver/" + nameserver;

        Result proto = new Result(Status.Notification, path,
                                  "domain.standard",
                                  "content", "",
                                  "draft-ietf-weirds-json-response-06",
                                  "6.2");
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
            context, proto, root, knownAttributes, true,
            Arrays.asList(
                new Nameserver(false),
                new RdapConformance(),
                new Notices()
            )
        );
    }
}
