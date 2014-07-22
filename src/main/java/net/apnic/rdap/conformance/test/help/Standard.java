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

import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;

/**
 * <p>Standard class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Standard implements Test {
    private Context context = null;
    private HttpResponse httpResponse = null;

    /**
     * <p>Constructor for Standard.</p>
     */
    public Standard() { }

    /** {@inheritDoc} */
    public void setContext(final Context c) {
        context = c;
    }

    /** {@inheritDoc} */
    public void setResponse(final HttpResponse hr) {
        httpResponse = hr;
    }

    /** {@inheritDoc} */
    public HttpRequest getRequest() {
        String path = context.getSpecification().getBaseUrl() + "/help";
        return Utils.httpGetRequest(context, path, true);
    }

    /** {@inheritDoc} */
    public boolean run() {
        boolean ret = true;
        String path = context.getSpecification().getBaseUrl() + "/help";
        Result proto = new Result(Result.Status.Notification, path,
                                  "help",
                                  "content", "",
                                  "draft-ietf-weirds-json-response-07",
                                  "8");
        Map root = Utils.processResponse(context, httpResponse, proto);
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
