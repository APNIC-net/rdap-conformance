package net.apnic.rdap.conformance.test.entity;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.attributetest.Entity;
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
    private String entity = "";
    private Context context = null;
    private HttpResponse httpResponse = null;
    private Throwable throwable = null;

    /**
     * <p>Constructor for Standard.</p>
     *
     * @param argEntity a {@link java.lang.String} object.
     */
    public Standard(final String argEntity) {
        entity = argEntity;
    }

    /** {@inheritDoc} */
    public void setContext(final Context c) {
        context = c;
    }

    /** {@inheritDoc} */
    public void setResponse(final HttpResponse hr) {
        httpResponse = hr;
    }

    /** {@inheritDoc} */
    public void setError(final Throwable t) {
        throwable = t;
    }

    /** {@inheritDoc} */
    public HttpRequest getRequest() {
        String bu = context.getSpecification().getBaseUrl();
        String path = bu + "/entity/" + entity;
        return Utils.httpGetRequest(context, path, true);
    }

    /** {@inheritDoc} */
    public boolean run() {
        String bu = context.getSpecification().getBaseUrl();
        String path = bu + "/entity/" + entity;

        Result proto = new Result(Status.Notification, path,
                                  "entity.standard",
                                  "content", "",
                                  "draft-ietf-weirds-json-response-07",
                                  "6.1");
        Map<String, Object> data =
            Utils.processResponse(context, httpResponse, proto,
                                  200, throwable);
        if (data == null) {
            return false;
        }

        Set<String> knownAttributes = new HashSet<String>();
        return Utils.runTestList(
            context, proto, (Map) data, knownAttributes, true,
            Arrays.asList(
                new Entity(entity, false),
                new RdapConformance(),
                new Notices()
            )
        );
    }
}
