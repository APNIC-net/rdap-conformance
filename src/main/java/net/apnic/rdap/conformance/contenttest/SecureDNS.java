package net.apnic.rdap.conformance.contenttest;

import java.util.Map;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;

public class SecureDNS implements ContentTest
{
    public SecureDNS() {}

    public boolean run(Context context, Result proto, 
                       Object arg_data)
    {
        return true;
    }

    public Set<String> getKnownAttributes()
    {
        return new HashSet<String>();
    }
}
