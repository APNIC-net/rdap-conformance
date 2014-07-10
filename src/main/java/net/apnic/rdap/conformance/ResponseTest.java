package net.apnic.rdap.conformance;

import org.apache.http.HttpResponse;

/**
 * <p>ResponseTest interface.</p>
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
