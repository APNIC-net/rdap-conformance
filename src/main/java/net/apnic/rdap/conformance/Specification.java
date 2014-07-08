package net.apnic.rdap.conformance;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.apnic.rdap.conformance.specification.ObjectClass;

public class Specification
{
    private String base_url = null;
    private double requests_per_second = 0;
    private Map<String, ObjectClass> object_classes = null;

    private Specification() {}

    public static Specification fromString(String json_text)
    {
        Gson gson = new Gson();
        return gson.fromJson(json_text, Specification.class);
    }

    public static Specification fromPath(String path)
        throws FileNotFoundException, IOException
    {
        BufferedReader reader =
            new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(path),
                    "UTF-8"
                )
            );
        String line = null;
        StringBuilder data = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            data.append(line);
        }
        reader.close();
        return fromString(data.toString());
    }

    public String getBaseUrl()
    {
        return base_url;
    }

    public ObjectClass getObjectClass(String type)
    {
        return object_classes.get(type);
    }

    public double getRequestsPerSecond()
    {
        return requests_per_second;
    }
}
