package net.apnic.rdap.conformance;

import com.google.gson.Gson;

import java.util.Map;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import net.apnic.rdap.conformance.specification.ObjectClass;

/**
 * <p>Specification class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
 */
public final class Specification {
    private String baseUrl = null;
    private double requestsPerSecond = 0;
    private Map<String, ObjectClass> objectClasses = null;

    private Specification() { }

    /**
     * <p>fromString.</p>
     *
     * @param jsonText a {@link java.lang.String} object.
     * @return a {@link net.apnic.rdap.conformance.Specification} object.
     */
    public static Specification fromString(final String jsonText) {
        Gson gson = new Gson();
        return gson.fromJson(jsonText, Specification.class);
    }

    /**
     * <p>fromPath.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @return a {@link net.apnic.rdap.conformance.Specification} object.
     * @throws java.io.IOException if any.
     */
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

    /**
     * <p>Getter for the field <code>baseUrl</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * <p>getObjectClass.</p>
     *
     * @param type a {@link java.lang.String} object.
     * @return a {@link net.apnic.rdap.conformance.specification.ObjectClass} object.
     */
    public ObjectClass getObjectClass(final String type) {
        return objectClasses.get(type);
    }

    /**
     * <p>Getter for the field <code>requestsPerSecond</code>.</p>
     *
     * @return a double.
     */
    public double getRequestsPerSecond() {
        return requestsPerSecond;
    }
}
