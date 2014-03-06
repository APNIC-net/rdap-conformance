package net.apnic.rdap.conformance;

import java.util.List;
import org.apache.http.client.HttpClient;

import net.apnic.rdap.conformance.Context;

public interface Test
{
    public boolean run(Context context);
}
