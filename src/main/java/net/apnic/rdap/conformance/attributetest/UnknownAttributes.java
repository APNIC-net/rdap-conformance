package net.apnic.rdap.conformance.attributetest;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;

/**
 * <p>UnknownAttributes class.</p>
 *
 * Takes a set of "known attributes", and checks in the map for
 * attributes that do not have corresponding entries in that set. Any
 * attribute that contains an underscore is assumed to be correct and
 * to belong to a documented extension; this may become stricter in
 * the future, depending on how many extensions are proposed and
 * implemented.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
 */
public final class UnknownAttributes implements AttributeTest {
    private Set<String> knownAttributes = null;

    /**
     * <p>Constructor for UnknownAttributes.</p>
     */
    public UnknownAttributes() { }

    /**
     * <p>Constructor for UnknownAttributes.</p>
     *
     * @param argKnownAttributes a {@link java.util.Set} object.
     */
    public UnknownAttributes(final Set<String> argKnownAttributes) {
        knownAttributes = argKnownAttributes;
    }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-06");
        nr.setReference("3.2");

        boolean success = true;

        Set<String> attributes = data.keySet();
        Sets.SetView<String> unknownAttributes =
            Sets.difference(attributes, knownAttributes);
        for (String unknownAttribute : unknownAttributes) {
            if (unknownAttribute.indexOf('_') == -1) {
                Result ua = new Result(nr);
                ua.setStatus(Status.Failure);
                ua.addNode(unknownAttribute);
                ua.setInfo("attribute is not permitted here or is "
                           + "non-standard and does not "
                           + "contain an underscore");
                context.addResult(ua);
                success = false;
            }
        }

        return success;
    }

    /**
     * <p>Getter for the field <code>knownAttributes</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getKnownAttributes() {
        return new HashSet<String>();
    }
}
