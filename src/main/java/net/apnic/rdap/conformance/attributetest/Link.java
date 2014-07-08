package net.apnic.rdap.conformance.attributetest;

import java.util.ArrayList;
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
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.Utils;

import java.lang.IllegalArgumentException;
import java.util.Locale;
import java.util.Arrays;
import java.util.IllformedLocaleException;
import java.util.Set;
import com.google.common.net.MediaType;

import org.apache.http.client.HttpClient;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.Header;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.client.config.RequestConfig;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.attributetest.Link;

public class Link implements AttributeTest
{
    private static final Set<String> link_relations =
        Sets.newHashSet("about",
                        "alternate",
                        "appendix",
                        "archives",
                        "author",
                        "bookmark",
                        "canonical",
                        "chapter",
                        "collection",
                        "contents",
                        "copyright",
                        "create-form",
                        "current",
                        "describedby",
                        "describes",
                        "disclosure",
                        "duplicate",
                        "edit",
                        "edit-form",
                        "edit-media",
                        "enclosure",
                        "first",
                        "glossary",
                        "help",
                        "hosts",
                        "hub",
                        "icon",
                        "index",
                        "item",
                        "last",
                        "latest-version",
                        "license",
                        "lrdd",
                        "memento",
                        "monitor",
                        "monitor-group",
                        "next",
                        "next-archive",
                        "nofollow",
                        "noreferrer",
                        "original",
                        "payment",
                        "predecessor-version",
                        "prefetch",
                        "prev",
                        "preview",
                        "previous",
                        "prev-archive",
                        "privacy-policy",
                        "profile",
                        "identifiers",
                        "related",
                        "replies",
                        "search",
                        "section",
                        "self",
                        "service",
                        "start",
                        "stylesheet",
                        "subsection",
                        "successor-version",
                        "tag",
                        "terms-of-service",
                        "timegate",
                        "timemap",
                        "type",
                        "up",
                        "version-history",
                        "via",
                        "working-copy",
                        "working-copy-of");

    /* The first nine are defined by HTML 4.01, and the last two by
     * CSS 2. */
    private static final Set<String> media_types =
        Sets.newHashSet("aural",
                        "braille",
                        "handheld",
                        "print",
                        "projection",
                        "screen",
                        "tty",
                        "tv",
                        "all",
                        "embossed",
                        "speech");

    public Link() {}

    private boolean urlIsFetchable(Context context,
                                   Result proto,
                                   String key,
                                   String url)
    {
        boolean success = true;
        Result vnr = new Result(proto);
        vnr.addNode(key);
        int code = 0;
        HttpGet request = null;
        try {
            request = new HttpGet(url);
            RequestConfig config =
                RequestConfig.custom()
                             .setConnectionRequestTimeout(5000)
                             .setConnectTimeout(5000)
                             .setSocketTimeout(5000)
                             .build();
            request.setConfig(config);
            HttpResponse response =
                context.executeRequest(request);
            code = response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            vnr.setStatus(Status.Failure);
            vnr.setInfo("unable to send request for URL: " +
                            e.toString());
            context.addResult(vnr);
            success = false;
        }
        if (request != null) {
            request.releaseConnection();
        }
        /* Previously, this treated >= 400 as a problem. Of course, if
           an error response is being tested, that won't work. Ideally
           this would check against the 'current' status code. */
        if (success) {
            vnr.setStatus(Status.Success);
            vnr.setInfo("got response for URL (" + code + ")");
            context.addResult(vnr);
        }
        return success;
    }

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("5.2");

        boolean success = true;
        String value = Utils.getStringAttribute(context, nr, "value",
                                                Status.Failure, data);
        if ((value == null)
                || (!urlIsFetchable(context, nr, "value", value))) {
            success = false;
        }

        String href = Utils.getStringAttribute(context, nr, "href",
                                               Status.Failure, data);
        if ((href == null)
                || (!urlIsFetchable(context, nr, "href", href))) {
            success = false;
        }

        String rel = Utils.getStringAttribute(context, nr, "rel",
                                              Status.Failure, data);
        if (rel == null) {
            success = false;
        } else {
            Result valid = new Result(nr);
            if (link_relations.contains(rel)) {
                valid.setInfo("valid");
                valid.setStatus(Status.Success);
            } else {
                valid.setInfo("invalid: " + rel);
                valid.setStatus(Status.Failure);
                success = false;
            }
            results.add(valid);
        }

        if (data.get("hreflang") != null) {
            boolean is_list = false;
            Result hlr = new Result(nr);
            hlr.addNode("hreflang");
            hlr.setStatus(Status.Success);
            hlr.setInfo("present");
            List<String> hreflangs = new ArrayList<String>();
            try {
                String hreflang = (String) data.get("hreflang");
                hreflangs.add(hreflang);
            } catch (ClassCastException e) {}
            if (hreflangs.size() == 0) {
                try {
                    hreflangs = (List<String>) data.get("hreflang");
                    is_list = true;
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
                    if (is_list) {
                        hler.addNode(Integer.toString(i));
                    }
                    hler.setStatus(Status.Success);
                    hler.setInfo("valid");
                    if (hreflang.length() == 0) {
                        hler.setStatus(Status.Failure);
                        hler.setInfo("empty");
                    } else {
                        try {
                            Locale.Builder hlt =
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

        String title = Utils.getStringAttribute(context, nr, "title",
                                                Status.Notification, data);

        String media = Utils.getStringAttribute(context, nr, "media",
                                                Status.Notification, data);
        if (media != null) {
            Result mtr = new Result(nr);
            mtr.addNode("media");
            mtr.setStatus(Status.Success);
            mtr.setInfo("registered");
            if (!media_types.contains(media)) {
                /* It's not impossible that the media type is one
                    * that has been registered in the meantime, which
                    * is why this is only a warning. */
                mtr.setStatus(Status.Warning);
                mtr.setInfo("unregistered: " + media);
            }
            results.add(mtr);
        }

        String type = Utils.getStringAttribute(context, nr, "type",
                                               Status.Notification, data);
        if (type != null) {
            Result tvr = new Result(nr);
            tvr.addNode("type");
            tvr.setStatus(Status.Success);
            tvr.setInfo("valid");
            try {
                MediaType.parse(type);
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

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet("type", "title", "media",
                               "href", "hreflang", "rel", "value");
    }
}
