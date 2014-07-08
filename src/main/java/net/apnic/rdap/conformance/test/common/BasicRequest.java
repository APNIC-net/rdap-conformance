package net.apnic.rdap.conformance.test.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpStatus;
import org.apache.http.Header;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.apnic.rdap.conformance.Specification;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ResponseTest;
import net.apnic.rdap.conformance.responsetest.StatusCode;
import net.apnic.rdap.conformance.responsetest.NotStatusCode;
import net.apnic.rdap.conformance.responsetest.ContentType;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.attributetest.RdapConformance;
import net.apnic.rdap.conformance.attributetest.ScalarAttribute;
import net.apnic.rdap.conformance.attributetest.Notices;
import net.apnic.rdap.conformance.attributetest.ErrorResponse;
import net.apnic.rdap.conformance.Utils;

public class BasicRequest implements net.apnic.rdap.conformance.Test
{
    private int expected_status;
    private String test_name;
    private String url_path;
    boolean invert_status_test;

    public BasicRequest(int arg_expected_status,
                        String arg_url_path,
                        String arg_test_name,
                        boolean arg_invert_status_test)
    {
        expected_status = arg_expected_status;
        test_name = arg_test_name;
        url_path  = arg_url_path;
        invert_status_test = arg_invert_status_test;

        if (test_name == null) {
            test_name = "common." +
                        (arg_invert_status_test ? "not-" : "") +
                        ((expected_status == 404)
                            ? "not-found"
                            : expected_status);
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
            request = Utils.httpGetRequest(context, path, true);
            response = context.executeRequest(request);
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
            (invert_status_test)
                ? new NotStatusCode(expected_status)
                : new StatusCode(expected_status);
        boolean scres = sc.run(context, proto, response);
        if (!scres) {
            request.releaseConnection();
            return false;
        }

        Header cth = response.getEntity().getContentType();
        if (cth == null) {
            /* If there is no content-type, then there shouldn't be a body.
             * This is fine, since bodies are optional for error responses. */
            request.releaseConnection();
            return true;
        }

        ResponseTest ct = new ContentType();
        boolean ctres = ct.run(context, proto, response);

        if (!(scres && ctres)) {
            request.releaseConnection();
            return false;
        }

        Map root = null;
        try {
            InputStream is = response.getEntity().getContent();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            root = new Gson().fromJson(isr, Map.class);
        } catch (Exception e) {
            r = new Result(proto);
            r.setInfo(e.toString());
            results.add(r);
            request.releaseConnection();
            return false;
        }
        if (root == null) {
            request.releaseConnection();
            return ctres;
        }

        Set<String> keys = root.keySet();
        if (keys.size() == 0) {
            request.releaseConnection();
            return ctres;
        }

        if ((expected_status >= 400) && (!invert_status_test)) {
            AttributeTest ert = new ErrorResponse(expected_status);
            request.releaseConnection();
            return ert.run(context, proto, root);
        } else {
            return ctres;
        }
    }
}
