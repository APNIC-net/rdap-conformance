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
    Set<Integer> expectedCodes = new HashSet<Integer>();

    public StatusCode(int argExpectedCode)
    {
        expectedCodes.add(argExpectedCode);
    }

    public StatusCode(Set<Integer> argExpectedCodes)
    {
        expectedCodes.addAll(argExpectedCodes);
    }

    public boolean run(Context context, Result proto,
                       HttpResponse hr)
    {
        boolean hasMultiple = expectedCodes.size() > 1;

        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("status-code");
        if (nr.getDocument().equals("")
                && !hasMultiple
                && expectedCodes.contains(HttpStatus.SC_BAD_REQUEST)) {
            nr.setDocument("draft-ietf-weirds-using-http-08");
            nr.setReference("5.4");
        }

        int code = hr.getStatusLine().getStatusCode();

        if (expectedCodes.contains(code)) {
            nr.setStatus(Status.Success);
            nr.setInfo("got " + (hasMultiple ? "an " : "") +
                       "expected code (" +
                       expectedCodes.iterator().next() + ")");
            results.add(nr);
            return true;
        } else {
            nr.setStatus(Status.Failure);
            nr.setInfo("got " + code + " instead of " +
                       (hasMultiple
                           ? ("one of " + Joiner.on(", ").join(expectedCodes))
                           : expectedCodes.iterator().next()));
            results.add(nr);
            return false;
        }
    }
}
