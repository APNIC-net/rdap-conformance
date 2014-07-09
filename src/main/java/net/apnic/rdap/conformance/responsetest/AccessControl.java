package net.apnic.rdap.conformance.responsetest;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.Header;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.ResponseTest;

public class AccessControl implements ResponseTest
{
    public AccessControl() {}

    public boolean run(Context context, Result proto,
                       HttpResponse hr)
    {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("access-control-allow-origin");
        nr.setDocument("draft-ietf-weirds-using-http-08");
        nr.setReference("5.6");

        Header cth = hr.getFirstHeader("Access-Control-Allow-Origin");
        if (cth == null) {
            nr.setStatus(Status.Failure);
            nr.setInfo("not present");
            results.add(nr);
            return false;
        }

        String ct = cth.getValue();
        if (ct.equals("*")) {
            nr.setStatus(Status.Success);
            results.add(nr);
            return true;
        } else {
            nr.setStatus(Status.Failure);
            nr.setInfo("got '" + ct + "' instead of '*'");
            results.add(nr);
            return false;
        }
    }
}
