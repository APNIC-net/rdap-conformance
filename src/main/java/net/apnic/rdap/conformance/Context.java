package net.apnic.rdap.conformance;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.HttpResponse;

import com.google.common.util.concurrent.RateLimiter;

public final class Context {
    private HttpClient httpClient = null;
    private Specification specification = null;
    private List<Result> results = new ArrayList<Result>();
    private String contentType = null;
    private int index;
    private RateLimiter rateLimiter = null;

    public Context() {
        index = 0;
    }

    public void acquireRequestPermit() {
        if (rateLimiter != null) {
            rateLimiter.acquire();
        }
    }

    public HttpResponse executeRequest(final HttpRequestBase httpRequest)
            throws IOException {
        acquireRequestPermit();
        return httpClient.execute(httpRequest);
    }

    public void setHttpClient(final HttpClient hc) {
        httpClient = hc;
    }

    public List<Result> getResults() {
        return results;
    }

    public void addResult(final Result r) {
        results.add(r);
        flushResults();
    }

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(final Specification s) {
        specification = s;
        double rps = s.getRequestsPerSecond();
        if (rps > 0) {
            rateLimiter = RateLimiter.create(rps);
        }
    }

    public String getContentType() {
        return ((contentType == null) ? "application/rdap+json"
                                       : contentType);
    }

    public void setContentType(final String s) {
        contentType = s;
    }

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
