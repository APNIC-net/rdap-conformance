package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

/**
 * <p>StringTest class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class StringTest implements ValueTest {
    /**
     * <p>Constructor for StringTest.</p>
     */
    public StringTest() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        Result nr = new Result(proto);
        boolean res = nr.setDetails((Utils.castToString(argData) != null),
                                    "is string", "not string");
        context.addResult(nr);
        return res;
    }
}
