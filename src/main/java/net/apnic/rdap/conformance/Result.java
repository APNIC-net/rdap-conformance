package net.apnic.rdap.conformance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;

/**
 * <p>Result class.</p>
 *
 * Represents a single validation test result (e.g. "got correct
 * status code", "value for field has correct type"). A test result
 * maps to a single line in the output of the validator.
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class Result {
    public enum Status { Success, Notification, Warning, Failure };

    private Status status     = Status.Notification;
    private String path       = "";
    private String testName   = "";
    private String code       = "";
    private String info       = "";
    private String document   = "";
    private String reference  = "";
    private List<String> node = new ArrayList<String>();
    private boolean statusSet = false;

    /**
     * <p>Constructor for Result.</p>
     */
    public Result() { }

    /**
     * <p>Constructor for Result.</p>
     *
     * @param r a {@link net.apnic.rdap.conformance.Result} object.
     */
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

    /**
     * <p>Constructor for Result.</p>
     *
     * @param argStatus a {@link net.apnic.rdap.conformance.Result.Status} object.
     * @param argPath a {@link java.lang.String} object.
     * @param argTestName a {@link java.lang.String} object.
     * @param argCode a {@link java.lang.String} object.
     * @param argInfo a {@link java.lang.String} object.
     * @param argDocument a {@link java.lang.String} object.
     * @param argReference a {@link java.lang.String} object.
     */
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

    /**
     * <p>Getter for the field <code>status</code>.</p>
     *
     * @return a {@link net.apnic.rdap.conformance.Result.Status} object.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * <p>Setter for the field <code>status</code>.</p>
     *
     * @param s a {@link net.apnic.rdap.conformance.Result.Status} object.
     */
    public void setStatus(final Status s) {
        statusSet = true;
        status = s;
    }

    /**
     * <p>Getter for the field <code>statusSet</code>.</p>
     *
     * @return a boolean.
     */
    public boolean getStatusSet() {
        return statusSet;
    }

    /**
     * <p>Getter for the field <code>path</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPath() {
        return path;
    }

    /**
     * <p>Setter for the field <code>path</code>.</p>
     *
     * @param p a {@link java.lang.String} object.
     */
    public void setPath(final String p) {
        path = p;
    }

    /**
     * <p>Getter for the field <code>testName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTestName() {
        return testName;
    }

    /**
     * <p>Setter for the field <code>testName</code>.</p>
     *
     * @param t a {@link java.lang.String} object.
     */
    public void setTestName(final String t) {
        testName = t;
    }

    /**
     * <p>Getter for the field <code>code</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCode() {
        return code;
    }

    /**
     * <p>Setter for the field <code>code</code>.</p>
     *
     * @param c a {@link java.lang.String} object.
     */
    public void setCode(final String c) {
        code = c;
    }

    /**
     * <p>Getter for the field <code>info</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getInfo() {
        return info;
    }

    /**
     * <p>Setter for the field <code>info</code>.</p>
     *
     * @param i a {@link java.lang.String} object.
     */
    public void setInfo(final String i) {
        info = i;
    }

    /**
     * <p>Getter for the field <code>document</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDocument() {
        return document;
    }

    /**
     * <p>Setter for the field <code>document</code>.</p>
     *
     * @param d a {@link java.lang.String} object.
     */
    public void setDocument(final String d) {
        document = d;
    }

    /**
     * <p>Getter for the field <code>reference</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getReference() {
        return reference;
    }

    /**
     * <p>Setter for the field <code>reference</code>.</p>
     *
     * @param r a {@link java.lang.String} object.
     */
    public void setReference(final String r) {
        reference = r;
    }

    /**
     * <p>Getter for the field <code>node</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<String> getNode() {
        return node;
    }

    /**
     * <p>Setter for the field <code>node</code>.</p>
     *
     * @param n a {@link java.util.List} object.
     */
    public void setNode(final List<String> n) {
        node = n;
    }

    /**
     * <p>addNode.</p>
     *
     * @param s a {@link java.lang.String} object.
     */
    public void addNode(final String s) {
        node.add(s);
    }

    /**
     * <p>setStatusAndInfo.</p>
     *
     * @param s a {@link net.apnic.rdap.conformance.Result.Status} object.
     * @param i a {@link java.lang.String} object.
     */
    public void setStatusAndInfo(final Status s, final String i) {
        setStatus(s);
        setInfo(i);
    }

    /**
     * <p>setDetails.</p>
     *
     * @param success a boolean.
     * @param successInfo a {@link java.lang.String} object.
     * @param failureInfo a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean setDetails(final boolean success,
                              final String successInfo,
                              final String failureInfo) {
        return setDetails(success,
                          Status.Success, successInfo,
                          Status.Failure, failureInfo);
    }

    /**
     * <p>setDetails.</p>
     *
     * @param success a boolean.
     * @param successStatus a {@link net.apnic.rdap.conformance.Result.Status} object.
     * @param successInfo a {@link java.lang.String} object.
     * @param failureStatus a {@link net.apnic.rdap.conformance.Result.Status} object.
     * @param failureInfo a {@link java.lang.String} object.
     * @return a boolean.
     */
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

    /**
     * <p>toJson.</p>
     *
     * @return a {@link java.lang.String} object.
     */
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

    /**
     * <p>toString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String toString() {
        StringBuilder epath = new StringBuilder();
        int slen = path.length();
        for (int i = 0; i < slen; i++) {
            char c = path.charAt(i);
            if (Character.isISOControl(c)) {
                epath.append("{" + ((int) c) + "}");
            } else {
                epath.append(c);
            }
        }

        List<String> nodeMapped = new ArrayList<String>();
        for (String n : node) {
            nodeMapped.add(nodeToString(n));
        }

        List<String> elements =
            Arrays.asList(getTestName(),
                          epath.toString(),
                          getStatusAsString(),
                          getCode(),
                          getInfo(),
                          Joiner.on(":").join(nodeMapped),
                          getDocument(),
                          getReference());

        List<String> elementsEscaped = new ArrayList<String>();
        for (String n : elements) {
            elementsEscaped.add(n.replaceAll(",", "{44}"));
        }

        return Joiner.on(",").join(elementsEscaped);
    }
}
