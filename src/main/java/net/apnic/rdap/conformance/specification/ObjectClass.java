package net.apnic.rdap.conformance.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>ObjectClass class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3
 */
public final class ObjectClass {
    private boolean supported = false;
    private List<String> exists = new ArrayList<String>();
    private List<String> notExists = new ArrayList<String>();
    private List<String> redirects = new ArrayList<String>();
    private Map<String, ObjectClassSearch> search =
        new HashMap<String, ObjectClassSearch>();

    private ObjectClass() { }

    /**
     * <p>isSupported.</p>
     *
     * @return a boolean.
     */
    public boolean isSupported() {
        return supported;
    }

    /**
     * <p>Getter for the field <code>exists</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<String> getExists() {
        return exists;
    }

    /**
     * <p>Getter for the field <code>notExists</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<String> getNotExists() {
        return notExists;
    }

    /**
     * <p>Getter for the field <code>redirects</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<String> getRedirects() {
        return redirects;
    }

    /**
     * <p>Getter for the field <code>search</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, ObjectClassSearch> getSearch() {
        return search;
    }
}
