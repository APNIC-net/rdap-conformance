package net.apnic.rdap.conformance.attributetest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpStatus;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.ResponseTest;
import net.apnic.rdap.conformance.responsetest.StatusCode;
import net.apnic.rdap.conformance.responsetest.ContentType;
import net.apnic.rdap.conformance.attributetest.UnknownAttributes;
import net.apnic.rdap.conformance.attributetest.RdapConformance;
import net.apnic.rdap.conformance.attributetest.ScalarAttribute;
import net.apnic.rdap.conformance.valuetest.StringTest;
import net.apnic.rdap.conformance.attributetest.Notices;

public class ErrorResponse implements net.apnic.rdap.conformance.AttributeTest
{
    Set<String> known_attributes = null;
    private int status_code;

    public ErrorResponse(int arg_status_code)
    {
        status_code = arg_status_code;
    }

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        Result p = new Result(proto);
        p.setCode("content");
        p.setDocument("draft-ietf-json-response-06");
        p.setReference("7");

        List<AttributeTest> tests = new ArrayList(Arrays.asList(
            new RdapConformance(),
            new ScalarAttribute("errorCode"),
            new ScalarAttribute("title"),
            new ArrayAttribute(new StringTest(), "description"),
            new Notices(),
            new Lang()
        ));

        known_attributes = new HashSet<String>();

        boolean ret = true;
        for (AttributeTest test : tests) {
            boolean res = test.run(context, p, data);
            if (!res) {
                ret = false;
            }
            known_attributes.addAll(test.getKnownAttributes());
        }

        if (ret) {
            Double error_code;
            Result p2 = new Result(p);
            p2.addNode("errorCode");
            p2.setInfo("is a number");
            try {
                error_code = (Double) data.get("errorCode");
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

        AttributeTest ua = new UnknownAttributes(known_attributes);
        boolean ret2 = ua.run(context, proto, data);

        return (ret && ret2);
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
