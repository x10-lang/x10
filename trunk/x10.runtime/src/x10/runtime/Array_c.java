/*
 * Created on Oct 16, 2004
 */
package x10.runtime;

import x10.lang.Array;
import x10.lang.Distribution;

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
