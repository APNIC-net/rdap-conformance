package net.apnic.rdap.conformance.specification;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>ObjectClassSearch class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.4-SNAPSHOT
 */
public final class ObjectClassSearch {
    private boolean supported = false;
    private List<String> exists = new ArrayList<String>();
    private List<String> notExists = new ArrayList<String>();
    private List<String> truncated = new ArrayList<String>();

    /**
     * <p>Constructor for ObjectClassSearch.</p>
     */
    public ObjectClassSearch() { }

    /**
     * <p>Getter for the field <code>supported</code>.</p>
     *
     * @return a boolean.
     */
    public boolean getSupported() {
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
     * <p>Getter for the field <code>truncated</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<String> getTruncated() {
        return truncated;
    }
}
