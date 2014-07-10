package net.apnic.rdap.conformance.responsetest;

import java.util.List;

import org.apache.http.HttpResponse;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.ResponseTest;

public class NotStatusCode implements ResponseTest {
    int not_expected_code;

    public NotStatusCode(int arg_not_expected_code) {
        not_expected_code = arg_not_expected_code;
    }

    public boolean run(Context context, Result proto,
                       HttpResponse hr) {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("not-status-code");

        int code = hr.getStatusLine().getStatusCode();

        if (code != not_expected_code) {
            nr.setStatus(Status.Success);
            nr.setInfo("got " + code + " instead of unexpected code ("
                       + not_expected_code + ")");
            results.add(nr);
            return true;
        } else {
            nr.setStatus(Status.Failure);
            nr.setInfo("got unexpected code (" + not_expected_code + ")");
            results.add(nr);
            return false;
        }
    }
}
