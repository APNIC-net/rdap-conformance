package net.apnic.rdap.conformance.attributetest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.valuetest.LinkRelation;
import net.apnic.rdap.conformance.valuetest.MediaType;

import java.util.Locale;
import java.util.IllformedLocaleException;
import com.google.common.collect.Sets;

/**
 * <p>Link class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Link implements AttributeTest {
    /**
     * <p>Constructor for Link.</p>
     */
    public Link() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-07");
        nr.setReference("5.2");

        boolean success = true;
        String value = Utils.getStringAttribute(context, nr, "value",
                                                Status.Failure, data);
        if (value == null) {
            success = false;
        } else {
            Result nr2 = new Result(nr);
            nr2.addNode("value");
            context.submitTest(new net.apnic.rdap.conformance.test.common.Link(
                value, nr2
            ));
        }

        String href = Utils.getStringAttribute(context, nr, "href",
                                               Status.Failure, data);
        if (href == null) {
            success = false;
        } else {
            Result nr2 = new Result(nr);
            nr2.addNode("href");
            context.submitTest(new net.apnic.rdap.conformance.test.common.Link(
                href, nr2
            ));
        }

        AttributeTest rel = new ScalarAttribute("rel", new LinkRelation());
        boolean relres = rel.run(context, nr, data);
        if (!relres) {
            success = false;
        }

        if (data.get("hreflang") != null) {
            boolean isList = false;
            Result hlr = new Result(nr);
            hlr.addNode("hreflang");
            hlr.setStatus(Status.Success);
            hlr.setInfo("present");
            List<String> hreflangs = new ArrayList<String>();
            String hreflangt = Utils.castToString(data.get("hreflang"));
            if (hreflangt != null) {
                hreflangs.add(hreflangt);
            } else {
                try {
                    hreflangs = (List<String>) data.get("hreflang");
                    isList = true;
                } catch (ClassCastException e) {
                    hlr.setStatus(Status.Failure);
                    hlr.setInfo("structure is invalid");
                }
            }
            results.add(hlr);
            if (hreflangs.size() != 0) {
                int i = 0;
                for (String hreflang : hreflangs) {
                    Result hler = new Result(nr);
                    hler.addNode("hreflang");
                    if (isList) {
                        hler.addNode(Integer.toString(i));
                    }
                    hler.setStatus(Status.Success);
                    hler.setInfo("valid");
                    if (hreflang.length() == 0) {
                        hler.setStatus(Status.Failure);
                        hler.setInfo("empty");
                    } else {
                        try {
                            new Locale.Builder()
                                      .setLanguageTag(hreflang);
                        } catch (IllformedLocaleException e) {
                            hler.setStatus(Status.Failure);
                            hler.setInfo("invalid: " + e.toString());
                        }
                    }
                    results.add(hler);
                }
            }
        }

        Utils.getStringAttribute(context, nr, "title",
                                 Status.Notification, data);

        AttributeTest med = new ScalarAttribute("media", new MediaType());
        boolean medres = med.run(context, nr, data);
        if (!medres) {
            success = false;
        }

        String type = Utils.getStringAttribute(context, nr, "type",
                                               Status.Notification, data);
        if (type != null) {
            Result tvr = new Result(nr);
            tvr.addNode("type");
            tvr.setStatus(Status.Success);
            tvr.setInfo("valid");
            try {
                com.google.common.net.MediaType.parse(type);
            } catch (IllegalArgumentException e) {
                tvr.setInfo(e.toString());
                tvr.setStatus(Status.Failure);
            }
            results.add(tvr);
        }

        AttributeTest ua = new UnknownAttributes(getKnownAttributes());
        boolean ret2 = ua.run(context, proto, data);

        return (success && ret2);
    }

    /**
     * <p>getKnownAttributes.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getKnownAttributes() {
        return Sets.newHashSet("type", "title", "media",
                               "href", "hreflang", "rel", "value");
    }
}
