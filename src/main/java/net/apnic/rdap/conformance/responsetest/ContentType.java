package net.apnic.rdap.conformance.responsetest;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.Header;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.ResponseTest;

/**
 * <p>ContentType class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
 */
public final class ContentType implements ResponseTest {
    /**
     * <p>Constructor for ContentType.</p>
     */
    public ContentType() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final HttpResponse hr) {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("content-type");
        nr.setDocument("draft-ietf-weirds-using-http-08");
        nr.setReference("4.1");

        Header cth = hr.getEntity().getContentType();
        if (cth == null) {
            nr.setStatus(Status.Failure);
            nr.setInfo("no content-type found in response");
            results.add(nr);
            return false;
        }

        String ct = cth.getValue();
        org.apache.http.entity.ContentType cto =
            org.apache.http.entity.ContentType.parse(ct);
        if (cto.getMimeType().equals("application/rdap+json")) {
            nr.setStatus(Status.Success);
            results.add(nr);
            return true;
        } else {
            nr.setStatus(Status.Failure);
            nr.setInfo("got '" + ct + "' instead of "
                       + "'application/rdap+json'");
            results.add(nr);
            return false;
        }
    }
}
