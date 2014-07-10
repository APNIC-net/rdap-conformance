package net.apnic.rdap.conformance.specification;

import java.util.ArrayList;
import java.util.List;

public class ObjectClass {
    private boolean supported = false;
    private List<String> exists = new ArrayList<String>();
    private List<String> notExists = new ArrayList<String>();
    private List<String> redirects = new ArrayList<String>();
    private ObjectClassSearch search = new ObjectClassSearch();

    private ObjectClass() { }

    public boolean isSupported() {
        return supported;
    }

    public List<String> getExists() {
        return exists;
    }

    public List<String> getNotExists() {
        return notExists;
    }

    public List<String> getRedirects() {
        return redirects;
    }

    public ObjectClassSearch getObjectClassSearch() {
        return search;
    }
}
