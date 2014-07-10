package net.apnic.rdap.conformance.test.common;

import java.net.URLEncoder;
import java.util.Map;
import java.util.HashSet;
import java.util.Arrays;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;

import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.SearchTest;
import net.apnic.rdap.conformance.attributetest.ArrayAttribute;
import net.apnic.rdap.conformance.attributetest.ScalarAttribute;
import net.apnic.rdap.conformance.attributetest.StandardResponse;
import net.apnic.rdap.conformance.valuetest.BooleanValue;
import net.apnic.rdap.conformance.Utils;

/**
 * <p>Search class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
 */
public class Search implements Test {
    private String urlPath;
    private String prefix;
    private String key;
    private String pattern;
    private String testName;
    private String searchResultsKey;
    private SearchTest searchTest;

    /**
     * <p>Constructor for Search.</p>
     *
     * @param argSearchTest a {@link net.apnic.rdap.conformance.SearchTest} object.
     * @param argPrefix a {@link java.lang.String} object.
     * @param argKey a {@link java.lang.String} object.
     * @param argPattern a {@link java.lang.String} object.
     * @param argTestName a {@link java.lang.String} object.
     * @param argSearchResultsKey a {@link java.lang.String} object.
     */
    public Search(final SearchTest argSearchTest,
                  final String argPrefix,
                  final String argKey,
                  final String argPattern,
                  final String argTestName,
                  final String argSearchResultsKey) {
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
    public boolean run(final Context context) {
        String bu = context.getSpecification().getBaseUrl();
        String path = bu + urlPath;

        Result proto = new Result(Status.Notification, path,
                                  testName,
                                  "", "", "", "");
        Result r = new Result(proto);
        r.setCode("response");

        Map root = Utils.standardRequest(context, path, r);
        if (root == null) {
            return false;
        }

        Map<String, Object> data = Utils.castToMap(context, proto, root);
        if (data == null) {
            return false;
        }

        HashSet<String> knownAttributes = new HashSet<String>();
        return Utils.runTestList(
            context, proto, root, knownAttributes, true,
            Arrays.asList(
                new ArrayAttribute(searchTest, searchResultsKey),
                new ScalarAttribute("resultsTruncated",
                                    new BooleanValue()),
                new StandardResponse()
            )
        );
    }
}
