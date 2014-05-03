package net.apnic.rdap.conformance.contenttest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import java.math.BigInteger;
import java.math.BigDecimal;
import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.contenttest.StandardObject;
import net.apnic.rdap.conformance.contenttest.StandardResponse;
import net.apnic.rdap.conformance.contenttest.UnknownAttributes;

public class Entity implements ContentTest
{
    String handle = null;
    Set<String> known_attributes = null;

    private static final Set<String> roles =
        Sets.newHashSet("registrant",
                        "technical",
                        "administrative",
                        "abuse",
                        "billing",
                        "registrar",
                        "reseller",
                        "sponsor",
                        "proxy",
                        "notifications",
                        "noc");

    public Entity() {}

    public Entity(String arg_handle) 
    {
        handle = arg_handle;
    }

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        known_attributes = Sets.newHashSet("handle", "roles", "vcardArray");

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("6.1");

        Map<String, Object> root;
        try {
            root = (Map<String, Object>) arg_data;
        } catch (ClassCastException e) {
            nr.setInfo("structure is invalid");
            nr.setStatus(Status.Failure);
            context.addResult(nr);
            return false;
        }

        String response_handle = Utils.castToString(root.get("handle"));
        Result r = new Result(nr);
        r.setStatus(Status.Success);
        r.addNode("handle");
        r.setInfo("present");
        if (response_handle == null) {
            r.setStatus(Status.Warning);
            r.setInfo("not present");
        } 
        context.addResult(r);
        if ((response_handle != null) && (handle != null)) {
            Result r2 = new Result(nr);
            r.addNode("handle");
            r2.setStatus(Status.Success);
            r2.setInfo("response handle element matches requested handle");
            if (!response_handle.equals(handle)) {
                r2.setStatus(Status.Warning);
                r2.setInfo("response handle element does not " +
                           "match requested handle");
            }
            context.addResult(r2);
        }

        Result hr = new Result(nr);
        hr.setStatus(Status.Success);
        hr.addNode("roles");
        hr.setInfo("present");
        Object response_roles = root.get("roles");
        if (response_roles == null) {
            hr.setStatus(Status.Notification);
            hr.setInfo("not present");
        }
        context.addResult(hr);
        if (response_roles != null) {
            Result ilr = new Result(nr);
            ilr.setStatus(Status.Success);
            ilr.addNode("roles");
            ilr.setInfo("is an array");
            List<String> response_roles_list = null;
            try {
                response_roles_list = (List<String>) response_roles;
            } catch (ClassCastException e) {
                ilr.setStatus(Status.Failure);
                ilr.setInfo("is not an array");
            }
            context.addResult(ilr);
            if (response_roles_list != null) {
                int i = 0;
                for (String role : response_roles_list) {
                    Result rr = new Result(nr);
                    rr.addNode("roles");
                    rr.addNode(Integer.toString(i));
                    rr.setInfo("registered");
                    rr.setStatus(Status.Success);
                    rr.setReference("10.2.3");
                    if (!roles.contains(role)) {
                        rr.setInfo("unregistered: " + role);
                        rr.setStatus(Status.Failure);
                    }
                    context.addResult(rr);
                    i++;
                }
            }
        }

        ContentTest srt = new StandardObject();
        boolean ret = srt.run(context, proto, root);
        known_attributes.addAll(srt.getKnownAttributes());

        ContentTest ua = new UnknownAttributes(known_attributes);
        boolean ret2 = ua.run(context, proto, root);

        return (ret && ret2);
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
