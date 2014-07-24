package net.apnic.rdap.conformance.attributetest;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import ezvcard.VCard;
import ezvcard.Ezvcard;
import ezvcard.Ezvcard.ParserChainJsonString;
import ezvcard.ValidationWarnings;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.SearchTest;
import net.apnic.rdap.conformance.Utils;

/**
 * <p>Entity class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Entity implements SearchTest {
    private boolean checkUnknown = false;
    private boolean searchContext = false;
    private String handle = null;
    private String fn = null;
    private Set<String> knownAttributes = null;

    private static final Set<String> ROLES =
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

    /**
     * <p>Constructor for Entity.</p>
     */
    public Entity() { }

    /**
     * <p>Constructor for Entity.</p>
     *
     * @param argHandle a {@link java.lang.String} object.
     * @param argCheckUnknown a boolean.
     */
    public Entity(final String argHandle, final boolean argCheckUnknown) {
        handle = argHandle;
        checkUnknown = argCheckUnknown;
        searchContext = false;
    }

    /** {@inheritDoc} */
    public void setSearchDetails(final String key, final String pattern) {
        fn = null;
        handle = null;
        searchContext = false;

        if (key.equals("handle")) {
            handle = pattern;
            searchContext = true;
        } else if (key.equals("fn")) {
            fn = pattern;
            searchContext = true;
        }
    }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        knownAttributes = Sets.newHashSet("handle", "roles", "vcardArray");

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("6.1");

        String responseHandle =
            Utils.getStringAttribute(context, nr, "handle",
                                     Status.Warning, data);

        if ((responseHandle != null) && (handle != null)) {
            Result r2 = new Result(nr);
            r2.addNode("handle");
            r2.setStatus(Status.Success);
            if (searchContext) {
                r2.setInfo("response handle matches search pattern");
                if (!Utils.matchesSearch(handle, responseHandle)) {
                    r2.setStatus(Status.Warning);
                    r2.setInfo("response handle does not "
                               + "match search pattern");
                }
            } else {
                r2.setInfo("response handle matches requested handle");
                if (!responseHandle.equals(handle)) {
                    r2.setStatus(Status.Warning);
                    r2.setInfo("response handle does not "
                               + "match requested handle");
                }
            }
            context.addResult(r2);
        }

        Result hr = new Result(nr);
        hr.setStatus(Status.Success);
        hr.addNode("roles");
        hr.setInfo("present");
        Object responseRoles = data.get("roles");
        if (responseRoles == null) {
            hr.setStatus(Status.Notification);
            hr.setInfo("not present");
        }
        context.addResult(hr);
        if (responseRoles != null) {
            Result ilr = new Result(nr);
            ilr.setStatus(Status.Success);
            ilr.addNode("roles");
            ilr.setInfo("is an array");
            List<String> responseRolesList = null;
            try {
                responseRolesList = (List<String>) responseRoles;
            } catch (ClassCastException e) {
                ilr.setStatus(Status.Failure);
                ilr.setInfo("is not an array");
            }
            context.addResult(ilr);
            if (responseRolesList != null) {
                int i = 0;
                for (String role : responseRolesList) {
                    Result rr = new Result(nr);
                    rr.addNode("roles");
                    rr.addNode(Integer.toString(i));
                    rr.setInfo("registered");
                    rr.setStatus(Status.Success);
                    rr.setReference("11.2.3");
                    if (!ROLES.contains(role)) {
                        rr.setInfo("unregistered: " + role);
                        rr.setStatus(Status.Failure);
                    }
                    context.addResult(rr);
                    i++;
                }
            }
        }

        boolean vret = true;
        Object vcardArray =
            Utils.getAttribute(context, nr, "vcardArray", null, data);
        VCard vcard = null;
        if (vcardArray != null) {
            String json = new Gson().toJson(vcardArray);
            List<VCard> vcards = null;
            List<List<String>> warnings = null;
            String error = null;
            try {
                ParserChainJsonString pcjs =
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
                    List<String> vcardWarnings = warnings.get(0);
                    for (String s : vcardWarnings) {
                        System.err.println(s);
                    }
                }
                vcard = vcards.get(0);
                ValidationWarnings vws =
                    vcard.validate(vcard.getVersion());
                String validationWarnings = vws.toString();
                nrv2 = new Result(nrv);
                nrv2.setDetails((validationWarnings.length() == 0),
                                "valid", "invalid: " + validationWarnings);
            }
            context.addResult(nrv);
            if (nrv2 != null) {
                context.addResult(nrv2);
            }
        }

        if ((fn != null) && searchContext) {
            Result r2 = new Result(nr);
            r2.addNode("vcardArray");
            if ((vcard == null)
                    || (vcard.getFormattedName().getValue() == null)) {
                r2.setStatus(Status.Warning);
                r2.setInfo("no vcard or name in response so unable to "
                           + "check search pattern");
            } else {
                String name = vcard.getFormattedName().getValue();
                r2.setDetails(Utils.matchesSearch(fn, name),
                              Status.Success,
                              "response name matches search pattern",
                              Status.Warning,
                              "response name does not match search pattern");
            }
            context.addResult(r2);
        }

        boolean ret = Utils.runTestList(
            context, proto, data, knownAttributes, checkUnknown,
            Arrays.asList(
                new AsEventActor(),
                new ArrayAttribute(new Ip(), "networks"),
                new ArrayAttribute(new Autnum(), "autnums"),
                new StandardObject()
            )
        );

        return (ret && vret);
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
