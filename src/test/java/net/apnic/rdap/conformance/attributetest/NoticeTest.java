package net.apnic.rdap.conformance.attributetest;

import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.attributetest.Notice;

import java.util.HashMap;
import java.util.Map;
import com.google.common.collect.ImmutableMap;

public class NoticeTest {
    public NoticeTest() {
    }

    @Test
    public void testType() throws Exception {
        Context context = new Context();
        Result proto = new Result();
        Map<String, Object> data = ImmutableMap.of(
            "title", (Object) "truncated",
            "type", "object truncated due to authorization"
        );
        Notice notice = new Notice();
        boolean res = notice.run(context, proto, data);
        if (!res) {
            context.flushResults();
        }
        assertTrue(res, "Notice test completed successfully");
    }
}
