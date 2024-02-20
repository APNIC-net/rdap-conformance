package net.apnic.rdap.conformance;

import java.util.ArrayList;

/**
 * Represents a list of Context objects.
 *
 * Class is a wrapper around {@link java.util.ArrayList} that provides
 * convenience functions for getting information about a list of Context objects.
 *
 * Note: This class does not implement any ArrayList constructors as they are
 * not needed at present.
 */
public class ContextList
    extends ArrayList<Context>
{
    /**
     * Indiciates if at least one Context object in this list has a failed
     * result.
     *
     * @return boolean if at least one Context has a failed result.
     */
    public boolean hasFailedResult() {
        for (Context context : this) {
            if (context.hasFailedResult()) {
                return true;
            }
        }
        return false;
    }
}
