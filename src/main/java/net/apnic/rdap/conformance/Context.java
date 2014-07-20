package net.apnic.rdap.conformance;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.HttpResponse;

import com.google.common.util.concurrent.RateLimiter;

/**
 * <p>Context class.</p>
 *
 * Stores configuration details, the application's HTTP client object,
 * the master result list and other miscellaneous application-wide
 * things.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Context {
    private HttpClient httpClient = null;
    private Specification specification = null;
    private List<Result> results = new ArrayList<Result>();
    private String contentType = null;
    private int index;
    private RateLimiter rateLimiter = null;

    /**
     * <p>Constructor for Context.</p>
     */
    public Context() {
        index = 0;
    }

    /**
     * <p>acquireRequestPermit.</p>
     */
    public void acquireRequestPermit() {
        if (rateLimiter != null) {
            rateLimiter.acquire();
        }
    }

    /**
     * <p>executeRequest.</p>
     *
     * @param httpRequest a {@link org.apache.http.client.methods.HttpRequestBase} object.
     * @return a {@link org.apache.http.HttpResponse} object.
     * @throws java.io.IOException if any.
     */
    public HttpResponse executeRequest(final HttpRequestBase httpRequest)
            throws IOException {
        acquireRequestPermit();
        return httpClient.execute(httpRequest);
    }

    /**
     * <p>Setter for the field <code>httpClient</code>.</p>
     *
     * @param hc a {@link org.apache.http.client.HttpClient} object.
     */
    public void setHttpClient(final HttpClient hc) {
        httpClient = hc;
    }

    /**
     * <p>Getter for the field <code>results</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Result> getResults() {
        return results;
    }

    /**
     * <p>addResult.</p>
     *
     * @param r a {@link net.apnic.rdap.conformance.Result} object.
     */
    public void addResult(final Result r) {
        results.add(r);
    }

    /**
     * <p>Getter for the field <code>specification</code>.</p>
     *
     * @return a {@link net.apnic.rdap.conformance.Specification} object.
     */
    public Specification getSpecification() {
        return specification;
    }

    /**
     * <p>Setter for the field <code>specification</code>.</p>
     *
     * @param s a {@link net.apnic.rdap.conformance.Specification} object.
     */
    public void setSpecification(final Specification s) {
        specification = s;
        double rps = s.getRequestsPerSecond();
        if (rps > 0) {
            rateLimiter = RateLimiter.create(rps);
        }
    }

    /**
     * <p>Getter for the field <code>contentType</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getContentType() {
        return ((contentType == null) ? "application/rdap+json"
                                       : contentType);
    }

    /**
     * <p>Setter for the field <code>contentType</code>.</p>
     *
     * @param s a {@link java.lang.String} object.
     */
    public void setContentType(final String s) {
        contentType = s;
    }

    /**
     * <p>Setter for the field <code>rateLimiter</code>.</p>
     *
     * @param rl a {@link com.google.common.util.concurrent.RateLimiter} object.
     */
    public void setRateLimiter(final RateLimiter rl) {
        rateLimiter = rl;
    }

    /**
     * <p>flushResults.</p>
     */
    public void flushResults() {
        List<Result> ml = getResults();
        int size = ml.size();
        int i;
        for (i = index; i < size; i++) {
            System.out.println(ml.get(i).toString());
        }
        index = i;
    }
}
