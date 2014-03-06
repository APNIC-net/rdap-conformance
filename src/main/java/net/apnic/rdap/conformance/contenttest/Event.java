package net.apnic.rdap.conformance.contenttest;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import com.google.common.collect.Sets;
import org.joda.time.*;
import org.joda.time.format.*;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.contenttest.ScalarAttribute;
import net.apnic.rdap.conformance.contenttest.ArrayAttribute;
import net.apnic.rdap.conformance.contenttest.Links;

public class Event implements ContentTest
{
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

    public Event() {}

    public boolean run(Context context, Result proto, 
                       Object arg_data)
    {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("content");

        Map<String, Object> data;
        try {
            data = (Map<String, Object>) arg_data;
        } catch (ClassCastException e) {
            nr.setInfo("structure is invalid");
            nr.setStatus(Status.Failure);
            results.add(nr);
            return false;
        }

        boolean evtres = true;
        String event_action = (String) data.get("eventAction");
        Result ear = new Result(proto);
        ear.setCode("content");
        ear.addNode("eventAction");
        if (event_action == null) {
            ear.setInfo("not present");
            ear.setStatus(Status.Failure);
            results.add(ear);
            evtres = false;
        } else {    
            ear.setInfo("present");
            ear.setStatus(Status.Success);
            results.add(ear);
            Result evr = new Result(proto);
            ear.setCode("content");
            ear.addNode("eventAction");
            evr.setDocument("draft-ietf-weirds-json-response-06");
            evr.setReference("10.2.2");
            if (event_actions.contains(event_action)) {
                evr.setInfo("registered");
                evr.setStatus(Status.Success);
                results.add(evr);
            } else {
                evr.setInfo("not registered");
                evr.setStatus(Status.Failure);
                results.add(evr);
                evtres = false;
            }
        }

        DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();

        boolean evdres = true;
        String event_date = (String) data.get("eventDate");
        Result edr = new Result(proto);
        edr.setCode("content");
        edr.addNode("eventDate");
        if (event_date == null) {
            edr.setInfo("not present");
            edr.setStatus(Status.Failure);
            results.add(edr);
            evdres = false;
        } else {
            edr.setInfo("present");
            edr.setStatus(Status.Success);
            results.add(edr);
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
                edvr.setInfo("not valid");
                edvr.setStatus(Status.Failure);
                results.add(edvr);
                evdres = false;
            }
        }

        ContentTest lst = new Links();
        boolean lstres = lst.run(context, proto, arg_data);

        return (evtres && evdres && lstres);
    }
}
