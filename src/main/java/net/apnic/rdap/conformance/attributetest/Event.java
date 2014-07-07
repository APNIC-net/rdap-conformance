package net.apnic.rdap.conformance.attributetest;

import java.util.HashSet;
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
import net.apnic.rdap.conformance.attributetest.Links;

public class Event implements AttributeTest
{
    boolean allow_actor = true;

    private static final Set<String> event_actions =
        Sets.newHashSet("registration",
                        "reregistration",
                        "last changed",
                        "expiration",
                        "deletion",
                        "reinstantiation",
                        "transfer",
                        "locked",
                        "unlocked");

    public Event(boolean arg_allow_actor)
    {
        allow_actor = arg_allow_actor;
    }

    public Event() { }

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("content");

        Map<String, Object> data = Utils.castToMap(context, nr, arg_data);
        if (data == null) {
            return false;
        }

        boolean evtres = true;
        String event_action =
            Utils.getStringAttribute(context, nr, "eventAction",
                                     Status.Failure, data);
        if (event_action == null) {
            evtres = false;
        } else {
            Result evr = new Result(proto);
            evr.setCode("content");
            evr.addNode("eventAction");
            evr.setDocument("draft-ietf-weirds-json-response-06");
            evr.setReference("10.2.2");
            if (event_actions.contains(event_action)) {
                evr.setInfo("registered");
                evr.setStatus(Status.Success);
                results.add(evr);
            } else {
                evr.setInfo("unregistered: " + event_action);
                evr.setStatus(Status.Failure);
                results.add(evr);
                evtres = false;
            }
        }

        DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();

        boolean evdres = true;
        String event_date =
            Utils.getStringAttribute(context, nr, "eventDate",
                                     Status.Failure, data);
        if (event_date == null) {
            evdres = false;
        } else {
            Result edvr = new Result(proto);
            edvr.setCode("content");
            edvr.addNode("eventDate");
            edvr.setDocument("draft-ietf-weirds-json-response-06");
            edvr.setReference("10.2.2");
            DateTime dth = parser.parseDateTime(event_date);
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
        String event_actor =
            Utils.getStringAttribute(context, nr, "eventActor",
                                     Status.Notification, data);
        if ((event_actor != null) && !allow_actor) {
            Result eacr = new Result(proto);
            eacr.setCode("content");
            eacr.addNode("eventActor");
            eacr.setInfo("not permitted here");
            eacr.setStatus(Status.Failure);
            results.add(eacr);
            eacres = false;
        }

        AttributeTest lst = new Links();
        boolean lstres = lst.run(context, proto, arg_data);

        return (evtres && evdres && eacres && lstres);
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet("eventActor", "eventDate",
                               "eventAction");
    }
}
