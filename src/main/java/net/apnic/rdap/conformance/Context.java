package net.apnic.rdap.conformance;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpHost;

import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * <p>Context class.</p>
 *
 * Stores configuration details, the application's HTTP client object,
 * the master result list and other miscellaneous application-wide
 * things.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.4-SNAPSHOT
 */
public final class Context {
    private static final int HTTP_PORT = 80;

    private CloseableHttpAsyncClient httpClient = null;
    private Specification specification = null;
    private List<Result> results = new ArrayList<Result>();
    private String contentType = null;
    private int index;
    private RateLimiter rateLimiter = null;
    private ExecutorService executorService = null;
    private AtomicInteger testsRunning = null;

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
    public ListenableFuture<HttpResponse> executeRequest(
                final HttpRequest httpRequest)
            throws IOException {
        acquireRequestPermit();
        Future<HttpResponse> response = null;
        try {
            URL url = new URL(httpRequest.getRequestLine().getUri());
            HttpHost httphost =
                new HttpHost(url.getHost(),
                             ((url.getPort() == -1)
                                 ? url.getDefaultPort()
                                 : url.getPort()),
                             (url.toString().startsWith("https")
                                 ? "https" : "http"));
            response = httpClient.execute(httphost, httpRequest, null);
        } catch (Exception e) {
            System.err.println("Exception occurred during asynchronous "
                               + "HTTP request: " + e.toString());
        }
        if (response == null) {
            return null;
        }
        ListenableFuture<HttpResponse> hr =
            JdkFutureAdapters.listenInPoolThread(response, executorService);
        return hr;
    }

    /**
     * <p>Setter for the field <code>httpClient</code>.</p>
     *
     * @param hc a {@link org.apache.http.impl.nio.client.CloseableHttpAsyncClient} object.
     */
    public void setHttpClient(final CloseableHttpAsyncClient hc) {
        httpClient = hc;
    }

    public void setTestsRunning(final AtomicInteger tr) {
        testsRunning = tr;
    }

    public CloseableHttpAsyncClient getHttpClient() {
        return httpClient;
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
     * <p>Indicates if a failed result is present in this context</p>
     *
     * @return True of false if this context has a failed result
     */
    public boolean hasFailedResult()
    {
        for(Result res: results)
        {
            if(res.isStatus(Result.Status.Failure)) {
                return true;
            }
        }
        return false;
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

    /**
     * <p>Setter for the field <code>executorService</code>.</p>
     *
     * @param es a {@link java.util.concurrent.ExecutorService} object.
     */
    public void setExecutorService(final ExecutorService es) {
        executorService = es;
    }

    /**
     * <p>submitTest.</p>
     *
     * Runs the test within this context's executor service.
     *
     * @param test a {@link net.apnic.rdap.conformance.Test} object.
     * @return a {@link java.util.concurrent.Future} object.
     */
    public Future submitTest(final Test test) {
        final ExecutorService executorService2 = executorService;
        final Context context = this;

        testsRunning.getAndIncrement();
        test.setContext(context);

        return executorService2.submit(
            new Runnable() {
                @Override
                public void run() {
                    try {
                        final HttpRequest httpRequest = test.getRequest();
                        if (httpRequest == null) {
                            testsRunning.getAndDecrement();
                            return;
                        }
                        final ListenableFuture<HttpResponse> future =
                            executeRequest(httpRequest);
                        if (future == null) {
                            testsRunning.getAndDecrement();
                            return;
                        }
                        Futures.addCallback(
                            future,
                            new FutureCallback<HttpResponse>() {
                                public void onSuccess(final HttpResponse httpResponse) {
                                    test.setResponse(httpResponse);
                                    test.run();
                                    synchronized (System.out) {
                                        context.flushResults();
                                    }
                                    if (!(test instanceof net.apnic.rdap.conformance.test.common.Head)) {
                                        context.submitTest(
                                            new net.apnic.rdap.conformance.test.common.Head(
                                                httpRequest.getRequestLine().getUri(),
                                                httpResponse.getStatusLine().getStatusCode()
                                            )
                                        );
                                    }
                                    testsRunning.getAndDecrement();
                                }
                                public void onFailure(final Throwable t) {
                                    test.setError(t);
                                    test.run();
                                    synchronized (System.out) {
                                        context.flushResults();
                                    }
                                    testsRunning.getAndDecrement();
                                }
                            },
                            MoreExecutors.directExecutor()
                        );
                    } catch (Exception e) {
                        System.err.println("Error during test submission: " +
                                           e.toString());
                    }
                }
            }
        );
    }
}
