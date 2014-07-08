package net.apnic.rdap.conformance.test.entity;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.List;

import com.google.common.collect.Sets;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.AttributeTest;
import net.apnic.rdap.conformance.attributetest.Entity;
import net.apnic.rdap.conformance.attributetest.RdapConformance;
import net.apnic.rdap.conformance.attributetest.Notices;
import net.apnic.rdap.conformance.attributetest.UnknownAttributes;

public class Standard implements net.apnic.rdap.conformance.Test
{
    String entity = "";

    public Standard(String arg_entity)
    {
        entity = arg_entity;
    }

    public boolean run(Context context)
    {
        String bu = context.getSpecification().getBaseUrl();
        String path = bu + "/entity/" + entity;

        Result proto = new Result(Status.Notification, path,
                                  "entity.standard",
                                  "content", "",
                                  "draft-ietf-weirds-json-response-07",
                                  "6.1");
        Result r = new Result(proto);
        r.setCode("response");
        Map root = Utils.standardRequest(context, path, r);
        if (root == null) {
            return false;
        }

        Map<String, Object> data = Utils.castToMap(context, proto, root);
        if (data == null) {
            return false;
        }

        Set<String> known_attributes = new HashSet<String>();
        return Utils.runTestList(
            context, proto, root, known_attributes, true,
            Arrays.asList(
                new Entity(entity, false),
                new RdapConformance(),
                new Notices()
            )
        );
    }
}
