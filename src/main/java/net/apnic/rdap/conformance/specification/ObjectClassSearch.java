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
    private Map<String, List<String>> values =
        new HashMap<String, List<String>>();

    /**
     * <p>Constructor for ObjectClassSearch.</p>
     */
    public ObjectClassSearch() { }

    /**
     * <p>isSupported.</p>
     *
     * @return a boolean.
     */
    public boolean isSupported() {
        return supported;
    }

    /**
     * <p>Getter for the field <code>values</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, List<String>> getValues() {
        return values;
    }
}
