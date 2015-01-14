package net.apnic.rdap.conformance.test.ip;

import java.util.Arrays;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ObjectTest;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.attributetest.Ip;
import net.apnic.rdap.conformance.attributetest.StandardResponse;

import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;

/**
 * <p>Standard class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Standard implements ObjectTest {
    private String ip = null;
    private String url = null;
    private Context context = null;
    private HttpResponse httpResponse = null;
    private Throwable throwable = null;

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
        String path =
            (url != null)
                ? url
                : context.getSpecification().getBaseUrl() + "/ip/" + ip;
        return Utils.httpGetRequest(context, path, true);
    }

    /** {@inheritDoc} */
    public boolean run() {
        String path =
            (url != null)
                ? url
                : context.getSpecification().getBaseUrl() + "/ip/" + ip;

        Result proto = new Result(Result.Status.Notification, path,
                                  "ip.standard",
                                  "content", "",
                                  "draft-ietf-weirds-json-response-14",
                                  "5.4");
        Map<String, Object> data =
            Utils.processResponse(context, httpResponse, proto,
                                  HttpStatus.SC_OK, throwable);
        if (data == null) {
            return false;
        }

        Set<String> knownAttributes = new HashSet<String>();
        return Utils.runTestList(
            context, proto, data, knownAttributes, true,
            Arrays.asList(
                new Ip(ip),
                new StandardResponse()
            )
        );
    }
}
