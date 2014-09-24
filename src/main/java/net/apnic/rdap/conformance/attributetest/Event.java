package net.apnic.rdap.conformance.attributetest;

import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.valuetest.EventAction;
import net.apnic.rdap.conformance.valuetest.Date;

/**
 * <p>Event class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Event implements AttributeTest {
    private boolean allowActor = true;
    private Set<String> knownAttributes = new HashSet<String>();

    /**
     * <p>Constructor for Event.</p>
     *
     * @param argAllowActor a boolean.
     */
    public Event(final boolean argAllowActor) {
        allowActor = argAllowActor;
    }

    /**
     * <p>Constructor for Event.</p>
     */
    public Event() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-09");
        nr.setReference("5.5");

        boolean eacres = true;
        String eventActor =
            Utils.getStringAttribute(context, nr, "eventActor",
                                     Status.Notification, data);
        if ((eventActor != null) && !allowActor) {
            Result eacr = new Result(nr);
            eacr.addNode("eventActor");
            eacr.setInfo("not permitted here");
            eacr.setStatus(Status.Failure);
            results.add(eacr);
            eacres = false;
        }

        knownAttributes.add("eventActor");

        return (Utils.runTestList(
            context, nr, data, knownAttributes, true,
            Arrays.<AttributeTest>asList(
                new ScalarAttribute("eventAction", new EventAction()),
                new ScalarAttribute("eventDate", new Date()),
                new Links()
            )) && eacres);
    }

    /**
     * <p>getKnownAttributes.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getKnownAttributes() {
        return knownAttributes;
    }
}
