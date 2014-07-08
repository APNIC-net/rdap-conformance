package net.apnic.rdap.conformance.test.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.URLEncoder;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpStatus;
import org.apache.http.Header;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Specification;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ObjectTest;
import net.apnic.rdap.conformance.ResponseTest;
import net.apnic.rdap.conformance.SearchTest;
import net.apnic.rdap.conformance.responsetest.StatusCode;
import net.apnic.rdap.conformance.responsetest.NotStatusCode;
import net.apnic.rdap.conformance.responsetest.ContentType;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.attributetest.ArrayAttribute;
import net.apnic.rdap.conformance.attributetest.RdapConformance;
import net.apnic.rdap.conformance.attributetest.ScalarAttribute;
import net.apnic.rdap.conformance.attributetest.Notices;
import net.apnic.rdap.conformance.attributetest.StandardResponse;
import net.apnic.rdap.conformance.attributetest.UnknownAttributes;
import net.apnic.rdap.conformance.valuetest.BooleanValue;
import net.apnic.rdap.conformance.Utils;

public class Search implements net.apnic.rdap.conformance.Test
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
                        java.net.URLEncoder.encode(
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
        List<Result> results = context.getResults();

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
