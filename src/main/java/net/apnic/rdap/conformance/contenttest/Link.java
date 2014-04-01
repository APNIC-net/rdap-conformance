package net.apnic.rdap.conformance.contenttest;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.IllformedLocaleException;
import com.google.common.collect.Sets;

import org.apache.http.client.HttpClient;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpStatus;
import org.apache.http.Header;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.client.config.RequestConfig;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.ContentTest;

import net.apnic.rdap.conformance.contenttest.Link;

public class Link implements ContentTest
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

    public Link() {}

    private boolean urlIsFetchable(Context context,
                                   Result proto,
                                   String url)
    {
        boolean success = true;
        Result vnr = new Result(proto);
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
                context.getHttpClient().execute(request);
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
                       Object arg_data)
    {
        List<Result> results = context.getResults();

        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("5.2");

        Map<String, Object> data;
        try {
            data = (Map<String, Object>) arg_data;
        } catch (ClassCastException e) {
            nr.setInfo("structure is invalid");
            nr.setStatus(Status.Failure);
            results.add(nr);
            return false;
        }

        String value = null;
        Result vnr = new Result(nr);
        vnr.addNode("value");
        boolean success = true;
        try {
            value = (String) data.get("value");
        } catch (ClassCastException e) {}
        if (value == null) {
            vnr.setStatus(Status.Failure);
            vnr.setInfo("not present");
            results.add(vnr);
            success = false;
        } else {
            vnr.setStatus(Status.Success);
            vnr.setInfo("present");
            results.add(vnr);
            if (!urlIsFetchable(context, vnr, value)) {
                success = false;
            }
        }

        String href = null;
        Result vhr = new Result(nr);
        vhr.addNode("href");
        try {
            href = (String) data.get("href");
        } catch (ClassCastException e) {}
        if (href == null) {
            vhr.setStatus(Status.Failure);
            vhr.setInfo("not present");
            results.add(vhr);
            success = false;
        } else {
            vhr.setStatus(Status.Success);
            vhr.setInfo("present");
            results.add(vhr);
            if (!urlIsFetchable(context, vhr, href)) {
                success = false;
            }
        }

        String rel = null;
        Result vrr = new Result(nr);
        vrr.addNode("rel");
        try {
            rel = (String) data.get("rel");
        } catch (ClassCastException e) {}
        if (rel == null) {
            vrr.setStatus(Status.Failure);
            vrr.setInfo("not present");
            results.add(vrr);
            success = false;
        } else {
            vrr.setStatus(Status.Success);
            vrr.setInfo("present");
            results.add(vrr);

            Result valid = new Result(nr);
            if (link_relations.contains(rel)) {
                valid.setInfo("valid");
                valid.setStatus(Status.Success);
            } else {
                valid.setInfo("not valid");
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
                            hler.setInfo(e.toString());
                        }
                    }
                    results.add(hler);
                }
            }
        }
            
        return success;
    }
}
