package net.apnic.rdap.conformance;

import java.util.Set;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;

public interface ContentTest
{
    boolean run(Context context, Result proto, Object content);
    Set<String> getKnownAttributes();
}
