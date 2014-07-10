package net.apnic.rdap.conformance;

/**
 * <p>Test interface.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
 */
public interface Test {
    /**
     * <p>run.</p>
     *
     * @param context a {@link net.apnic.rdap.conformance.Context} object.
     * @return a boolean.
     */
    boolean run(Context context);
}
