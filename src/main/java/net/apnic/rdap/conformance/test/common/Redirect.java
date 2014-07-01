package net.apnic.rdap.conformance.test.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
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
import net.apnic.rdap.conformance.responsetest.StatusCode;
import net.apnic.rdap.conformance.responsetest.NotStatusCode;
import net.apnic.rdap.conformance.responsetest.ContentType;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.contenttest.RdapConformance;
import net.apnic.rdap.conformance.contenttest.ScalarAttribute;
import net.apnic.rdap.conformance.contenttest.Notices;
import net.apnic.rdap.conformance.contenttest.ErrorResponse;
import net.apnic.rdap.conformance.Utils;

public class Redirect implements net.apnic.rdap.conformance.Test
{
    private String url_path;
    private String test_name;
    private ObjectTest result_test;

    public Redirect(String arg_url_path,
                    String arg_test_name)
    {
        url_path    = arg_url_path;
        test_name   = arg_test_name;

        if (test_name == null) {
            test_name = "common.redirect";
        }
    }

    public Redirect(ObjectTest arg_result_test,
                    String arg_url_path,
                    String arg_test_name)
    {
        result_test = arg_result_test;
        url_path    = arg_url_path;
        test_name   = arg_test_name;

        if (test_name == null) {
            test_name = "common.redirect";
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

        HttpRequestBase request = null;
        HttpResponse response = null;
        try {
            request = Utils.httpGetRequest(context, path, false);
            response = context.getHttpClient().execute(request);
        } catch (IOException e) {
            r.setStatus(Status.Failure);
            r.setInfo(e.toString());
            results.add(r);
            if (request != null) {
                request.releaseConnection();
            }
            return false;
        }

        r.setStatus(Status.Success);
        results.add(r);

        ResponseTest sc =
            new StatusCode(
                Sets.newHashSet(HttpStatus.SC_TEMPORARY_REDIRECT,
                                HttpStatus.SC_MOVED_PERMANENTLY,
                                HttpStatus.SC_MOVED_TEMPORARILY,
                                HttpStatus.SC_SEE_OTHER)
            );
        boolean scres = sc.run(context, proto, response);
        if (!scres) {
            request.releaseConnection();
            return false;
        }

        if (result_test != null) {
            String location = response.getFirstHeader("Location")
                                      .getValue();
            result_test.setUrl(location);
            return result_test.run(context);
        } else {
            return true;
        }
    }
}
