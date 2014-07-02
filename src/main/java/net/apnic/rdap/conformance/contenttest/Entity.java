package net.apnic.rdap.conformance.contenttest;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import ezvcard.*;

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
    boolean check_unknown = false;
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

    public Entity(String arg_handle, boolean arg_check_unknown)
    {
        handle = arg_handle;
        check_unknown = arg_check_unknown;
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

        boolean vret = true;
        Object vcard_array =
            Utils.getAttribute(context, nr, "vcardArray", null, root);
        if (vcard_array != null) {
            String json = new Gson().toJson(vcard_array);
            List<VCard> vcards = null;
            List<List<String>> warnings = null;
            String error = null;
            try {
                Ezvcard.ParserChainJsonString pcjs =
                    Ezvcard.parseJson(json);
                warnings = new ArrayList<List<String>>();
                pcjs.warnings(warnings);
                vcards = pcjs.all();
            } catch (Exception e) {
                error = e.toString();
            }
            Result nrv = new Result(nr);
            Result nrv2 = null;
            nrv.addNode("vcardArray");
            if (error != null) {
                nrv.setStatus(Status.Failure);
                nrv.setInfo("unable to parse vcard: " + error);
                vret = false;
            } else if (vcards.size() == 0) {
                nrv.setStatus(Status.Failure);
                nrv.setInfo("vcard not present");
                vret = false;
            } else if (vcards.size() > 1) {
                nrv.setStatus(Status.Failure);
                nrv.setInfo("multiple vcards present");
                vret = false;
            } else {
                nrv.setStatus(Status.Success);
                nrv.setInfo("vcard present");
                if (warnings.size() > 0) {
                    List<String> vcard_warnings = warnings.get(0);
                    for (String s : vcard_warnings) {
                        System.err.println(s);
                    }
                }
                VCard vcard = vcards.get(0);
                ezvcard.ValidationWarnings vws =
                    vcard.validate(vcard.getVersion());
                String validation_warnings = vws.toString();
                nrv2 = new Result(nrv);
                if (validation_warnings.length() == 0) {
                    nrv2.setStatus(Status.Success);
                    nrv2.setInfo("valid");
                } else {
                    nrv2.setStatus(Status.Failure);
                    nrv2.setInfo("invalid: " + validation_warnings);
                }
            }
            context.addResult(nrv);
            if (nrv2 != null) {
                context.addResult(nrv2);
            }
        }

        boolean ret = true;
        List<ContentTest> tests =
            new ArrayList<ContentTest>(Arrays.asList(
                new AsEventActor(),
                new StandardObject()
            ));

        for (ContentTest test : tests) {
            boolean ret_inner = test.run(context, proto, arg_data);
            if (!ret_inner) {
                ret = false;
            }
            known_attributes.addAll(test.getKnownAttributes());
        }

        boolean ret2 = true;
        if (check_unknown) {
            ContentTest ua = new UnknownAttributes(known_attributes);
            ret2 = ua.run(context, proto, arg_data);
        }

        return (ret && vret && ret2);
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
