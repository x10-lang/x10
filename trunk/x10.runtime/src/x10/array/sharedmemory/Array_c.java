/*
 * Created on Oct 16, 2004
 */
package x10.array.sharedmemory;

import x10.array.Array;
import x10.array.Distribution;


/**
 * @author praun
 * @author Christian Grothoff
 */
public abstract class Array_c implements Array {

    protected final Distribution d;
    
    protected Array_c(Distribution d) {
        this.d = d;
    }
	
    /* (non-Javadoc)
     * @see x10.lang.Array#getDistribution()
     */
    public final Distribution getDistribution() {
        return d;
    }

} // end of Array_c
