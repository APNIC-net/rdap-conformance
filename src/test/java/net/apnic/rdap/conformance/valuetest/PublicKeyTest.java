package net.apnic.rdap.conformance.valuetest;

import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.valuetest.PublicKey;
import java.util.List;

public class PublicKeyTest {
    public PublicKeyTest() {
    }

    @Test
    public void testBasic() throws Exception {
        Context context = new Context();
        PublicKey publicKeyTest = new PublicKey();
        Result proto = new Result();
        publicKeyTest.run(context, proto, "AQPJ////4Q==");
        List<Result> results = context.getResults();
        Result finalResult = results.get(results.size() - 1);
        assertTrue(finalResult.getStatus().equals(Result.Status.Success),
                   "Public key test accepts base64-encoded data");
    }
}
