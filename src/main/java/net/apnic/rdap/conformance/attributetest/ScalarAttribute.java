package net.apnic.rdap.conformance.attributetest;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.ValueTest;
import net.apnic.rdap.conformance.Utils;

public class ScalarAttribute implements AttributeTest
{
    private String attribute_name = null;
    private ValueTest value_test = null;
    private AttributeTest attribute_test = null;

    public ScalarAttribute(String arg_attribute_name)
    {
        attribute_name = arg_attribute_name;
    }

    public ScalarAttribute(String arg_attribute_name,
                           AttributeTest arg_attribute_test)
    {
        attribute_name = arg_attribute_name;
        attribute_test = arg_attribute_test;
    }

    public ScalarAttribute(String arg_attribute_name,
                           ValueTest arg_value_test)
    {
        attribute_name = arg_attribute_name;
        value_test = arg_value_test;
    }

    public boolean run(Context context, Result proto,
                       Map<String, Object> data)
    {
        Result nr = new Result(proto);
        nr.setCode("content");
        nr.setInfo("present");

        Object value = Utils.getAttribute(context, nr, attribute_name,
                                          Status.Notification, data);
        boolean res = (value != null);

        if (value_test != null) {
            return (res && value_test.run(context, nr, value));
        } else if (attribute_test != null) {
            Map<String, Object> subdata =
                Utils.castToMap(context, nr, value);
            if (subdata == null) {
                return false;
            } else {
                return (res && attribute_test.run(context, nr, subdata));
            }
        }

        return res;
    }

    public Set<String> getKnownAttributes()
    {
        return Sets.newHashSet(attribute_name);
    }
}
