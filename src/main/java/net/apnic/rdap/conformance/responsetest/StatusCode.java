package net.apnic.rdap.conformance.responsetest;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.google.common.base.Joiner;
import org.apache.http.HttpStatus;
import org.apache.http.HttpResponse;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.ResponseTest;

public class StatusCode implements ResponseTest
{
    Set<Integer> expected_codes = new HashSet<Integer>();

    public StatusCode(int arg_expected_code)
    {
        expected_codes.add(arg_expected_code);
    }

    public StatusCode(Set<Integer> arg_expected_codes)
    {
        expected_codes.addAll(arg_expected_codes);
    }

    public boolean run(Context context, Result proto,
                       HttpResponse hr)
    {
        boolean has_multiple = expected_codes.size() > 1;

        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("status-code");
        if (nr.getDocument().equals("")
                && !has_multiple
                && expected_codes.contains(HttpStatus.SC_BAD_REQUEST)) {
            nr.setDocument("draft-ietf-weirds-using-http-08");
            nr.setReference("5.4");
        }

        int code = hr.getStatusLine().getStatusCode();

        if (expected_codes.contains(code)) {
            nr.setStatus(Status.Success);
            nr.setInfo("got " + (has_multiple ? "an " : "") +
                       "expected code (" +
                       expected_codes.iterator().next() + ")");
            results.add(nr);
            return true;
        } else {
            nr.setStatus(Status.Failure);
            nr.setInfo("got " + code + " instead of " +
                       (has_multiple
                           ? ("one of " + Joiner.on(", ").join(expected_codes))
                           : expected_codes.iterator().next()));
            results.add(nr);
            return false;
        }
    }
}
