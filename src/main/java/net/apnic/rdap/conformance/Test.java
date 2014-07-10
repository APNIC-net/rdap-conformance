package net.apnic.rdap.conformance;

/**
 * <p>Test interface.</p>
 *
 * The most basic test interface. Returns a boolean indicating whether
 * the test succeeded or failed. Tests should populate the result list
 * in the context object.
 *
 * Although the other test interfaces (except for ObjectTest) do not
 * extend this interface, they should all operate in a similar
 * fashion: accept context object in some way, add results to that
 * object, return boolean indicating success/failure.
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
