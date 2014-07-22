package net.apnic.rdap.conformance;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.common.util.concurrent.ListenableFuture;

import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.responsetest.StatusCode;
import net.apnic.rdap.conformance.responsetest.ContentType;
import net.apnic.rdap.conformance.responsetest.AccessControl;
import net.apnic.rdap.conformance.attributetest.UnknownAttributes;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;

/**
 * <p>Utils class.</p>
 *
 * Miscellaneous utility methods. Most relate to HTTP request
 * execution, casting, or retrieving attributes from arbitrary
 * objects.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Utils {
    private static final int TIMEOUT_MS = 60000;

    private Utils() { }

    /**
     * <p>httpGetRequest.</p>
     *
     * @param context a {@link net.apnic.rdap.conformance.Context} object.
     * @param path a {@link java.lang.String} object.
     * @param followRedirects a boolean.
     * @return a {@link org.apache.http.client.methods.HttpRequestBase} object.
     */
    public static HttpRequestBase httpGetRequest(
                final Context context,
                final String path,
                final boolean followRedirects) {
        HttpGet request = new HttpGet(path);
        request.setHeader("Accept", context.getContentType());
        RequestConfig config =
            RequestConfig.custom()
                         .setConnectionRequestTimeout(TIMEOUT_MS)
                         .setConnectTimeout(TIMEOUT_MS)
                         .setSocketTimeout(TIMEOUT_MS)
                         .setRedirectsEnabled(followRedirects)
                         .build();
        request.setConfig(config);
        return request;
    }

    /**
     * <p>processResponse.</p>
     *
     * @param context a {@link net.apnic.rdap.conformance.Context} object.
     * @param httpResponse a {@link org.apache.http.HttpResponse} object.
     * @param proto a {@link net.apnic.rdap.conformance.Result} object.
     */
    public static Map processResponse(final Context context,
                                      final HttpResponse httpResponse,
                                      final Result proto) {
        Result r = new Result(proto);
        r.setCode("response");
        r.setStatus(Status.Success);
        context.addResult(r);

        ResponseTest sc = new StatusCode(HttpStatus.SC_OK);
        boolean scres = sc.run(context, proto, httpResponse);
        ResponseTest ct = new ContentType();
        boolean ctres = ct.run(context, proto, httpResponse);
        if (!(scres && ctres)) {
            return null;
        }

        ResponseTest ac = new AccessControl();
        ac.run(context, proto, httpResponse);

        Map root = null;
        try {
            InputStream is = httpResponse.getEntity().getContent();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            root = new Gson().fromJson(isr, Map.class);
        } catch (Exception e) {
            r = new Result(proto);
            r.setStatus(Status.Failure);
            r.setInfo(e.toString());
            context.addResult(r);
            return null;
        }
        if (root == null) {
            return null;
        }

        Set<String> keys = root.keySet();
        if (keys.size() == 0) {
            r = new Result(proto);
            /* Technically not an error, but there's not much point in
             * returning nothing. */
            r.setStatus(Status.Failure);
            r.setInfo("no data returned");
            context.addResult(r);
            return null;
        }

        return root;
    }

    /**
     * <p>standardRequest.</p>
     *
     * @param context a {@link net.apnic.rdap.conformance.Context} object.
     * @param path a {@link java.lang.String} object.
     * @param proto a {@link net.apnic.rdap.conformance.Result} object.
     * @return a {@link java.util.Map} object.
     */
    public static Map standardRequest(final Context context,
                                      final String path,
                                      final Result proto) {
        Result r = new Result(proto);
        r.setCode("response");

        HttpRequestBase request = null;
        HttpResponse response = null;
        try {
            request = httpGetRequest(context, path, true);
            ListenableFuture<HttpResponse> fresponse =
                context.executeRequest(request);
            response = fresponse.get();
        } catch (Exception e) {
            r.setStatus(Status.Failure);
            r.setInfo(e.toString());
            context.addResult(r);
            if (request != null) {
                request.releaseConnection();
            }
            return null;
        }

        r.setStatus(Status.Success);
        context.addResult(r);

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
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            root = new Gson().fromJson(isr, Map.class);
        } catch (Exception e) {
            r = new Result(proto);
            r.setStatus(Status.Failure);
            r.setInfo(e.toString());
            context.addResult(r);
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
            context.addResult(r);
            request.releaseConnection();
            return null;
        }

        request.releaseConnection();
        return root;
    }

    /**
     * <p>castToString.</p>
     *
     * @param b a {@link java.lang.Object} object.
     * @return a {@link java.lang.String} object.
     */
    public static String castToString(final Object b) {
        if (b == null) {
            return null;
        }
        String sb;
        try {
            sb = (String) b;
        } catch (ClassCastException ce) {
            sb = null;
        }
        return sb;
    }

    /**
     * <p>castToInteger.</p>
     *
     * @param n a {@link java.lang.Object} object.
     * @return a {@link java.lang.Integer} object.
     */
    public static Integer castToInteger(final Object n) {
        if (n == null) {
            return null;
        }
        Integer value = null;
        try {
            Double dvalue = (Double) n;
            if ((dvalue != null) && (dvalue == Math.rint(dvalue))) {
                value = Integer.valueOf((int) Math.round(dvalue));
            }
        } catch (ClassCastException ce) {
            value = null;
        }
        return value;
    }

    /**
     * <p>castToMap.</p>
     *
     * @param context a {@link net.apnic.rdap.conformance.Context} object.
     * @param proto a {@link net.apnic.rdap.conformance.Result} object.
     * @param obj a {@link java.lang.Object} object.
     * @return a {@link java.util.Map} object.
     */
    public static Map<String, Object> castToMap(final Context context,
                                                final Result proto,
                                                final Object obj) {
        Map<String, Object> data = null;
        Result castResult = new Result(proto);
        try {
            data = (Map<String, Object>) obj;
        } catch (ClassCastException e) {
            castResult.setInfo("structure is invalid");
            castResult.setStatus(Status.Failure);
            context.addResult(castResult);
        }
        return data;
    }

    /**
     * <p>getAttribute.</p>
     *
     * @param context a {@link net.apnic.rdap.conformance.Context} object.
     * @param proto a {@link net.apnic.rdap.conformance.Result} object.
     * @param key a {@link java.lang.String} object.
     * @param missingStatus a {@link net.apnic.rdap.conformance.Result.Status} object.
     * @param data a {@link java.util.Map} object.
     * @return a {@link java.lang.Object} object.
     */
    public static Object getAttribute(final Context context,
                                      final Result proto,
                                      final String key,
                                      final Status missingStatus,
                                      final Map<String, Object> data) {
        Object obj = data.get(key);
        boolean res = true;
        Result lnr = new Result(proto);
        lnr.addNode(key);
        if (obj == null) {
            if (missingStatus == null) {
                return null;
            }
            lnr.setStatus(missingStatus);
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

    /**
     * <p>getStringAttribute.</p>
     *
     * @param context a {@link net.apnic.rdap.conformance.Context} object.
     * @param proto a {@link net.apnic.rdap.conformance.Result} object.
     * @param key a {@link java.lang.String} object.
     * @param missingStatus a {@link net.apnic.rdap.conformance.Result.Status} object.
     * @param data a {@link java.util.Map} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getStringAttribute(final Context context,
                                            final Result proto,
                                            final String key,
                                            final Status missingStatus,
                                            final Map<String, Object> data) {
        Object obj = getAttribute(context, proto, key, missingStatus, data);
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

    /**
     * <p>getMapAttribute.</p>
     *
     * @param context a {@link net.apnic.rdap.conformance.Context} object.
     * @param proto a {@link net.apnic.rdap.conformance.Result} object.
     * @param key a {@link java.lang.String} object.
     * @param missingStatus a {@link net.apnic.rdap.conformance.Result.Status} object.
     * @param data a {@link java.util.Map} object.
     * @return a {@link java.util.Map} object.
     */
    public static Map<String, Object> getMapAttribute(
                final Context context,
                final Result proto,
                final String key,
                final Status missingStatus,
                final Map<String, Object> data) {
        Object obj = getAttribute(context, proto, key, missingStatus, data);
        if (obj == null) {
            return null;
        }

        Map<String, Object> mapData;
        try {
            mapData = (Map<String, Object>) obj;
        } catch (ClassCastException e) {
            mapData = null;
        }
        Result snr = new Result(proto);
        snr.addNode(key);
        if (mapData == null) {
            snr.setStatus(Status.Failure);
            snr.setInfo("not object");
        } else {
            snr.setStatus(Status.Success);
            snr.setInfo("is object");
        }
        context.addResult(snr);

        return mapData;
    }

    /**
     * <p>getListAttribute.</p>
     *
     * @param context a {@link net.apnic.rdap.conformance.Context} object.
     * @param proto a {@link net.apnic.rdap.conformance.Result} object.
     * @param key a {@link java.lang.String} object.
     * @param missingStatus a {@link net.apnic.rdap.conformance.Result.Status} object.
     * @param data a {@link java.util.Map} object.
     * @return a {@link java.util.List} object.
     */
    public static List<Object> getListAttribute(
                final Context context,
                final Result proto,
                final String key,
                final Status missingStatus,
                final Map<String, Object> data) {
        Object obj = getAttribute(context, proto, key, missingStatus, data);
        if (obj == null) {
            return null;
        }

        List<Object> listData;
        try {
            listData = (List<Object>) obj;
        } catch (ClassCastException e) {
            listData = null;
        }
        Result snr = new Result(proto);
        snr.addNode(key);
        if (listData == null) {
            snr.setStatus(Status.Failure);
            snr.setInfo("not array");
        } else {
            snr.setStatus(Status.Success);
            snr.setInfo("is array");
        }
        context.addResult(snr);

        return listData;
    }

    /**
     * <p>runTestList.</p>
     *
     * @param context a {@link net.apnic.rdap.conformance.Context} object.
     * @param proto a {@link net.apnic.rdap.conformance.Result} object.
     * @param data a {@link java.util.Map} object.
     * @param knownAttributes a {@link java.util.Set} object.
     * @param checkUnknown a boolean.
     * @param tests a {@link java.util.List} object.
     * @return a boolean.
     */
    public static boolean runTestList(
                final Context context,
                final Result proto,
                final Map<String, Object> data,
                final Set<String> knownAttributes,
                final boolean checkUnknown,
                final List<AttributeTest> tests) {
        boolean ret = true;
        for (AttributeTest test : tests) {
            boolean res = test.run(context, proto, data);
            if (!res) {
                ret = false;
            }
            knownAttributes.addAll(test.getKnownAttributes());
        }

        boolean ret2 = true;
        if (checkUnknown) {
            AttributeTest ua = new UnknownAttributes(knownAttributes);
            ret2 = ua.run(context, proto, data);
        }
        return (ret && ret2);
    }

    /**
     * <p>matchesSearch</p>
     *
     * Returns a boolean indicating whether the string matches the
     * RDAP search pattern.
     *
     * @param strPattern a string.
     * @param value a string.
     * @return a boolean.
     */
    public static boolean matchesSearch(final String strPattern,
                                        final String value) {
        /* At least some servers will add implicit ".*" to the
         * beginning and the end of the pattern, so add those here
         * too. This may become configurable, so that stricter servers
         * can verify their behaviour.  Searches are presumed to be
         * case-insensitive as well. */
        String regexPattern = strPattern.replaceAll("\\*", ".*");
        regexPattern = ".*" + regexPattern + ".*";
        Pattern pattern = Pattern.compile(regexPattern,
                                          Pattern.CASE_INSENSITIVE
                                        | Pattern.UNICODE_CASE);
        return pattern.matcher(value).matches();
    }
}
