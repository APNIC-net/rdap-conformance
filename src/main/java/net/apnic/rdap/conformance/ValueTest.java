package net.apnic.rdap.conformance;

public interface ValueTest {
    boolean run(Context context, Result proto, Object content);
}
