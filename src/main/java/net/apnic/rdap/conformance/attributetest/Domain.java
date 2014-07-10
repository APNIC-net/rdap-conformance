package net.apnic.rdap.conformance.attributetest;

import java.util.HashSet;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.SearchTest;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.valuetest.Variant;

/**
 * <p>Domain class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
 */
public final class Domain implements SearchTest {
    private boolean checkUnknown = false;
    private Set<String> knownAttributes = new HashSet<String>();
    private String key = null;
    private String pattern = null;

    /**
     * <p>Constructor for Domain.</p>
     *
     * @param argCheckUnknown a boolean.
     */
    public Domain(final boolean argCheckUnknown) {
        checkUnknown = argCheckUnknown;
    }

    /** {@inheritDoc} */
    public void setSearchDetails(final String argKey,
                                 final String argPattern) {
        key = argKey;
        pattern = argPattern;
    }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        SearchTest domainNames = new DomainNames();
        if (key != null) {
            domainNames.setSearchDetails(key, pattern);
        }

        return Utils.runTestList(
            context, proto, data, knownAttributes, checkUnknown,
            Arrays.asList(
                new ScalarAttribute("handle"),
                domainNames,
                new ArrayAttribute(new Variant(), "variants"),
                new ArrayAttribute(new Nameserver(true), "nameServers"),
                new ScalarAttribute("secureDNS", new SecureDNS()),
                new StandardObject()
            )
        );
    }

    /**
     * <p>Getter for the field <code>knownAttributes</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getKnownAttributes() {
        return knownAttributes;
    }
}
