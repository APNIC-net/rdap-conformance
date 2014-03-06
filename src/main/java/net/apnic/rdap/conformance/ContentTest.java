package net.apnic.rdap.conformance;

import java.util.Map;
import java.util.List;
import org.apache.http.HttpResponse;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;

public interface ContentTest
{
    boolean run(Context context, Result proto, Object content);
}
