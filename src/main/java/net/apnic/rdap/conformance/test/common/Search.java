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

public class Search implements Test
{
    private String urlPath;
    private String prefix;
    private String key;
    private String pattern;
    private String testName;
    private String searchResultsKey;
    private SearchTest searchTest;

    public Search(SearchTest argSearchTest,
                  String argPrefix,
                  String argKey,
                  String argPattern,
                  String argTestName,
                  String argSearchResultsKey)
    {
        prefix = argPrefix;
        key = argKey;
        pattern = argPattern;
        try {
            urlPath  = "/" + prefix + "?" + key + "=" +
                        URLEncoder.encode(
                            pattern, "UTF-8"
                        );
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

    public boolean run(Context context)
    {
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
