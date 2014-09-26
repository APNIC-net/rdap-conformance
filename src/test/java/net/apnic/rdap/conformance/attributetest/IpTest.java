package net.apnic.rdap.conformance.attributetest;

import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.attributetest.Ip;

import java.util.HashMap;
import java.util.Map;
import com.google.common.collect.ImmutableMap;

public class IpTest {
    public IpTest() {
    }

    @Test
    public void testParentHandleMatch() throws Exception {
        Context context = new Context();
        Result proto = new Result();
        Map<String, Object> data = ImmutableMap.of(
            "parentHandle", (Object) "my-parent-handle",
            "startAddress", "1.0.0.0",
            "endAddress", "1.0.0.255"
        );
        Ip ip = new Ip();
        boolean noException = true;
        try {
            ip.run(context, proto, data);
        } catch (Exception e) {
            System.err.println(e.toString());
            noException = false;
        }
        assertTrue(noException, "No exception thrown on link absence");
    }
}
