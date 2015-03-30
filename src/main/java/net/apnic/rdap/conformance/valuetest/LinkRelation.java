package net.apnic.rdap.conformance.valuetest;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ValueTest;

/**
 * <p>LinkRelation class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.4-SNAPSHOT
 */
public final class LinkRelation implements ValueTest {
    private static StringSet stringSet =
        new StringSet(
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
                            "working-copy-of")
        );

    /**
     * <p>Constructor for LinkRelation.</p>
     */
    public LinkRelation() { }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Object data) {
        return stringSet.run(context, proto, data);
    }
}
