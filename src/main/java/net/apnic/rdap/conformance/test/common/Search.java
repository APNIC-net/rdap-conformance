package net.apnic.rdap.conformance.test.common;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Arrays;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;

import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.SearchTest;
import net.apnic.rdap.conformance.attributetest.ArrayAttribute;
import net.apnic.rdap.conformance.attributetest.StandardResponse;
import net.apnic.rdap.conformance.attributetest.ResultsTruncated;
import net.apnic.rdap.conformance.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;

/**
 * <p>Search class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
final public class Search implements Test {
    public enum ExpectedResultType { SOME, NONE, TRUNCATED };

    private String urlPath;
    private String prefix;
    private String key;
    private String pattern;
    private String testName;
    private String searchResultsKey;
    private SearchTest searchTest;
    private ExpectedResultType expectedResultType;
    private Context context = null;
    private HttpResponse httpResponse = null;
    private Throwable throwable = null;

    /**
     * <p>Constructor for Search.</p>
     *
     * @param argSearchTest a {@link net.apnic.rdap.conformance.SearchTest} object.
     * @param argPrefix a {@link java.lang.String} object.
     * @param argKey a {@link java.lang.String} object.
     * @param argPattern a {@link java.lang.String} object.
     * @param argTestName a {@link java.lang.String} object.
     * @param argSearchResultsKey a {@link java.lang.String} object.
     * @param argExpectedResultType a {@link ExpectedResultType} object.
     */
    public Search(final SearchTest argSearchTest,
                  final String argPrefix,
                  final String argKey,
                  final String argPattern,
                  final String argTestName,
                  final String argSearchResultsKey,
                  final ExpectedResultType argExpectedResultType) {
        expectedResultType = argExpectedResultType;
        prefix = argPrefix;
        key = argKey;
        pattern = argPattern;
        try {
            urlPath  = "/" + prefix + "?" + key + "="
                        + URLEncoder.encode(pattern, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        testName = argTestName;
        searchResultsKey = argSearchResultsKey;
        searchTest = argSearchTest;
        searchTest.setSearchDetails(key, pattern);

        if (testName == null) {
            testName = "common.search";
        }
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
        String path = context.getSpecification().getBaseUrl() + urlPath;
        return Utils.httpGetRequest(context, path, true);
    }

    /** {@inheritDoc} */
    public boolean run() {
        String path = context.getSpecification().getBaseUrl() + urlPath;
        Result proto = new Result(Status.Notification, path,
                                  testName,
                                  "", "",
                                  "draft-ietf-weirds-json-response-07",
                                  "9");
        Map<String, Object> data =
            Utils.processResponse(context, httpResponse, proto,
                                  HttpStatus.SC_OK, throwable);
        if (data == null) {
            return false;
        }

        HashSet<String> knownAttributes = new HashSet<String>();
        boolean res = Utils.runTestList(
            context, proto, (Map) data, knownAttributes, true,
            Arrays.asList(
                new ArrayAttribute(searchTest, searchResultsKey),
                new ResultsTruncated(),
                new StandardResponse()
            )
        );

        List<Object> results = null;
        try {
            results = (List<Object>) data.get(searchResultsKey);
        } catch (ClassCastException ce) {
            return false;
        }

        Object ob = data.get("resultsTruncated");
        Boolean b = (ob != null) ? ((Boolean) ob) : false;

        Result r = new Result(proto);
        r.setCode("response");
        if (expectedResultType == ExpectedResultType.SOME) {
            if (results.size() > 0) {
                r.setStatus(Status.Success);
                r.setInfo("got one or more results for search");
            } else {
                r.setStatus(Status.Failure);
                r.setInfo("did not get any results for search");
            }
        } else if (expectedResultType == ExpectedResultType.NONE) {
            if (results.size() == 0) {
                r.setStatus(Status.Success);
                r.setInfo("got no results for search");
            } else {
                r.setStatus(Status.Failure);
                r.setInfo("did not get no results for search");
            }
        } else if (expectedResultType == ExpectedResultType.TRUNCATED) {
            if ((b != null) && b) {
                r.setStatus(Status.Success);
                r.setInfo("results have been truncated");
            } else {
                r.setStatus(Status.Failure);
                r.setInfo("results have not been truncated");
            }
        }
        context.addResult(r);

        return res;
    }
}
