package net.apnic.rdap.conformance.attributetest;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.text.Normalizer;

import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.SearchTest;
import net.apnic.rdap.conformance.Utils;

import com.google.common.collect.Sets;
import com.google.common.base.CharMatcher;
import com.vgrs.xcode.idna.Idna;
import com.vgrs.xcode.idna.Punycode;
import com.vgrs.xcode.util.XcodeException;

/**
 * <p>DomainNames class.</p>
 *
 * @author Tom Harrison <tomh@apnic.net>
 * @version 0.3-SNAPSHOT
 */
public final class DomainNames implements SearchTest {
    private static final int DOMAIN_LABEL_MAX_LENGTH = 63;
    private String pattern = null;

    /**
     * <p>Constructor for DomainNames.</p>
     */
    public DomainNames() { }

    /** {@inheritDoc} */
    public void setSearchDetails(final String argKey,
                                 final String argPattern) {
        if (argKey.equals("name")) {
            pattern = argPattern;
        }
    }

    private int isValidLdhName(final String ldhName) {
        String[] labels = ldhName.split("\\.");
        Pattern ldhPattern = Pattern.compile(
            "[\\p{Alnum}][\\p{Alnum}-]*[\\p{Alnum}]?"
        );
        boolean ldhres = true;
        boolean aLabelFound = false;
        for (String label : labels) {
            boolean labelres = ldhPattern.matcher(label).matches();
            if (!labelres) {
                ldhres = false;
            }
            if (label.length() > DOMAIN_LABEL_MAX_LENGTH) {
                ldhres = false;
            }
            if (label.startsWith("xn--")) {
                aLabelFound = true;
            }
        }

        return (ldhres ? (aLabelFound ? 2 : 1) : 0);
    }

    /** {@inheritDoc} */
    public boolean run(final Context context, final Result proto,
                       final Map<String, Object> data) {
        Result nr = new Result(proto);
        /* This would refer to a specific object class, but ldhName
         * and unicodeName appear in both nameservers and domains, and
         * section 4 is likely to be the most helpful part of the
         * document in the event of failure anyway. */
        nr.setCode("content");
        nr.setDocument("draft-ietf-weirds-json-response-09");
        nr.setReference("4");

        boolean res = true;
        String ldhName = Utils.getStringAttribute(context,
                                                   nr, "ldhName",
                                                   Status.Failure,
                                                   data);
        if (ldhName == null) {
            return false;
        }
        int ldhresBoth = isValidLdhName(ldhName);
        boolean ldhres = (ldhresBoth >= 1);
        boolean aLabelFound = (ldhresBoth == 2);

        Result dn = new Result(nr);
        dn.addNode("ldhName");
        res = dn.setDetails(ldhres, "valid", "invalid");
        context.addResult(dn);

        if (!aLabelFound && (pattern != null)) {
            Result rp = new Result(nr);
            rp.addNode("ldhName");
            String ldhPattern = pattern.replaceAll("\\*", ".*");
            ldhPattern = ".*" + ldhPattern + ".*";
            Pattern p = Pattern.compile(ldhPattern,
                                        Pattern.CASE_INSENSITIVE);
            rp.setDetails(p.matcher(ldhName).matches(),
                          Status.Success,
                          "response domain name matches search pattern",
                          Status.Warning,
                          "response domain name does not match search pattern");
            context.addResult(rp);
        }

        Object unicodeNameObj = data.get("unicodeName");
        if (unicodeNameObj == null) {
            if (aLabelFound) {
                Result nou = new Result(nr);
                nou.addNode("unicodeName");
                nou.setStatus(Status.Warning);
                nou.setInfo("not present and ldhName contains A-label");
                context.addResult(nou);
            }
            return res;
        }

        String unicodeName = Utils.getStringAttribute(context,
                                                       nr, "unicodeName",
                                                       Status.Failure,
                                                       data);
        boolean isAscii =
            CharMatcher.ASCII.matchesAllOf(unicodeName);
        Result hu = new Result(nr);
        hu.addNode("unicodeName");
        /* There are at least a couple of implementations that return
         * ldhName in unicodeName when ldhName contains no A-labels.
         * This is not (currently) compliant: see section 4 of the
         * draft, as well as RFC 5890 [2.3.2.1], which requires that
         * at least one U-label be present. */
        hu.setDetails((!isAscii),
                      "non-ascii characters present",
                      "no non-ascii characters present");
        context.addResult(hu);
        if (isAscii) {
            return false;
        }

        Idna idna = null;
        try {
            idna = new Idna(new Punycode(), true, true);
        } catch (XcodeException xe) {
            System.err.println("Unable to initialise/use IDNA processor "
                               + xe.toString());
            return res;
        }

        int[] unicodeNums = new int[unicodeName.length()];
        char[] unicodeChars = unicodeName.toCharArray();
        for (int i = 0; i < unicodeName.length(); i++) {
            unicodeNums[i] = unicodeChars[i];
        }
        String ldhNameCheck = null;
        String error = null;
        try {
            ldhNameCheck = new String(idna.domainToAscii(unicodeNums));
        } catch (XcodeException ce) {
            error = ce.toString();
        }
        Result iv = new Result(nr);
        iv.addNode("unicodeName");
        iv.setDetails((ldhNameCheck != null), "valid", "invalid: " + error);
        context.addResult(iv);

        if (ldhNameCheck != null) {
            Result ms = new Result(nr);
            ms.addNode("unicodeName");
            String ldhNameCanon = ldhName.toLowerCase();
            if (ldhNameCanon.charAt(ldhNameCanon.length() - 1) != '.') {
                ldhNameCanon += ".";
            }
            ms.setDetails((ldhNameCheck.equals(ldhNameCanon)),
                          "matches ldhName", "does not match ldhName");
            context.addResult(ms);
        }

        if (pattern != null) {
            Result rp = new Result(nr);
            rp.addNode("unicodeName");
            String unPattern = pattern.replaceAll("\\*", ".*");
            unPattern = ".*" + unPattern + ".*";
            unPattern =
                Normalizer.normalize(unPattern,
                                     Normalizer.Form.NFKC);
            Pattern p = Pattern.compile(unPattern,
                                        Pattern.CASE_INSENSITIVE
                                      | Pattern.UNICODE_CASE);
            if (!p.matcher(unicodeName).matches()) {
                rp.setStatus(Status.Warning);
                rp.setInfo("response domain name does not "
                           + "match search pattern");
            } else {
                rp.setStatus(Status.Success);
                rp.setInfo("response domain name matches "
                           + "search pattern");
            }
            context.addResult(rp);
        }

        return res;
    }

    /**
     * <p>getKnownAttributes.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getKnownAttributes() {
        return Sets.newHashSet("ldhName", "unicodeName");
    }
}
