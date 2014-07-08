package net.apnic.rdap.conformance;

import java.util.Set;
import java.util.Map;

public interface AttributeTest
{
    boolean run(Context context, Result proto, Map<String, Object> content);
    Set<String> getKnownAttributes();
}
