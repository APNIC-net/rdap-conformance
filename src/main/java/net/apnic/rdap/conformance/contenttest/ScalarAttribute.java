package net.apnic.rdap.conformance.contenttest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import java.math.BigInteger;
import java.math.BigDecimal;
import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.Utils;

import java.util.Set;

import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;

public class ScalarAttribute implements ContentTest
{
    private String attribute_name = null;
    private ContentTest attribute_test = null;

    public ScalarAttribute(String arg_attribute_name)
    {
        attribute_name = arg_attribute_name;
    }

    public ScalarAttribute(String arg_attribute_name,
                           ContentTest arg_attribute_test)
    {
        attribute_name = arg_attribute_name;
        attribute_test = arg_attribute_test;
    }

    public boolean run(Context context, Result proto,
                       Object arg_data)
    {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setInfo("present");

        String ucattr_name =
            Character.toUpperCase(attribute_name.charAt(0)) +
            attribute_name.substring(1);

        Map<String, Object> data = Utils.castToMap(context, nr, arg_data);
        if (data == null) {
            return false;
        }

        Object value = Utils.getAttribute(context, nr, attribute_name,
                                          Status.Notification, data);
        boolean res = (value != null);

        if (attribute_test != null) {
            return (res && attribute_test.run(context, nr, value));
        }

        return res;
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet(attribute_name);
    }
}
