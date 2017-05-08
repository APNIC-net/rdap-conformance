package net.apnic.rdap.conformance;

import java.util.ArrayList;

/**
 * Container class for holding a list of Test objects.
 *
 * This container is a child of java.util.ArrayList<Test> and provides helper
 * methods on to the list of Test objects.
 *
 * Note: Class does not implement any java.util.ArrayList<Test> constructors.
 * These can be added as needed.
 */
public class TestAggregate
    extends ArrayList<Test>
{
    /**
     * Indicates if the at least one test in this aggregate has failed.
     *
     * @return boolean if at least one test in the aggregate has failed.
     */
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
