package x10.lang;

import x10.runtime.DefaultRuntime_c;

/**
 * This is the central entrypoint to the X10 Runtime for the
 * compiler.   There is exactly one Runtime per JVM running X10.
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
 * @author Christian Grothoff
 * @see Place
 * @see Activity
 */
public abstract class Runtime {

    /**
     * This instance should be used only in the implementation of 
     * the x10.runtime. 
     */
    public static final Runtime _;

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
     * Create a new Clock.
     */
    public abstract Clock createClock();

    /**
     * Return all places available in this VM.
     */
    public abstract Place[] initializePlaces();

    /**
     * Create a new array.
     */
    public abstract Array createArray(Distribution d);
				      
    /**
     * Get the 'native' API.
     */ 
    public abstract JavaRuntime getJavaRuntime();

    /**
     * @return The place where the current execution takes place 
     * ('here' in X10).
     */
    public static Place here() {
    	return _.currentPlace();
    }
    
    public abstract Place currentPlace();
    
} // end of Runtime