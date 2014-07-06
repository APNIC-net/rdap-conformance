package net.apnic.rdap.conformance;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ResponseTest;
import net.apnic.rdap.conformance.responsetest.StatusCode;
import net.apnic.rdap.conformance.responsetest.ContentType;
import net.apnic.rdap.conformance.responsetest.AccessControl;

import org.apache.http.client.HttpClient;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpStatus;
import org.apache.http.Header;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.client.config.RequestConfig;

public class Utils
{
    static public HttpRequestBase httpGetRequest(Context context,
                                                 String path,
                                                 boolean follow_redirects)
    {
        HttpGet request = new HttpGet(path);
        request.setHeader("Accept", context.getContentType());
        RequestConfig config =
            RequestConfig.custom()
                         .setConnectionRequestTimeout(5000)
                         .setConnectTimeout(5000)
                         .setSocketTimeout(5000)
                         .setRedirectsEnabled(follow_redirects)
                         .build();
        request.setConfig(config);
        return request;
    }

    static public Map standardRequest(Context context,
                                      String path,
                                      Result proto)
    {
        List<Result> results = context.getResults();

        Result r = new Result(proto);
        r.setCode("response");

        HttpRequestBase request = null;
        HttpResponse response = null;
        HttpEntity entity;
        try {
            request = httpGetRequest(context, path, true);
            response = context.executeRequest(request);
            entity = response.getEntity();
        } catch (IOException e) {
            r.setStatus(Status.Failure);
            r.setInfo(e.toString());
            results.add(r);
            if (request != null) {
                request.releaseConnection();
            }
            return null;
        }

        r.setStatus(Status.Success);
        results.add(r);

        ResponseTest sc = new StatusCode(HttpStatus.SC_OK);
        boolean scres = sc.run(context, proto, response);
        ResponseTest ct = new ContentType();
        boolean ctres = ct.run(context, proto, response);
        if (!(scres && ctres)) {
            request.releaseConnection();
            return null;
        }

        ResponseTest ac = new AccessControl();
        ac.run(context, proto, response);

        Map root = null;
        try {
            InputStream is = response.getEntity().getContent();
            InputStreamReader isr = new InputStreamReader(is);
            root = new Gson().fromJson(isr, Map.class);
        } catch (Exception e) {
            r = new Result(proto);
            r.setStatus(Status.Failure);
            r.setInfo(e.toString());
            results.add(r);
            request.releaseConnection();
            return null;
        }
        if (root == null) {
            request.releaseConnection();
            return null;
        }

        Set<String> keys = root.keySet();
        if (keys.size() == 0) {
            r = new Result(proto);
            /* Technically not an error, but there's not much point in
             * returning nothing. */
            r.setStatus(Status.Failure);
            r.setInfo("no data returned");
            results.add(r);
            request.releaseConnection();
            return null;
        }

        request.releaseConnection();
        return root;
    }

    public static String castToString(Object b)
    {
        if (b == null) {
            return null;
        }
        String sb = null;
        try {
            sb = (String) b;
        } catch (ClassCastException ce) {}
        return sb;
    }

    public static Map<String, Object> castToMap(Context context,
                                                Result proto,
                                                Object obj)
    {
        Map<String, Object> data = null;
        Result cast_result = new Result(proto);
        try {
            data = (Map<String, Object>) obj;
        } catch (ClassCastException e) {
            cast_result.setInfo("structure is invalid");
            cast_result.setStatus(Status.Failure);
            context.addResult(cast_result);
        }
        return data;
    }

    public static Object getAttribute(Context context,
                                      Result proto,
                                      String key,
                                      Status missing_status,
                                      Map<String, Object> data)
    {
        Object obj = data.get(key);
        boolean res = true;
        Result lnr = new Result(proto);
        lnr.addNode(key);
        if (obj == null) {
            if (missing_status == null) {
                return null;
            }
            lnr.setStatus(missing_status);
            lnr.setInfo("not present");
            res = false;
        } else {
            lnr.setStatus(Status.Success);
            lnr.setInfo("present");
        }
        context.addResult(lnr);
        if (!res) {
            return null;
        }
        return obj;
    }

    public static String getStringAttribute(Context context,
                                            Result proto,
                                            String key,
                                            Status missing_status,
                                            Map<String, Object> data)
    {
        Object obj = getAttribute(context, proto, key, missing_status, data);
        if (obj == null) {
            return null;
        }

        boolean res = true;
        String str = castToString(obj);
        Result snr = new Result(proto);
        snr.addNode(key);
        if (str == null) {
            snr.setStatus(Status.Failure);
            snr.setInfo("not string");
            res = false;
        } else {
            snr.setStatus(Status.Success);
            snr.setInfo("is string");
        }
        context.addResult(snr);
        if (!res) {
            return null;
        }

        return str;
    }

    public static Map<String, Object> getMapAttribute(Context context,
                                                      Result proto,
                                                      String key,
                                                      Status missing_status,
                                                      Map<String, Object> data)
    {
        Object obj = getAttribute(context, proto, key, missing_status, data);
        if (obj == null) {
            return null;
        }

        Map<String, Object> map_data = null;
        try {
            map_data = (Map<String, Object>) obj;
        } catch (ClassCastException e) { }
        Result snr = new Result(proto);
        snr.addNode(key);
        if (map_data == null) {
            snr.setStatus(Status.Failure);
            snr.setInfo("not object");
        } else {
            snr.setStatus(Status.Success);
            snr.setInfo("is object");
        }
        context.addResult(snr);

        return map_data;
    }
}
