package net.apnic.rdap.conformance.valuetest;

import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.valuetest.Date;
import java.util.List;

public class DateTest {
    public DateTest() {
    }

    @Test
    public void testBasic() throws Exception {
        Context context = new Context();
        Date dateTest = new Date();
        Result proto = new Result();
        boolean noException = true;
        try {
            dateTest.run(context, proto, "2000-01-01T00:00:61Z");
        } catch (Exception e) {
            System.err.println(e.toString());
            noException = false;
        }
        assertTrue(noException, "No exception thrown on invalid date");
        List<Result> results = context.getResults();
        Result finalResult = results.get(results.size() - 1);
        assertTrue(finalResult.getStatus().equals(Result.Status.Failure),
                   "Last context result is a failure");

        context = new Context();
        noException = true;
        try {
            dateTest.run(context, proto, "1991-12-31T23:59:59Z");
        } catch (Exception e) {
            System.err.println(e.toString());
            noException = false;
        }
        assertTrue(noException, "No exception thrown on valid date");
        results = context.getResults();
        finalResult = results.get(results.size() - 1);
        assertTrue(finalResult.getStatus().equals(Result.Status.Success),
                   "Last context result is a success");
    }
}
