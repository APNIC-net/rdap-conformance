package net.apnic.rdap.conformance.specification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ObjectClass
{
    private boolean supported = false;
    private List<String> exists = new ArrayList<String>();
    private List<String> not_exists = new ArrayList<String>();
    private List<String> redirects = new ArrayList<String>();
    private ObjectClassSearch search = new ObjectClassSearch();

    private ObjectClass() {}

    public boolean isSupported()
    {
        return supported;
    }

    public List<String> getExists()
    {
        return exists;
    }

    public List<String> getNotExists()
    {
        return not_exists;
    }

    public List<String> getRedirects()
    {
        return redirects;
    }

    public ObjectClassSearch getObjectClassSearch()
    {
        return search;
    }
}
