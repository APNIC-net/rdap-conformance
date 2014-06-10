package net.apnic.rdap.conformance;

import java.util.List;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Specification;

public class Context
{
    private HttpClient http_client = null;
    private Specification specification = null;
    private List<Result> results = new ArrayList<Result>();
    private String content_type = null;
    private int index;

    public Context() 
    {
        index = 0;
    }

    public HttpClient getHttpClient()
    {
        return http_client;
    }

    public void setHttpClient(HttpClient hc)
    {
        http_client = hc;
    }

    public List<Result> getResults()
    {
        return results;
    }

    public void addResult(Result r)
    {
        results.add(r);
        flushResults();
    }

    public Specification getSpecification()
    {
        return specification;
    }

    public void setSpecification(Specification s)
    {
        specification = s;
    }

    public String getContentType()
    {
        return ((content_type == null) ? "application/rdap+json"
                                       : content_type);
    }

    public void setContentType(String s)
    {
        content_type = s;
    }

    public void flushResults()
    {
        List<Result> ml = getResults();
        int size = ml.size();
        int i;
        for (i = index; i < size; i++) {
            System.out.println(ml.get(i).toString());
        }
        index = i;
    }
}
