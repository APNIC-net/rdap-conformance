package net.apnic.rdap.conformance.test.common;

import java.io.IOException;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.HttpStatus;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;

import net.apnic.rdap.conformance.Test;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ObjectTest;
import net.apnic.rdap.conformance.ResponseTest;
import net.apnic.rdap.conformance.responsetest.StatusCode;
import net.apnic.rdap.conformance.Utils;

public class Redirect implements Test
{
    private String urlPath;
    private String testName;
    private ObjectTest resultTest;

    public Redirect(String argUrlPath,
                    String argTestName)
    {
        urlPath    = argUrlPath;
        testName   = argTestName;

        if (testName == null) {
            testName = "common.redirect";
        }
    }

    public Redirect(ObjectTest argResultTest,
                    String argUrlPath,
                    String argTestName)
    {
        resultTest = argResultTest;
        urlPath    = argUrlPath;
        testName   = argTestName;

        if (testName == null) {
            testName = "common.redirect";
        }
    }

    public boolean run(Context context)
    {
        List<Result> results = context.getResults();

        String bu = context.getSpecification().getBaseUrl();
        String path = bu + urlPath;

        Result proto = new Result(Status.Notification, path,
                                  testName,
                                  "", "", "", "");
        Result r = new Result(proto);
        r.setCode("response");

        HttpRequestBase request = null;
        HttpResponse response = null;
        try {
            request = Utils.httpGetRequest(context, path, false);
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
            new StatusCode(
                Sets.newHashSet(HttpStatus.SC_TEMPORARY_REDIRECT,
                                HttpStatus.SC_MOVED_PERMANENTLY,
                                HttpStatus.SC_MOVED_TEMPORARILY,
                                HttpStatus.SC_SEE_OTHER)
            );
        boolean scres = sc.run(context, proto, response);
        request.releaseConnection();
        if (!scres) {
            return false;
        }

        if (resultTest != null) {
            String location = response.getFirstHeader("Location")
                                      .getValue();
            resultTest.setUrl(location);
            return resultTest.run(context);
        } else {
            return true;
        }
    }
}
