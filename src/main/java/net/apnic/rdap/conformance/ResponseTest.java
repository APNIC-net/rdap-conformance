package net.apnic.rdap.conformance;

import org.apache.http.HttpResponse;

/**
 * <p>ResponseTest interface.</p>
 *
 * A ResponseTest is a test that verifies something about a HTTP
 * response: e.g. that the status code is correct, or that a
 * particular header is set properly.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
 */
public interface ResponseTest {
    /**
     * <p>run.</p>
     *
     * @param context a {@link net.apnic.rdap.conformance.Context} object.
     * @param proto a {@link net.apnic.rdap.conformance.Result} object.
     * @param hr a {@link org.apache.http.HttpResponse} object.
     * @return a boolean.
     */
    boolean run(Context context, Result proto, HttpResponse hr);
}
