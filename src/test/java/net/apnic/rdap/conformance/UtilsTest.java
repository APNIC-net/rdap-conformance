package net.apnic.rdap.conformance;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNull;

import net.apnic.rdap.conformance.Utils;
import java.io.*;

public class UtilsTest
{
    public UtilsTest()
    {
    }

    @Test
    public void testIpv4ArpaToPrefix() throws Exception
    {
        String s;

        s = Utils.arpaToPrefix("in-addr.arpa");
        assertEquals(s, "",
            "Got empty string for invalid domain");

        s = Utils.arpaToPrefix("1.in-addr.arpa");
        assertEquals(s, "1.0.0.0/8",
            "Got correct prefix for a /8");

        s = Utils.arpaToPrefix("123.123.in-addr.arpa");
        assertEquals(s, "123.123.0.0/16",
            "Got correct prefix for a /16");

        s = Utils.arpaToPrefix("1.2.3.in-addr.arpa");
        assertEquals(s, "3.2.1.0/24",
            "Got correct prefix for a /24");

        s = Utils.arpaToPrefix("4.3.2.1.in-addr.arpa");
        assertEquals(s, "1.2.3.4/32",
            "Got correct prefix for a /32");

        s = Utils.arpaToPrefix("5.4.3.2.1.in-addr.arpa");
        assertEquals(s, "",
            "Got empty string for invalid domain (too many segments)");

        s = Utils.arpaToPrefix("a.b.c.in-addr.arpa");
        assertEquals(s, "",
            "Got empty string for invalid domain (contains letters)");
    }

    @Test
    public void testIpv6ArpaToPrefix() throws Exception
    {
        String s;

        s = Utils.arpaToPrefix("ip6.arpa");
        assertEquals(s, "",
            "Got empty string for invalid domain");

        s = Utils.arpaToPrefix("a.ip6.arpa");
        assertEquals(s, "a000::/4",
            "Got correct prefix for a /4");

        s = Utils.arpaToPrefix("a.b.c.d.a.b.c.d.a.b.c.d.a.b.c.d.a.b.c.d.a.b.c.d.a.b.c.d.a.b.c.d.ip6.arpa");
        assertEquals(s, "dcba:dcba:dcba:dcba:dcba:dcba:dcba:dcba/128",
            "Got correct prefix for a /128");

        s = Utils.arpaToPrefix("a.b.c.d.a.b.c.d.a.b.c.d.a.b.c.d.a.b.c.d.a.b.c.d.a.b.c.d.a.b.c.ip6.arpa");
        assertEquals(s, "cbad:cbad:cbad:cbad:cbad:cbad:cbad:cba0/124",
            "Got correct prefix for a /124");
    }
}
