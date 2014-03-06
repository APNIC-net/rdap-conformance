package net.apnic.rdap.conformance.test.ip;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;
import org.apache.http.conn.util.InetAddressUtils;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Utils;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.ContentTest;
import net.apnic.rdap.conformance.contenttest.StandardResponse;

public class Standard implements net.apnic.rdap.conformance.Test
{
    String ip = "";

    public Standard(String arg_ip)
    {
        ip = arg_ip;
    }

    private String processIpAddress(Context context, Result proto, 
                                    Map root, String key)
    {
        Result ipres = new Result(proto);
        String address = (String) root.get(key);
        if (address == null) {
            ipres.setStatus(Status.Failure);
            ipres.setInfo(key + " element not found");
            context.addResult(ipres);
        } else { 
            ipres.setStatus(Status.Success);
            ipres.setInfo(key + " element found");
            context.addResult(ipres);
            
            Result ipvalid = new Result(proto);
            ipvalid.setStatus(Status.Success);
            ipvalid.setInfo(key + " element is valid");
            if (!(InetAddressUtils.isIPv4Address(address)
                    || InetAddressUtils.isIPv6Address(address))) {
                ipvalid.setStatus(Status.Failure);
                ipvalid.setInfo(key + " element is not valid");
                context.addResult(ipvalid);
            } else {
                ipvalid.setStatus(Status.Success);
                ipvalid.setInfo(key + " element is valid");
                context.addResult(ipvalid);
            }
        }
        return address;
    }

    public boolean run(Context context)
    {
        List<Result> results = context.getResults();

        String bu = context.getSpecification().getBaseUrl();
        String path = bu + "/ip/" + ip;

        Result proto = new Result(Status.Notification, path,
                                  "ip.standard",
                                  "", "", "", "");
        Result r = new Result(proto);
        r.setCode("response");
        Map root = Utils.standardRequest(context, path, r);
        if (root == null) {
            return false;
        }

        String start_address = 
            processIpAddress(context, proto, root, "startAddress");
        String end_address =
            processIpAddress(context, proto, root, "endAddress");
        if ((start_address != null) && (end_address != null)) {
            Result types_match = new Result(proto);
            types_match.setInfo("start and end address types match");
            types_match.setStatus(Status.Success);
            if (InetAddressUtils.isIPv4Address(start_address)
                    && InetAddressUtils.isIPv4Address(end_address)) {
                context.addResult(types_match);
            } else {
                types_match.setInfo(
                    "start and end address types do not match"
                );
                types_match.setStatus(Status.Failure);
                context.addResult(types_match);
            }
        }

        ContentTest srt = new StandardResponse();
        return srt.run(context, proto, root);
    }
}
