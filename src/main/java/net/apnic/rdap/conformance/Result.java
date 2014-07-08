package net.apnic.rdap.conformance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;

public class Result
{
    public enum Status { Success, Notification, Warning, Failure };

    private Status status     = Status.Notification;
    private String path       = "";
    private String test_name  = "";
    private String code       = "";
    private String info       = "";
    private String document   = "";
    private String reference  = "";
    private List<String> node = new ArrayList<String>();
    boolean status_set        = false;

    public Result() {}

    public Result(Result r)
    {
        setStatus(r.getStatus());
        setPath(r.getPath());
        setTestName(r.getTestName());
        setCode(r.getCode());
        setInfo(r.getInfo());
        setDocument(r.getDocument());
        setReference(r.getReference());
        List<String> new_node = new ArrayList<String>();
        for (String s : r.getNode()) {
            new_node.add(s);
        }
        setNode(new_node);
    }

    public Result(Status arg_status,    String arg_path, String arg_test_name,
                  String arg_code,      String arg_info, String arg_document,
                  String arg_reference)
    {
        status    = arg_status;
        path      = arg_path;
        test_name = arg_test_name;
        code      = arg_code;
        info      = arg_info;
        document  = arg_document;
        reference = arg_reference;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status s)
    {
        status_set = true;
        status = s;
    }

    public boolean getStatusSet()
    {
        return status_set;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String p)
    {
        path = p;
    }

    public String getTestName()
    {
        return test_name;
    }

    public void setTestName(String t)
    {
        test_name = t;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String c)
    {
        code = c;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String i)
    {
        info = i;
    }

    public String getDocument()
    {
        return document;
    }

    public void setDocument(String d)
    {
        document = d;
    }

    public String getReference()
    {
        return reference;
    }

    public void setReference(String r)
    {
        reference = r;
    }

    public List<String> getNode()
    {
        return node;
    }

    public void setNode(List<String> n)
    {
        node = n;
    }

    public void addNode(String s)
    {
        node.add(s);
    }

    public void setStatusAndInfo(Status s, String i)
    {
        setStatus(s);
        setInfo(i);
    }

    public boolean setDetails(boolean success,
                              String success_info,
                              String failure_info)
    {
        return setDetails(success,
                          Status.Success, success_info,
                          Status.Failure, failure_info);
    }

    public boolean setDetails(boolean success,
                              Status success_status,
                              String success_info,
                              Status failure_status,
                              String failure_info)
    {
        if (success) {
            setStatusAndInfo(success_status, success_info);
        } else {
            setStatusAndInfo(failure_status, failure_info);
        }
        return success;
    }

    private String getStatusAsString()
    {
        return (status == Status.Success)      ? "success"
             : (status == Status.Notification) ? "notification"
             : (status == Status.Warning)      ? "warning"
                                               : "failure";
    }

    private String nodeToString(String s)
    {
        if (s.matches("^\\d+$")) {
            return s;
        } else {
            return "\"" + s + "\"";
        }
    }

    public String toJson()
    {
        String s =
               "{ \"status\": \"" + getStatusAsString() + "\", " +
                 "\"path\": \"" + getPath() + "\", " +
                 "\"testName\": \"" + getTestName() + "\", " +
                 "\"code\": \"" + getCode() + "\", " +
                 "\"info\": \"" + getInfo() + "\", " +
                 "\"document\": \"" + getDocument() + "\", " +
                 "\"reference\": \"" + getReference() + "\", " +
                 "\"node\": [";
        List<String> node_mapped = new ArrayList<String>();
        for (String n : node) {
            node_mapped.add(nodeToString(n));
        }
        s += Joiner.on(", ").join(node_mapped) + "] }";
        return s;
    }

    public String toString()
    {
        StringBuilder epath = new StringBuilder();
        int slen = path.length();
        for (int i = 0; i < slen; i++) {
            char c = path.charAt(i);
            if (Character.isISOControl(c)) {
                epath.append("{\\x" + ((int) c) + "}");
            } else {
                epath.append(c);
            }
        }

        List<String> node_mapped = new ArrayList<String>();
        for (String n : node) {
            node_mapped.add(nodeToString(n));
        }

        return
            Joiner.on(",").join(
                Arrays.asList(getTestName(),
                              epath.toString(),
                              getStatusAsString(),
                              getCode(),
                              getInfo(),
                              Joiner.on(":").join(node_mapped),
                              getDocument(),
                              getReference())
            );
    }
}
