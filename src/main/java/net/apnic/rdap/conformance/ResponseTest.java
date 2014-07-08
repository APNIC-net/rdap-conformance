package net.apnic.rdap.conformance;

import org.apache.http.HttpResponse;

public interface ResponseTest
{
    boolean run(Context context, Result proto, HttpResponse hr);
}
