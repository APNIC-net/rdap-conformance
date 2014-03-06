package net.apnic.rdap.conformance.responsetest;

import java.util.List;
import java.util.ArrayList;

import org.apache.http.HttpStatus;
import org.apache.http.HttpResponse;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.ResponseTest;

public class StatusCode implements ResponseTest
{
    int expected_code = HttpStatus.SC_OK;

    public StatusCode(int arg_expected_code)
    {
        expected_code = arg_expected_code;
    }

    public boolean run(Context context, Result proto, 
                       HttpResponse hr)
    {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("status-code");
        if (nr.getDocument().equals("")) {
            if (expected_code == HttpStatus.SC_BAD_REQUEST) {
                nr.setDocument("draft-ietf-weirds-using-http-08");
                nr.setReference("5.4");
            }
        }
 
        int code = hr.getStatusLine().getStatusCode();

        if (code == expected_code) {
            nr.setStatus(Status.Success);
            nr.setInfo("got expected code (" + expected_code + ")");
            results.add(nr);
            return true;
        } else {
            nr.setStatus(Status.Failure);
            nr.setInfo("got " + code + " instead of " +
                       expected_code);
            results.add(nr);
            return false;
        }
    }
}
