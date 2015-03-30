package net.apnic.rdap.conformance;

import java.io.Serializable;

/**
 * <p>SearchTest interface.</p>
 *
 * A SearchTest is an AttributeTest that allows for the search details
 * that produced the response to be provided to it, so that it can
 * confirm e.g. that the correct object has been returned.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3
 */
public interface SearchTest extends AttributeTest, Serializable {
    /**
     * <p>setSearchDetails.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @param pattern a {@link java.lang.String} object.
     */
    void setSearchDetails(String key, String pattern);
}
