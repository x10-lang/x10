package x10.lang;



import x10.array.DoubleArray;
import x10.array.IntArray;

import x10.runtime.DefaultRuntime_c;
import x10.runtime.Place;

/**
 * This is the central entrypoint to the X10 Runtime for the
 * compiler. There is exactly one Runtime per JVM running X10.
 * 
 * The Runtime is NOT an X10Object! In fact, it cannot be since
 * X10Object's constructor requires already an existing and working
 * Runtime.  Conceptually Runtime is an Interface (!) for the X10
 * world (just like Clock/Region/Distribution/Array/Range/Activity).
 * It is implemented as an abstract class since we need to put some
 * code here, notably the static (!) "_" (or "getRuntime()") 
 * implementation.  Sadly, Java does not allow statics code in
 * interfaces, otherwise this would be an interface. 
 * 
 * @author Christian Grothoff, Christoph von Praun
 * @see Place
 * @see Activity
 */
public abstract class Runtime implements x10.base.Runtime {

    /* 
     * This field is intentionally private, obtain an instance through 
     * method getRuntime();
     */
    public static final Runtime runtime;
    
    /**
     * This instance should be used only in the implementation of 
     * the x10.runtime. 
     */
    public static final JavaRuntime java;
    
    static {
        String rt = System.getProperty("x10.runtime");
        Runtime r = null;
        try {
            if (rt != null)
                r = (Runtime) Class.forName(rt).newInstance();
            else
                r = new DefaultRuntime_c();
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Did not find Runtime " + rt);
            throw new Error(cnfe);
        } catch (IllegalAccessException iae) {
        
        } catch (InstantiationException ie) {
            
        } finally {
            assert (r != null);
            runtime = r;
            java = new JavaRuntime();
            // ArrayFactory.init(r);
        }
    }
    public static final Factory factory = runtime.getFactory();
   
    protected void initialize() {
    
    }
    public static void main(String[] args) {
    	try {
    		runtime.run(args);
    	} catch (Exception e) {
    		Runtime.java.error("Unexpected Exception in X10 Runtime.", e);
    	}
    }

    protected Runtime() {}

  

    public static abstract class Factory {
    	public abstract region.factory getRegionFactory();
    	public abstract distribution.factory getDistributionFactory();
    	public abstract point.factory getPointFactory();
    	public abstract clock.factory getClockFactory();
    	public abstract intArray.factory getIntArrayFactory();
    	public abstract doubleArray.factory getDoubleArrayFactory();
    	public abstract place.factory getPlaceFactory();
    }

    public abstract Factory getFactory();

    /**
     * Run the X10 application.
     */
    protected abstract void run(String[] args) throws Exception;

    /**
     * @return The place where the current execution takes place 
     * ('here' in X10).
     */
    public static Place here() {
    	return runtime.currentPlace();
    }
    
    public static Place[] places() {
        Place[] pl = runtime.getPlaces();
        Place[] ret = new Place[pl.length];
        System.arraycopy(pl, 0, ret, 0, pl.length);
        return ret;
    }
} // end of Runtime
