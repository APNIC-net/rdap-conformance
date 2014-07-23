package net.apnic.rdap.conformance.specification;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * <p>ObjectClassSearch class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class ObjectClassSearch {
    private boolean supported = false;
    private Map<String, ObjectClassParameterSearch> parameters =
        new HashMap<String, ObjectClassParameterSearch>();

    /**
     * <p>Constructor for ObjectClassSearch.</p>
     */
    public ObjectClassSearch() { }

    /**
     * <p>Getter for the field <code>parameters</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, ObjectClassParameterSearch> getParameters() {
        return parameters;
    }
}
