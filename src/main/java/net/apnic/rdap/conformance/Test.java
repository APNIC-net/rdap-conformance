package net.apnic.rdap.conformance;

import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;

/**
 * <p>Test interface.</p>
 *
 * The most basic test interface. The flow is like so: instantiate the
 * test, set context, get the HTTP request (i.e. the one required by
 * the test), execute it (caller's responsibility), set the HTTP
 * response in the test, and run the test. Results are added to the
 * context object by the test class.
 *
 * Although the other test interfaces (except for ObjectTest) do not
 * extend this interface, they should all operate in a similar
 * fashion.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3
 */
public interface Test {
    /**
     * <p>setContext.</p>
     *
     * @param context a {@link net.apnic.rdap.conformance.Context} object.
     */
    void setContext(Context context);

    /**
     * <p>getRequest.</p>
     *
     * @return httpRequest an {@link org.apache.http.client.HttpRequest} object.
     */
    HttpRequest getRequest();

    /**
     * <p>setResponse.</p>
     *
     * @param httpResponse an {@link org.apache.http.HttpResponse} object.
     */
    void setResponse(HttpResponse httpResponse);

    /**
     * <p>setError.</p>
     *
     * @param throwable a {@link java.lang.Throwable} object.
     */
    void setError(Throwable throwable);

    /**
     * <p>run.</p>
     *
     * @return a boolean.
     */
    boolean run();
}
