package net.apnic.rdap.conformance.specification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ObjectClassSearch
{
    private boolean supported = false;
    private Map<String, List<String>> values =
        new HashMap<String, List<String>>();

    public ObjectClassSearch() {}

    public boolean isSupported()
    {
        return supported;
    }

    public Map<String, List<String>> getValues()
    {
        return values;
    }
}
