package net.apnic.rdap.conformance;

import java.io.Serializable;
import net.apnic.rdap.conformance.Test;

public interface SearchTest extends ContentTest, Serializable
{
    void setSearchDetails(String key, String pattern);
}
