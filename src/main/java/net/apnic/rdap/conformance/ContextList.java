package net.apnic.rdap.conformance;

import java.util.ArrayList;

public class ContextList
    extends ArrayList<Context>
{
    public boolean hasFailedResult()
    {
        for(Context context : this)
        {
            if(context.hasFailedResult()) {
                return true;
            }
        }
        return false;
    }
}
