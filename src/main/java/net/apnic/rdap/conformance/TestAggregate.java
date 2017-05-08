package net.apnic.rdap.conformance;

import java.util.ArrayList;

public class TestAggregate
    extends ArrayList<Test>
{
    TestAggregate() {
        super();
    }

    public boolean hasFailure()
    {
        for(Test test : this) {
            if(test.hasFailed()) {
                return true;
            }
        }
        return false;
    }
}
