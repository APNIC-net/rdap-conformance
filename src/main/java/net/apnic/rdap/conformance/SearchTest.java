package net.apnic.rdap.conformance;

import java.io.Serializable;

public interface SearchTest extends AttributeTest, Serializable
{
    void setSearchDetails(String key, String pattern);
}
