package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;
import net.apnic.rdap.conformance.Utils;

import com.google.common.io.BaseEncoding;

/**
 * <p>Base64String class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Base64String implements ValueTest {
    /**
     * <p>Constructor for Base64String.</p>
     */
    public Base64String() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        String value = Utils.castToString(argData);

        Result nr = new Result(proto);
        boolean res = nr.setDetails((value != null),
                                    "is string",
                                    "not string");
        context.addResult(nr);
        if (!res) {
            return false;
        }

        Result nr2 = new Result(proto);
        String error = null;
        try {
            BaseEncoding.base64().decode(value);
        } catch (IllegalArgumentException iae) {
            error = iae.toString();
        }
        res = nr2.setDetails((error == null),
                             "valid",
                             "invalid: " + error);
        context.addResult(nr2);
        return res;
    }
}
