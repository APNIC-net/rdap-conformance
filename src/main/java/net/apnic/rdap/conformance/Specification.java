package net.apnic.rdap.conformance;

import com.google.gson.Gson;

import java.util.Map;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import net.apnic.rdap.conformance.specification.ObjectClass;

public final class Specification {
    private String baseUrl = null;
    private double requestsPerSecond = 0;
    private Map<String, ObjectClass> objectClasses = null;

    private Specification() { }

    public static Specification fromString(final String jsonText) {
        Gson gson = new Gson();
        return gson.fromJson(jsonText, Specification.class);
    }

    public static Specification fromPath(final String path)
            throws IOException {
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

    public String getBaseUrl() {
        return baseUrl;
    }

    public ObjectClass getObjectClass(final String type) {
        return objectClasses.get(type);
    }

    public double getRequestsPerSecond() {
        return requestsPerSecond;
    }
}
