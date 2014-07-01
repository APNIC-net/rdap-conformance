package net.apnic.rdap.conformance;

import java.util.ArrayList;
import java.util.List;

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
        int len = node.size();
        for (int i = 0; i < (len - 1); i++) {
            s += nodeToString(node.get(i)) + ", ";
        }
        if (len >= 1) { 
            s += nodeToString(node.get(len - 1));
        }
        s += "] }";
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

        String s =
            getTestName() + "," +
            epath.toString() + "," +
            getStatusAsString() + "," +
            getCode() + "," +
            getInfo() + ",";
        int len = node.size();
        for (int i = 0; i < (len - 1); i++) {
            s += nodeToString(node.get(i)) + ":";
        }
        if (len >= 1) { 
            s += nodeToString(node.get(len - 1));
        }
        s += "," + getDocument() + "," + getReference();
        return s;
    }
}
