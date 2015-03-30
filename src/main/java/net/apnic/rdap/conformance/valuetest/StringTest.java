package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

/**
 * <p>StringTest class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.4-SNAPSHOT
 */
public final class StringTest implements ValueTest {
    private String expectedValue = null;

    /**
     * <p>Constructor for StringTest.</p>
     */
    public StringTest() { }

    /**
     * <p>Constructor for StringTest.</p>
     *
     * @param argExpectedValue a {@link java.lang.String} object.
     */
    public StringTest(String argExpectedValue) {
        expectedValue = argExpectedValue;
    }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        Result nr = new Result(proto);
        String value = Utils.castToString(argData);
        boolean res = nr.setDetails((value != null),
                                    "is string", "not string");
        context.addResult(nr);
        if (res && (expectedValue != null)) {
            Result nr2 = new Result(proto);
            res = nr2.setDetails((value.equals(expectedValue)),
                                 "valid",
                                 "invalid (expected '" 
                                 + expectedValue + "')");
            context.addResult(nr2);
        }
        return res;
    }
}
