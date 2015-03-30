package net.apnic.rdap.conformance.valuetest;

import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

/**
 * <p>ContentType class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3
 */
public final class ContentType implements ValueTest {
    /**
     * <p>Constructor for ContentType.</p>
     */
    public ContentType() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        Result nr = new Result(proto);
        nr.setCode("content");

        String contentType = Utils.castToString(argData);

        boolean res = nr.setDetails((contentType != null),
                                    "is string",
                                    "not string");
        context.addResult(nr);
        if (!res) {
            return false;
        }

        String err = "";
        try {
            org.apache.http.entity.ContentType.parse(contentType);
        } catch (IllegalArgumentException iae) {
            res = false;
            err = iae.toString();
        }

        Result nr2 = new Result(proto);
        res = nr2.setDetails(res, "valid", "invalid: " + err);
        context.addResult(nr2);
        return res;
    }
}
