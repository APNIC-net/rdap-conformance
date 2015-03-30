package net.apnic.rdap.conformance;

import java.util.Set;
import java.util.Map;

/**
 * <p>AttributeTest interface.</p>
 *
 * An AttributeTest tests a specific attribute in an arbitrary map
 * (from String to Object).
 *
 * getKnownAttributes allows for AttributeTests to be composed for a
 * particular map. Once the tests have been run, unknown attributes
 * can be detected and reported on, since they will be those that
 * aren't present in the union of the tests' known attributes.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.4-SNAPSHOT
 */
public interface AttributeTest {
    /**
     * <p>run.</p>
     *
     * @param context a {@link net.apnic.rdap.conformance.Context} object.
     * @param proto a {@link net.apnic.rdap.conformance.Result} object.
     * @param content a {@link java.util.Map} object.
     * @return a boolean.
     */
    boolean run(Context context, Result proto, Map<String, Object> content);
    /**
     * <p>getKnownAttributes.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    Set<String> getKnownAttributes();
}
