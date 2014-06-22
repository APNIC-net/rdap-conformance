package net.apnic.rdap.conformance.test.common;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import java.net.Socket;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.http.impl.io.DefaultHttpResponseParser;
import org.apache.http.impl.io.SessionInputBufferImpl;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import com.google.gson.Gson;

public class RawURIRequest implements net.apnic.rdap.conformance.Test
{
    String raw_uri = null;

    public RawURIRequest(String raw_uri)
    {
        this.raw_uri = raw_uri;
    }

    public boolean run(Context context)
    {
        boolean success = false;
        String error = null;
        try {
            String bu = context.getSpecification().getBaseUrl();
            URI uri = new URI(bu);
            String host = uri.getHost();

            Socket socket = new Socket(host, 80);
            OutputStream os = socket.getOutputStream();
            String request = "GET " + raw_uri + " HTTP/1.1\n" +
                             "Host: " + host + "\n" +
                             "Accept: application/rdap+json\n\n";
            os.write(request.getBytes());
            InputStream is  = socket.getInputStream();
            SessionInputBufferImpl sibl =
                new SessionInputBufferImpl(
                    new HttpTransportMetricsImpl(),
                    4096
                );
            sibl.bind(is);
            DefaultHttpResponseParser dhrp =
                new DefaultHttpResponseParser(sibl);
            HttpResponse hr = dhrp.parse();
            HttpEntity he = hr.getEntity();
            /* It is assumed that this class is used to produce
             * invalid requests. The error codes aren't checked here;
             * it's just for confirming that the content (if present)
             * is JSON. With some servers, e.g. Jetty, it's not
             * possible to do things like setting the content type in
             * this sort of situation, so that is explicitly not
             * checked. */
            if ((he == null) || (he.getContentLength() == 0)) {
                success = true;
            } else {
                InputStream isc = he.getContent();
                InputStreamReader iscr = new InputStreamReader(isc);
                Object root = new Gson().fromJson(iscr, Map.class);
                success = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            error = e.toString();
        }

        Result nr = new Result();
        nr.setPath(raw_uri);
        nr.setTestName("common.bad-request-uri");
        nr.setCode("content");
        nr.setStatus(success ? Status.Success : Status.Failure);
        nr.setInfo(success ? "error content is empty or JSON"
                           : "error content is not empty or JSON: " + error);
        context.addResult(nr);

        return success;
    }
}
