package net.apnic.rdap.conformance.contenttest;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.ContentTest;

import net.apnic.rdap.conformance.contenttest.Notice;

public class StringTest implements ContentTest
{
    public StringTest() {}

    public boolean run(Context context, Result proto, 
                       Object arg_data)
    {
        Result nr = new Result(proto);
        boolean success = true;
        nr.setStatus(Status.Success);
        nr.setInfo("is string");
        
        if (Utils.castToString(arg_data) == null) {
            nr.setStatus(Status.Failure);
            nr.setInfo("is not string");
            success = false;
        }

        context.addResult(nr);
        return success;
    }
}
