/*
 * Created on Nov 16, 2004
 */
package x10.array;

import x10.array.sharedmemory.Runtime_c;
import x10.base.Runtime;

/**
 * @author Christoph von Praun
 */
public abstract class ArrayRuntime implements Runtime {
    
//  depending on the system environment, this might change
    private static final Runtime _ = new Runtime_c(1);
    
    public static Place here() {
    	return _.currentPlace();
    }
    
    public static Place[] places() {
        return _.getPlaces();
    }
    
    public static Runtime getRuntime() {
        return _;
    }

}
