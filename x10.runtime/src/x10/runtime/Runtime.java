package x10.runtime;

/**
 * @author Christian Grothoff
 */
public abstract class Runtime {

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

    Runtime() {}

    /**
     * Run the X10 application.
     */
    protected abstract void run(String[] args) throws Exception;

    /**
     * Obtain the place of the current execution.
     */
    public abstract Place here();

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
    public abstract Native getNative();


}