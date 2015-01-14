package net.apnic.rdap.conformance.test.autnum;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ObjectTest;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.attributetest.Autnum;
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
    private String autnum = null;
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
                : context.getSpecification().getBaseUrl()
                    + "/autnum/" + autnum;
        return Utils.httpGetRequest(context, path, true);
    }

    /** {@inheritDoc} */
    public boolean run() {
        String path =
            (url != null)
                ? url
                : context.getSpecification().getBaseUrl()
                    + "/autnum/" + autnum;

        Result proto = new Result(Result.Status.Notification, path,
                                  "autnum.standard",
                                  "content", "",
                                  "draft-ietf-weirds-json-response-14",
                                  "5.5");
        Map<String, Object> data =
            Utils.processResponse(context, httpResponse, proto,
                                  HttpStatus.SC_OK, throwable);
        if (data == null) {
            return false;
        }

        Set<String> knownAttributes = new HashSet<String>();
        return Utils.runTestList(
            context, proto, (Map) data, knownAttributes, true,
            Arrays.asList(
                new Autnum(autnum),
                new StandardResponse()
            )
        );
    }
}
