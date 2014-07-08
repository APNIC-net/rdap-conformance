package net.apnic.rdap.conformance.attributetest;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import ezvcard.*;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.SearchTest;
import net.apnic.rdap.conformance.Utils;

public class Entity implements SearchTest
{
    boolean check_unknown = false;
    boolean search_context = false;
    String handle = null;
    String fn = null;
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
        search_context = false;
    }

    public void setSearchDetails(String key, String pattern)
    {
        fn = null;
        handle = null;
        search_context = false;

        if (key.equals("handle")) {
            handle = pattern;
            search_context = true;
        } else if (key.equals("fn")) {
            fn = pattern;
            search_context = true;
        }
    }

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        known_attributes = Sets.newHashSet("handle", "roles", "vcardArray");

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("6.1");

        String response_handle =
            Utils.getStringAttribute(context, nr, "handle",
                                     Status.Warning, data);

        if ((response_handle != null) && (handle != null)) {
            Result r2 = new Result(nr);
            r2.addNode("handle");
            r2.setStatus(Status.Success);
            if (search_context) {
                r2.setInfo("response handle matches search pattern");
                String handle_pattern = handle.replaceAll("\\*", ".*");
                /* At least some servers will add implicit ".*" to the
                 * beginning and the end of the pattern, so add those
                 * here too. This may become configurable, so that
                 * stricter servers can verify their behaviour.
                 * Searches are presumed to be case-insensitive as
                 * well. */
                handle_pattern = ".*" + handle_pattern + ".*";
                Pattern p =
                    Pattern.compile(handle_pattern,
                                    Pattern.CASE_INSENSITIVE
                                  | Pattern.UNICODE_CASE);
                if (!p.matcher(response_handle).matches()) {
                    r2.setStatus(Status.Warning);
                    r2.setInfo("response handle does not " +
                               "match search pattern");
                }
            } else {
                r2.setInfo("response handle matches requested handle");
                if (!response_handle.equals(handle)) {
                    r2.setStatus(Status.Warning);
                    r2.setInfo("response handle does not " +
                               "match requested handle");
                }
            }
            context.addResult(r2);
        }

        Result hr = new Result(nr);
        hr.setStatus(Status.Success);
        hr.addNode("roles");
        hr.setInfo("present");
        Object response_roles = data.get("roles");
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
            Utils.getAttribute(context, nr, "vcardArray", null, data);
        VCard vcard = null;
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
                vcard = vcards.get(0);
                ValidationWarnings vws =
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

        if ((fn != null) && search_context) {
            Result r2 = new Result(nr);
            r2.addNode("vcardArray");
            if ((vcard == null)
                    || (vcard.getFormattedName().getValue() == null)) {
                r2.setStatus(Status.Warning);
                r2.setInfo("no vcard or name in response so unable to " +
                           "check search pattern");
            } else {
                r2.setStatus(Status.Success);
                r2.setInfo("response name matches search pattern");
                String fn_pattern = fn.replaceAll("\\*", ".*");
                fn_pattern = ".*" + fn_pattern + ".*";
                Pattern p =
                    Pattern.compile(fn_pattern,
                                    Pattern.CASE_INSENSITIVE
                                  | Pattern.UNICODE_CASE);
                String name = vcard.getFormattedName().getValue();
                if (!p.matcher(name).matches()) {
                    r2.setStatus(Status.Warning);
                    r2.setInfo("response name does not " +
                               "match search pattern");
                }
            }
            context.addResult(r2);
        }

        boolean ret = Utils.runTestList(
            context, proto, data, known_attributes, check_unknown,
            Arrays.asList(
                new AsEventActor(),
                new StandardObject()
            )
        );

        return (ret && vret);
    }

    public Set<String> getKnownAttributes()
    {
        return known_attributes;
    }
}
