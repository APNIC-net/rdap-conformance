package net.apnic.rdap.conformance.valuetest;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

/**
 * <p>Date class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3
 */
public final class Date implements ValueTest {
    private static DateTimeFormatter parser =
        ISODateTimeFormat.dateTimeParser();

    /**
     * <p>Constructor for Date.</p>
     */
    public Date() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object argData) {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("rfc7483");
        nr.setReference("3");

        String date = Utils.castToString(argData);

        boolean res = nr.setDetails((date != null),
                                    "is string",
                                    "not string");
        context.addResult(nr);
        if (!res) {
            return false;
        }

        DateTime dth = null;
        String error = null;
        try {
            dth = parser.parseDateTime(date);
        } catch (IllegalArgumentException iae) {
            error = iae.toString();
        }
        Result nr2 = new Result(nr);
        res = nr2.setDetails((dth != null),
                             "valid",
                             "invalid"
                             + ((error != null) ? (": " + error)
                                                : ""));
        context.addResult(nr2);
        return res;
    }
}
