package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

public final class PublicKey implements ValueTest {
    public PublicKey() { }

    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        return new HexString().run(context, proto, argData);
    }
}
