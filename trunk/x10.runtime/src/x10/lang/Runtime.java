package x10.lang;

import java.util.Set;

import x10.runtime.DefaultRuntime_c;

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
public abstract class Runtime {

    /**
     * This instance should be used only in the implementation of 
     * the x10.runtime. 
     */
    public static final Runtime _;
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
            System.exit(-1);
        } catch (IllegalAccessException iae) {
        
        } catch (InstantiationException ie) {
            
        } finally {
            _ = r;
            java = new JavaRuntime();
        }
    }

    public static void main(String[] args) throws Exception {
	_.run(args);
    }

    protected Runtime() {}

    /**
     * Run the X10 application.
     */
    protected abstract void run(String[] args) throws Exception;


    /**
     * Shutdown the X10 runtime system.
     */
    public abstract void shutdown();

    /**
     * @return New Clock.
     */
    public abstract Clock newClock();

    /**
     * @return New Range.
     */
    public abstract Range newRange(int lo, int hi);
    
    /**
     * @return New Region.
     */
    public abstract Region newRegion(Range[] dims);
    	
    /**
     * @param p Set of places.
     * @return Unique, one-dimensional Distribution that maps
     *         one point to each place.
     */
    public abstract Distribution newUniqueDist(Set p);
	
	public abstract Distribution newConstantDist(Place p);
	
	public abstract Distribution newBlockDist(Region r, Set p);

	public abstract Distribution newCyclicDist(Region r, Set p); 
	
	public abstract Distribution newBlockCyclicDist(Region r, Set p, int bsize); 
		
    /**
     * @return  New array with Distribution d.
     */
    public abstract Array newArray(Distribution d);

    /**
     * @return The place where the current execution takes place 
     * ('here' in X10).
     */
    public static Place here() {
    	return _.currentPlace();
    }
    
    public abstract Place currentPlace();
    
} // end of Runtime