package net.apnic.rdap.conformance.attributetest;

import org.testng.annotations.Test;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import net.apnic.rdap.conformance.Context;
import net.apnic.rdap.conformance.Result;
import net.apnic.rdap.conformance.Result.Status;
import net.apnic.rdap.conformance.attributetest.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;

public class EntityTest {
    public EntityTest() {
    }

    private void testResultPresence(Map<String, Object> data,
                                    Status status,
                                    String info,
                                    String assertionMessage) throws Exception {
        Context context = new Context();
        Result proto = new Result();

        Entity entity = new Entity();
        entity.run(context, proto, data);
        List<Result> results = context.getResults();
        boolean foundResult = false;
        for (Result r : results) {
            if (r.getStatus() == status) {
                String resultInfo = r.getInfo();
                if (resultInfo.matches(info)) {
                    foundResult = true;
                }
            }
        }
        if (!foundResult) {
            System.err.println("Results:");
            for (Result r : results) {
                System.err.println(r.toString());
            }
        }
        assertTrue(foundResult, assertionMessage);
    }

    private void testSuccess(Map<String, Object> data,
                             String assertionMessage) throws Exception {
        Context context = new Context();
        Result proto = new Result();

        Entity entity = new Entity();
        entity.run(context, proto, data);
        List<Result> results = context.getResults();
        boolean foundFailure = false;
        for (Result r : results) {
            if (r.getStatus() == Status.Failure) {
                foundFailure = true;
            }
        }
        if (foundFailure) {
            System.err.println("Results:");
            for (Result r : results) {
                System.err.println(r.toString());
            }
        }
        assertFalse(foundFailure, assertionMessage);
    }

    @Test
    public void testVCardValidationParameterElement() throws Exception {
        Map<String, Object> data = ImmutableMap.of(
            "handle",
            (Object) "ENT-01",
            "objectClassName",
            "entity",
            "vcardArray",
            ImmutableList.of(
                "vcard",
                ImmutableList.of(
                    ImmutableList.of(
                        "version",
                        "",
                        "text",
                        "4.0"
                    )
                )
            )
        );
        testResultPresence(
            data,
            Status.Failure,
            "unable to parse vcard.*START_OBJECT.*VALUE_STRING.*",
            "VCard parsing fails on invalid parameter element"
        );
    }

    @Test
    public void testVCardValidationNoVersion() throws Exception {
        Map<String, Object> data = ImmutableMap.of(
            "handle",
            (Object) "ENT-01",
            "objectClassName",
            "entity",
            "vcardArray",
            ImmutableList.of(
                "vcard",
                ImmutableList.of()
            )
        );
        testResultPresence(
            data,
            Status.Failure,
            ".*No \"version\" property found.*",
            "VCard parsing fails when version not present"
        );
    }

    @Test
    public void testVCardValidationNoFormattedName() throws Exception {
        Map<String, Object> data = ImmutableMap.of(
            "handle",
            (Object) "ENT-01",
            "objectClassName",
            "entity",
            "vcardArray",
            ImmutableList.of(
                "vcard",
                ImmutableList.of(
                    ImmutableList.of(
                        "version",
                        ImmutableMap.of(),
                        "text",
                        "4.0"
                    )
                )
            )
        );
        testResultPresence(
            data,
            Status.Failure,
            ".*A FormattedName property is required.*",
            "VCard parsing fails when formatted name not present"
        );
    }

    @Test
    public void testVCardValidationBasic() throws Exception {
        Map<String, Object> data = ImmutableMap.of(
            "handle",
            (Object) "ENT-01",
            "objectClassName",
            "entity",
            "vcardArray",
            ImmutableList.of(
                "vcard",
                ImmutableList.of(
                    ImmutableList.of(
                        "version",
                        ImmutableMap.of(),
                        "text",
                        "4.0"
                    ),
                    ImmutableList.of(
                        "fn",
                        ImmutableMap.of(),
                        "text",
                        "John Smith"
                    )
                )
            )
        );
        testSuccess(
            data,
            "VCard parsing succeeds when FN is present"
        );
    }

    @Test
    public void testVCardValidationEmailAddress() throws Exception {
        Map<String, Object> data = ImmutableMap.of(
            "handle",
            (Object) "ENT-01",
            "objectClassName",
            "entity",
            "vcardArray",
            ImmutableList.of(
                "vcard",
                ImmutableList.of(
                    ImmutableList.of(
                        "version",
                        ImmutableMap.of(),
                        "text",
                        "4.0"
                    ),
                    ImmutableList.of(
                        "fn",
                        ImmutableMap.of(),
                        "text",
                        "John Smith"
                    ),
                    ImmutableList.of(
                        "email",
                        ImmutableMap.of(),
                        "text",
                        "durp"
                    )
                )
            )
        );
        testResultPresence(
            data,
            Status.Failure,
            ".*email address is invalid.*",
            "VCard parsing fails on invalid email address"
        );
    }

    @Test
    public void testVCardValidationAddress() throws Exception {
        Map<String, Object> data = ImmutableMap.of(
            "handle",
            (Object) "ENT-01",
            "objectClassName",
            "entity",
            "vcardArray",
            ImmutableList.of(
                "vcard",
                ImmutableList.of(
                    ImmutableList.of(
                        "version",
                        ImmutableMap.of(),
                        "text",
                        "4.0"
                    ),
                    ImmutableList.of(
                        "fn",
                        ImmutableMap.of(),
                        "text",
                        "John Smith"
                    ),
                    ImmutableList.of(
                        "adr",
                        ImmutableMap.of(),
                        "text",
                        ImmutableList.of(
                            "asdf",
                            "asdf",
                            "",
                            "",
                            "",
                            "",
                            ""
                        )
                    )
                )
            )
        );
        testResultPresence(
            data,
            Status.Warning,
            ".*PO box should not be set.*",
            "VCard parsing warns when PO box is set"
        );
        testResultPresence(
            data,
            Status.Warning,
            ".*extended address should not be set.*",
            "VCard parsing warns when extended address is set"
        );
    }

    @Test
    public void testVCardValidationExtendedProperties() throws Exception {
        Map<String, Object> data = ImmutableMap.of(
            "handle",
            (Object) "ENT-01",
            "objectClassName",
            "entity",
            "vcardArray",
            ImmutableList.of(
                "vcard",
                ImmutableList.of(
                    ImmutableList.of(
                        "version",
                        ImmutableMap.of(),
                        "text",
                        "4.0"
                    ),
                    ImmutableList.of(
                        "fn",
                        ImmutableMap.of(),
                        "text",
                        "John Smith"
                    ),
                    ImmutableList.of(
                        "non-standard",
                        ImmutableMap.of(),
                        "text",
                        "value"
                    )
                )
            )
        );
        testResultPresence(
            data,
            Status.Warning,
            ".*found non-standard property 'non-standard'.*",
            "VCard parsing warns on non-standard property"
        );
    }

    @Test
    public void testVCardValidationKind() throws Exception {
        Map<String, Object> data = ImmutableMap.of(
            "handle",
            (Object) "ENT-01",
            "objectClassName",
            "entity",
            "vcardArray",
            ImmutableList.of(
                "vcard",
                ImmutableList.of(
                    ImmutableList.of(
                        "version",
                        ImmutableMap.of(),
                        "text",
                        "4.0"
                    ),
                    ImmutableList.of(
                        "kind",
                        ImmutableMap.of(),
                        "text",
                        "non-standard"
                    )
                )
            )
        );
        testResultPresence(
            data,
            Status.Warning,
            ".*found non-standard kind 'non-standard'.*",
            "VCard parsing warns on non-standard kind"
        );
    }
}
