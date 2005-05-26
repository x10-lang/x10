package x10.lang;


import x10.runtime.Activity;
import x10.runtime.Configuration;
import x10.runtime.DefaultRuntime_c;
import x10.runtime.Place;
import x10.runtime.Report;


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

    public static Runtime runtime;
    
  
    
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
    	//System.out.println(Thread.currentThread() + "starting main(String[]).");
    	try {
            String[] args_stripped = Configuration.parseCommandLine(args);
            init();
    		runtime.run(args_stripped);
    	} catch (Exception e) {
    		Runtime.java.error("Unexpected Exception in X10 Runtime.", e);
    	}
    	// System.exit(returnValue);
    }

    static int exitCode;
    public static void setExitCode(int code) {
    	exitCode = code;
    }
    public static void x10Exit() {
    	if (Report.should_report("activity", 3)) {
    		Thread t = Thread.currentThread();
    		Report.report(3, t+ "@"+System.currentTimeMillis() 
    				+ " The XVM is now terminating.");
    				
    	}
    	System.exit( exitCode );
    }
    
    protected Runtime() {}

    public static abstract class Factory {
    	public abstract region.factory getRegionFactory();
    	public abstract dist.factory getDistributionFactory();
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
    
    public abstract void setCurrentPlace(place p);
    
    public abstract Place currentPlace();
    
    public abstract Activity currentActivity();
    public abstract Place[] getPlaces();
    
    /**
     * @return The place where the current execution takes place 
     * ('here' in X10).
     */
    public static Place here() {
        place p = runtime.currentPlace();
        assert p != null;
    	return (Place) p;
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
        if (xo.getLocation().equals(p)) 
            ret = o;
        else {
            // place cast failed
            throw new BadPlaceException();
        }
        return o;
    }
    
    public static Activity getCurrentActivity () {
    	return runtime.currentActivity();
    }
    
    public static Place[] places() {
        Place[] pl = runtime.getPlaces();
        Place[] ret = new Place[pl.length];
        System.arraycopy(pl, 0, ret, 0, pl.length);
        return ret;
    }
    public static void runAsync( Activity a) {
    	runtime.getPlaces()[0].runAsync(a);
    }
    public static Future runFuture( Activity.Expr a) {
    	return runtime.getPlaces()[0].runFuture(a);
    }
   
} // end of Runtime
