package net.apnic.rdap.conformance;

/**
 * <p>ObjectTest interface.</p>
 *
 * An ObjectTest is a Test that allows for a specific request URL to
 * be set prior to it being run, in which case that URL will be used
 * instead of the one the object would have generated on execution.
 * For example, test.ip.Standard's default constructor takes an IP
 * address range as a string. By default, it will take the base URL
 * from the context and create a target URL by appending "/ip/" and
 * the range thereto, but if setUrl is called on that object before it
 * is run, then it will use that URL instead.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.4-SNAPSHOT
 */
public interface ObjectTest extends Test {
    /**
     * <p>setUrl.</p>
     *
     * @param url a {@link java.lang.String} object.
     */
    void setUrl(String url);
}
