package net.apnic.rdap.conformance.attributetest;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Locale;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.AttributeTest;

/**
 * <p>Country class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Country implements AttributeTest {
    private static final Set<String> COUNTRY_CODES = new HashSet<String>();
    static {
        String[] arrayCountryCodes = Locale.getISOCountries();
        for (String countryCode : arrayCountryCodes) {
            COUNTRY_CODES.add(countryCode);
        }
    }

    /**
     * <p>Constructor for Country.</p>
     */
    public Country() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.addNode("country");
        nr.setDocument("draft-ietf-weirds-json-response-09");
        nr.setReference("4");

        String countryValue =
            Utils.getStringAttribute(context, nr, "country",
                                     Result.Status.Notification,
                                     data);
        if (countryValue == null) {
            return false;
        }

        Result nr3 = new Result(nr);
        boolean res = nr3.setDetails(COUNTRY_CODES.contains(countryValue),
                                     "valid",
                                     "invalid: " + countryValue);
        context.addResult(nr3);

        return res;
    }

    /**
     * <p>getKnownAttributes.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getKnownAttributes() {
        return Sets.newHashSet("country");
    }
}
