package x10.lang;

import x10.array.Distribution;
import x10.array.DoubleArray;
import x10.array.IntArray;
import x10.array.Range;
import x10.array.Region;
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
public abstract class Runtime implements x10.base.Runtime {

    /* 
     * This field is intentionally private, obtain an instance through 
     * method getRuntime();
     */
    private static final Runtime _;
    
    public static Runtime getRuntime() {
        return _;
    }
    
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
            System.exit(-1);
        } catch (IllegalAccessException iae) {
        
        } catch (InstantiationException ie) {
            
        } finally {
            assert (r != null);
            _ = r;
            java = new JavaRuntime();
        }
    }

    public static void main(String[] args) {
    	try {
    		_.run(args);
    	} catch (Exception e) {
    		Runtime.java.error("Unexpected Exception in X10 Runtime.", e);
    	}
    }

    protected Runtime() {}

    /**
     * Run the X10 application.
     */
    protected abstract void run(String[] args) throws Exception;

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
     * @return  New array with Distribution d.
     */
    public abstract IntArray newIntArray(Distribution d);
    
    /**
     * @return  New array with Distribution d.
     */
    public abstract DoubleArray newDoubleArray(Distribution d);

    /**
     * @return The place where the current execution takes place 
     * ('here' in X10).
     */
    public static Place here() {
    	return (x10.lang.Place) _.currentPlace();
    }
    
    public static Place[] places() {
        x10.base.Place[] pl = _.getPlaces();
        Place[] ret = new Place[pl.length];
        System.arraycopy(pl, 0, ret, 0, pl.length);
        return ret;
    }
    
    // Distribution factory methods...
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over all available Places.
     * @param r
     * @return
     */
    public abstract Distribution newBlockDistribution(Region r, Place[] p);
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks of size n over all available Places.
     * @param r
     * @return
     */
    public abstract Distribution newBlockDistribution(Region r, int n, Place[] p);
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public abstract Distribution newCyclicDistribution(Region r, Place[] p);
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public abstract Distribution newBlockCyclicDistribution(Region r, int n, Place[] p);
    
    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    public abstract Distribution newArbitraryDistribution(Region r, Place[] p);
    
    /**
     * Create a Distribution where all points in the given
     * Region are mapped to the same Place.
     * @param r
     * @param p specifically use the given place for all points
     * @return
     */
    public abstract Distribution newConstantDistribution(Region r, Place p);
    
    /**
     * Create a Distribution where the points in the
     * region 1...p.length are mapped to the respective
     * places.
     * @param p the list of places (implicitly defines the region)
     * @return
     */
    public abstract Distribution newUniqueDistribution(Place[] p);
    
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over all available Places.
     * @param r
     * @return
     */
    public abstract Distribution newBlockDistribution(Region r);
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks of size n over all available Places.
     * @param r
     * @return
     */
    public abstract Distribution newBlockDistribution(Region r, int n);
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public abstract Distribution newCyclicDistribution(Region r);
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public abstract Distribution newBlockCyclicDistribution(Region r, int n);
    
    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    public abstract Distribution newArbitraryDistribution(Region r);
    
    /**
     * Create a Distribution where the points in the
     * region 1...p.length are mapped to the respective
     * places.
     * @param p the list of places (implicitly defines the region)
     * @return
     */
    public abstract Distribution newUniqueDistribution();
    
    
} // end of Runtime