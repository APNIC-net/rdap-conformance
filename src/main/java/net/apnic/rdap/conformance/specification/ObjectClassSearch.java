package net.apnic.rdap.conformance.specification;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
