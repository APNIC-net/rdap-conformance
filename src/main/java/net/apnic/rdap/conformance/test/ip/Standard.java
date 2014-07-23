package net.apnic.rdap.conformance.test.ip;

import java.util.Map;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ObjectTest;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.attributetest.Ip;

import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;

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
        Map<String, Object> data =
            Utils.processResponse(context, httpResponse, proto,
                                  200, throwable);
        if (data == null) {
            return false;
        }

        AttributeTest ipTest = new Ip(ip);
        return ipTest.run(context, proto, data);
    }
}
