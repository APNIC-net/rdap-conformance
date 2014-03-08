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
import net.apnic.rdap.conformance.responsetest.ContentType;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.contenttest.RdapConformance;
import net.apnic.rdap.conformance.contenttest.ScalarAttribute;
import net.apnic.rdap.conformance.contenttest.Notices;
import net.apnic.rdap.conformance.contenttest.ErrorResponse;
import net.apnic.rdap.conformance.Utils;

public class BadRequest implements net.apnic.rdap.conformance.Test
{
    private String test_name;
    private String url_path;

    public BadRequest(String arg_test_name, String arg_url_path) 
    {
        test_name = arg_test_name;
        url_path  = arg_url_path;
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
            request = Utils.httpGetRequest(context, path);
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

        ResponseTest sc = new StatusCode(HttpStatus.SC_BAD_REQUEST);
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
            InputStreamReader isr = new InputStreamReader(is);
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

        ContentTest ert = new ErrorResponse(HttpStatus.SC_BAD_REQUEST);
        request.releaseConnection();
        return ert.run(context, proto, root);
    }
}
