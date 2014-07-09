package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;
import net.apnic.rdap.conformance.Utils;

public class Digest implements ValueTest
{
    public Digest() { }

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        return new HexString().run(context, proto, arg_data);
    }
}
