package net.apnic.rdap.conformance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;

public final class Result {
    public enum Status { Success, Notification, Warning, Failure };

    private Status status     = Status.Notification;
    private String path       = "";
    private String testName  = "";
    private String code       = "";
    private String info       = "";
    private String document   = "";
    private String reference  = "";
    private List<String> node = new ArrayList<String>();
    private boolean statusSet = false;

    public Result() { }

    public Result(final Result r) {
        setStatus(r.getStatus());
        setPath(r.getPath());
        setTestName(r.getTestName());
        setCode(r.getCode());
        setInfo(r.getInfo());
        setDocument(r.getDocument());
        setReference(r.getReference());
        List<String> newNode = new ArrayList<String>();
        for (String s : r.getNode()) {
            newNode.add(s);
        }
        setNode(newNode);
    }

    public Result(final Status argStatus,
                  final String argPath,
                  final String argTestName,
                  final String argCode,
                  final String argInfo,
                  final String argDocument,
                  final String argReference) {
        status    = argStatus;
        path      = argPath;
        testName  = argTestName;
        code      = argCode;
        info      = argInfo;
        document  = argDocument;
        reference = argReference;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status s) {
        statusSet = true;
        status = s;
    }

    public boolean getStatusSet() {
        return statusSet;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String p) {
        path = p;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(final String t) {
        testName = t;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String c) {
        code = c;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(final String i) {
        info = i;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(final String d) {
        document = d;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(final String r) {
        reference = r;
    }

    public List<String> getNode() {
        return node;
    }

    public void setNode(final List<String> n) {
        node = n;
    }

    public void addNode(final String s) {
        node.add(s);
    }

    public void setStatusAndInfo(final Status s, final String i) {
        setStatus(s);
        setInfo(i);
    }

    public boolean setDetails(final boolean success,
                              final String successInfo,
                              final String failureInfo) {
        return setDetails(success,
                          Status.Success, successInfo,
                          Status.Failure, failureInfo);
    }

    public boolean setDetails(final boolean success,
                              final Status successStatus,
                              final String successInfo,
                              final Status failureStatus,
                              final String failureInfo) {
        if (success) {
            setStatusAndInfo(successStatus, successInfo);
        } else {
            setStatusAndInfo(failureStatus, failureInfo);
        }
        return success;
    }

    private String getStatusAsString() {
        return (status == Status.Success)      ? "success"
             : (status == Status.Notification) ? "notification"
             : (status == Status.Warning)      ? "warning"
                                               : "failure";
    }

    private String nodeToString(final String s) {
        if (s.matches("^\\d+$")) {
            return s;
        } else {
            return "\"" + s + "\"";
        }
    }

    public String toJson() {
        String s =
               "{ \"status\": \"" + getStatusAsString() + "\", "
               + "\"path\": \"" + getPath() + "\", "
               + "\"testName\": \"" + getTestName() + "\", "
               + "\"code\": \"" + getCode() + "\", "
               + "\"info\": \"" + getInfo() + "\", "
               + "\"document\": \"" + getDocument() + "\", "
               + "\"reference\": \"" + getReference() + "\", "
               + "\"node\": [";
        List<String> nodeMapped = new ArrayList<String>();
        for (String n : node) {
            nodeMapped.add(nodeToString(n));
        }
        s += Joiner.on(", ").join(nodeMapped) + "] }";
        return s;
    }

    public String toString() {
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

        List<String> nodeMapped = new ArrayList<String>();
        for (String n : node) {
            nodeMapped.add(nodeToString(n));
        }

        return
            Joiner.on(",").join(
                Arrays.asList(getTestName(),
                              epath.toString(),
                              getStatusAsString(),
                              getCode(),
                              getInfo(),
                              Joiner.on(":").join(nodeMapped),
                              getDocument(),
                              getReference())
            );
    }
}
