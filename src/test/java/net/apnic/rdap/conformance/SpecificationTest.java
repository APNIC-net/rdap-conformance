package net.apnic.rdap.conformance;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import net.apnic.rdap.conformance.Specification;
import java.io.*;

public class SpecificationTest
{
    public SpecificationTest()
    {
    }

    @Test
    public void testBasic() throws Exception
    {
        Specification s = Specification.fromString("{base_url: 'asdf'}");
        assertEquals(s.getBaseUrl(), "asdf");

        File tempFile = File.createTempFile("temp-file", ".tmp");
        tempFile.deleteOnExit();
        OutputStream os = new FileOutputStream(tempFile);
        String json_text = "{base_url: 'asdf2'}";
        os.write(json_text.getBytes());

        Specification s2 = 
            Specification.fromPath(tempFile.getAbsolutePath());
        assertEquals(s2.getBaseUrl(), "asdf2");

        assertTrue(true);
    }
}
