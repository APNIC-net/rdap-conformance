package net.apnic.rdap.conformance.contenttest;

import java.util.Map;
import org.apache.http.HttpStatus;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ResponseTest;
import net.apnic.rdap.conformance.responsetest.StatusCode;
import net.apnic.rdap.conformance.responsetest.ContentType;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.contenttest.RdapConformance;
import net.apnic.rdap.conformance.contenttest.ScalarAttribute;
import net.apnic.rdap.conformance.contenttest.Array;
import net.apnic.rdap.conformance.contenttest.StringTest;
import net.apnic.rdap.conformance.contenttest.Notices;

public class ErrorResponse implements net.apnic.rdap.conformance.ContentTest
{
    private int status_code;

    public ErrorResponse(int arg_status_code) 
    {
        status_code = arg_status_code;
    }

    public boolean run(Context context, Result proto,
                       Object root)
    {
        Result p = new Result(proto);
        p.setCode("content");
        p.setDocument("draft-ietf-json-response-06");
        p.setReference("7");

        ContentTest rc = new RdapConformance();
        boolean rcres = rc.run(context, p, root);
        ContentTest ect = new ScalarAttribute("errorCode");
        boolean ectres = ect.run(context, p, root);
        ContentTest sat = new ScalarAttribute("title");
        boolean satres = sat.run(context, p, root);
        ContentTest aat = new Array(new StringTest(), "description");
        Result pd = new Result(p);
        pd.addNode("description");
        boolean aatres = aat.run(context, pd, root);
        ContentTest nt = new Notices();
        boolean ntres = nt.run(context, p, root);

        Map<String, Object> root_cast;
        try {
            root_cast = (Map<String, Object>) root;
        } catch (ClassCastException e) {
            return false;
        }

        if (ectres) {
            Double error_code;
            Result p2 = new Result(p);
            p2.addNode("errorCode");
            p2.setInfo("is a number");
            try {
                error_code = (Double) root_cast.get("errorCode");
            } catch (Exception e) {
                p2.setStatus(Status.Failure);
                p2.setInfo("is not a number");
                context.addResult(p2);
                return false;
            }
            p2.setStatus(Status.Success);
            context.addResult(p2);

            Result p3 = new Result(p);
            p3.addNode("errorCode");
            p3.setInfo("matches the response code");

            if (error_code != HttpStatus.SC_BAD_REQUEST) {
                p3.setStatus(Status.Failure);
                p3.setInfo("does not match the response code");
                context.addResult(p3);
                return false;
            }
            p3.setStatus(Status.Success);
            context.addResult(p3);
        }

        return (rcres && satres && aatres && ntres);
    }
}
