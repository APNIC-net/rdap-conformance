package net.apnic.rdap.conformance;

import java.io.Serializable;

/**
 * <p>SearchTest interface.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.2
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
