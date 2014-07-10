package net.apnic.rdap.conformance.attributetest;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import org.joda.time.*;
import org.joda.time.format.*;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.AttributeTest;

public class Event implements AttributeTest
{
    boolean allowActor = true;

    private static final Set<String> eventActions =
        Sets.newHashSet("registration",
                        "reregistration",
                        "last changed",
                        "expiration",
                        "deletion",
                        "reinstantiation",
                        "transfer",
                        "locked",
                        "unlocked");

    public Event(boolean argAllowActor)
    {
        allowActor = argAllowActor;
    }

    public Event() { }

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("content");

        boolean evtres = true;
        String eventAction =
            Utils.getStringAttribute(context, nr, "eventAction",
                                     Status.Failure, data);
        if (eventAction == null) {
            evtres = false;
        } else {
            Result evr = new Result(proto);
            evr.setCode("content");
            evr.addNode("eventAction");
            evr.setDocument("draft-ietf-weirds-json-response-06");
            evr.setReference("10.2.2");
            if (eventActions.contains(eventAction)) {
                evr.setInfo("registered");
                evr.setStatus(Status.Success);
                results.add(evr);
            } else {
                evr.setInfo("unregistered: " + eventAction);
                evr.setStatus(Status.Failure);
                results.add(evr);
                evtres = false;
            }
        }

        DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();

        boolean evdres = true;
        String eventDate =
            Utils.getStringAttribute(context, nr, "eventDate",
                                     Status.Failure, data);
        if (eventDate == null) {
            evdres = false;
        } else {
            Result edvr = new Result(proto);
            edvr.setCode("content");
            edvr.addNode("eventDate");
            edvr.setDocument("draft-ietf-weirds-json-response-06");
            edvr.setReference("10.2.2");
            DateTime dth = parser.parseDateTime(eventDate);
            if (dth != null) {
                edvr.setInfo("valid");
                edvr.setStatus(Status.Success);
                results.add(edvr);
            } else {
                edvr.setInfo("invalid");
                edvr.setStatus(Status.Failure);
                results.add(edvr);
                evdres = false;
            }
        }

        boolean eacres = true;
        String eventActor =
            Utils.getStringAttribute(context, nr, "eventActor",
                                     Status.Notification, data);
        if ((eventActor != null) && !allowActor) {
            Result eacr = new Result(proto);
            eacr.setCode("content");
            eacr.addNode("eventActor");
            eacr.setInfo("not permitted here");
            eacr.setStatus(Status.Failure);
            results.add(eacr);
            eacres = false;
        }

        AttributeTest lst = new Links();
        boolean lstres = lst.run(context, proto, data);

        return (evtres && evdres && eacres && lstres);
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet("eventActor", "eventDate",
                               "eventAction");
    }
}
