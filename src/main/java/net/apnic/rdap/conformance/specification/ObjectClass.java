package net.apnic.rdap.conformance.specification;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>ObjectClass class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
 */
public final class ObjectClass {
    private boolean supported = false;
    private List<String> exists = new ArrayList<String>();
    private List<String> notExists = new ArrayList<String>();
    private List<String> redirects = new ArrayList<String>();
    private ObjectClassSearch search = new ObjectClassSearch();

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
     * <p>getObjectClassSearch.</p>
     *
     * @return a {@link net.apnic.rdap.conformance.specification.ObjectClassSearch} object.
     */
    public ObjectClassSearch getObjectClassSearch() {
        return search;
    }
}
