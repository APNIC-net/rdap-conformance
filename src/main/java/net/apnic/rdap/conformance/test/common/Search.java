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
    private String url_path;
    private String prefix;
    private String key;
    private String pattern;
    private String test_name;
    private String search_results_key;
    private SearchTest search_test;

    public Search(SearchTest arg_search_test,
                  String arg_prefix,
                  String arg_key,
                  String arg_pattern,
                  String arg_test_name,
                  String arg_search_results_key)
    {
        prefix = arg_prefix;
        key = arg_key;
        pattern = arg_pattern;
        try {
            url_path  = "/" + prefix + "?" + key + "=" +
                        URLEncoder.encode(
                            pattern, "UTF-8"
                        );
        } catch (Exception e) {
            e.printStackTrace();
        }
        test_name = arg_test_name;
        search_results_key = arg_search_results_key;
        search_test = arg_search_test;
        search_test.setSearchDetails(key, pattern);

        if (test_name == null) {
            test_name = "common.search";
        }
    }

    public boolean run(Context context)
    {
        String bu = context.getSpecification().getBaseUrl();
        String path = bu + url_path;

        Result proto = new Result(Status.Notification, path,
                                  test_name,
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

        HashSet<String> known_attributes = new HashSet<String>();
        return Utils.runTestList(
            context, proto, root, known_attributes, true,
            Arrays.asList(
                new ArrayAttribute(search_test, search_results_key),
                new ScalarAttribute("resultsTruncated",
                                    new BooleanValue()),
                new StandardResponse()
            )
        );
    }
}
