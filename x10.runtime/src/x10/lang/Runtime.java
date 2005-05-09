package x10.lang;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import x10.runtime.ActivityInformation;
import x10.runtime.Activity;
import x10.runtime.Configuration;
import x10.runtime.DefaultRuntime_c;
import x10.runtime.Place;
import x10.runtime.Clock;

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

    /* 
     * This field is intentionally private, obtain an instance through 
     * method getRuntime();
     */
    public static Runtime runtime;
    
    private static int returnValue;
    
    /**
     * This instance should be used only in the implementation of 
     * the x10.runtime. 
     */
    public static JavaRuntime java;
    
    private static boolean done_;
    public static void init() {
        assert !done_;
        done_ = true;
        String rt = System.getProperty("x10.runtime");
        Runtime r = null;
        try {
            if (rt != null)
                r = (Runtime) Class.forName(rt).newInstance();
            else
                r = new DefaultRuntime_c();
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Runtime::<clinit> did not find runtime " + rt);
            throw new Error(cnfe);
        } catch (IllegalAccessException iae) {
            System.err.println("Runtime::<clinit> could not access runtime " + rt);
            throw new Error(iae);
        } catch (InstantiationException ie) {
            System.err.println("Runtime::<clinit> could not create runtime " + rt);
            throw new Error(ie);
        } catch (Throwable t) {
            System.err.println("Runtime::<clinit> unknown exception during creation of runtime " + rt);
            throw new Error(t);
        } finally {        
            assert (r != null);
            runtime = r;
            java = new JavaRuntime();
            factory = runtime.getFactory();
            // ArrayFactory.init(r);
        }
    }
    public static Factory factory; // = runtime.getFactory();
   
    protected abstract void initialize();
    
    public static void main(String[] args) {
    	try {
            String[] args_stripped = Configuration.parseCommandLine(args);
            init();
    		runtime.run(args_stripped);
    	} catch (Exception e) {
    		Runtime.java.error("Unexpected Exception in X10 Runtime.", e);
    	}
    	System.exit(returnValue);
    }

    public static void setExitCode(int rv) {
    	returnValue = rv;
    }
    
    protected Runtime() {}

    public static abstract class Factory {
    	public abstract region.factory getRegionFactory();
    	public abstract distribution.factory getDistributionFactory();
    	public abstract point.factory getPointFactory();
    	public abstract clock.factory getClockFactory();
        public abstract booleanArray.factory getBooleanArrayFactory();
        public abstract charArray.factory getCharArrayFactory();
        public abstract byteArray.factory getByteArrayFactory();
        public abstract shortArray.factory getShortArrayFactory();
    	public abstract intArray.factory getIntArrayFactory();
    	public abstract longArray.factory getLongArrayFactory();
        public abstract floatArray.factory getFloatArrayFactory();
        public abstract doubleArray.factory getDoubleArrayFactory();
        public abstract genericArray.factory getGenericArrayFactory();
    	public abstract place.factory getPlaceFactory();
    }

    public abstract Factory getFactory();

    /**
     * Run the X10 application.
     */
    protected abstract void run(String[] args) throws Exception;

    public abstract Place currentPlace();
    
    public abstract Place[] getPlaces();
    
    /**
     * @return The place where the current execution takes place 
     * ('here' in X10).
     */
    public static Place here() {
        Place p = runtime.currentPlace();
        assert p != null;
    	return p;
    }
    
    public static void checkHere(place p) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && !p.equals(here()))
            throw new BadPlaceException();
    }
    
    /**
     * Method used to do dynamic nullcheck when nullable is casted away.
     */
    public static java.lang.Object nullCheck(java.lang.Object o) {
        if (o == null)
            throw new NullPointerException("Cast of value 'null' to non-nullable type failed.");
        return o;
    }
    
    /**
     * Method used to do dynamic nullcheck when nullable is casted away.
     */
    public static java.lang.Object placeCheck(java.lang.Object o, x10.lang.place p) {
        java.lang.Object ret = null;
        if (o == null)
            throw new NullPointerException("Place-cast of value 'null' failed.");
        
        if (! (o instanceof x10.lang.Object)) 
            throw new Error("Place-cast currently not available for object of type " + o.getClass().getName());
        
        x10.lang.Object xo = (x10.lang.Object) o;
        if (xo.location.equals(p)) 
            ret = o;
        else {
            // place cast failed
            throw new BadPlaceException();
        }
        return o;
    }
    
    /* 
     * The main thread is a java.lang.Thread --> not an X10 thread and hence 
     * the Activity information has to be provided in a separate record.
     */
    private static ActivityInformation aiMainThread_;
    
    private static class MainActivityInformation implements ActivityInformation {
        private final LinkedList registeredClocks_ = new LinkedList();
        public List getRegisteredClocks() {
            return registeredClocks_;
        }
    }
    
    public static ActivityInformation getCurrentActivityInformation() {
        ActivityInformation ai;
        Thread cur_thread = Thread.currentThread();
        if (cur_thread instanceof ActivityInformation) 
            ai = (ActivityInformation) cur_thread;
        else {
            if (aiMainThread_ == null) {
                // there is no risk of a race here, because this initialization 
                // happens early within the main thread before any other threads
                // are created.
                ai = aiMainThread_ = new MainActivityInformation();
            } else
                ai = aiMainThread_;
        }
        return ai;
    }
    
    public static void doNext() {
        List clks = getCurrentActivityInformation().getRegisteredClocks();
        // Without having a clock ordering, we MUST
        // first do resume on all clocks before calling doNext!
        // Otherwise the lack of order may lead to a deadlock!
        Iterator it = clks.iterator();
        while (it.hasNext()) {
            Clock c = (Clock) it.next();
            if (!c.clockUsedForFuture)
                c.resume();
        }
        it = clks.iterator();
        while (it.hasNext()) {
            Clock c = (Clock) it.next();
            if (!c.clockUsedForFuture)
                c.doNext();
        }
    }
    
    public static Place[] places() {
        Place[] pl = runtime.getPlaces();
        Place[] ret = new Place[pl.length];
        System.arraycopy(pl, 0, ret, 0, pl.length);
        return ret;
    }
   
} // end of Runtime
