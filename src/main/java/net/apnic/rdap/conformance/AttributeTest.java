package net.apnic.rdap.conformance;

import java.util.Set;
import java.util.Map;

/**
 * <p>AttributeTest interface.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
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
