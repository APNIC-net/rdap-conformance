package net.apnic.rdap.conformance.responsetest;

import java.util.List;

import org.apache.http.HttpResponse;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.ResponseTest;

/**
 * <p>NotStatusCode class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3
 */
public final class NotStatusCode implements ResponseTest {
    private int notExpectedCode;

    /**
     * <p>Constructor for NotStatusCode.</p>
     *
     * @param argNotExpectedCode a int.
     */
    public NotStatusCode(final int argNotExpectedCode) {
        notExpectedCode = argNotExpectedCode;
    }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final HttpResponse hr) {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("not-status-code");

        int code = hr.getStatusLine().getStatusCode();

        if (code != notExpectedCode) {
            nr.setStatus(Status.Success);
            if (notExpectedCode != 0) {
                nr.setInfo("got " + code + " instead of unexpected code ("
                           + notExpectedCode + ")");
            } else {
                nr.setInfo("got valid code (" + code + ")");
            }
            results.add(nr);
            return true;
        } else {
            nr.setStatus(Status.Failure);
            nr.setInfo("got unexpected code (" + notExpectedCode + ")");
            results.add(nr);
            return false;
        }
    }
}
