package x10.runtime;

import java.util.HashMap;

/**
 * @author Christian Grothoff
 */
class DefaultRuntime_c implements ThreadRegistry {

    private final Native native_ = new Native();

    private final HashMap thread2place_ = new HashMap();

    private final Place[] places_;

    DefaultRuntime_() {
	// FIXME: add mechanism for dynamic configuration
	// Properties!
	this.places_ 
	    = new Place[] { new LocalPlace_c(this) };
    }

    /**
     * Run the X10 application.
     */
    protected void run(String[] args) throws Exception {
	if (args.length < 1) {
	    System.err.println("Invoke with name of main x10 class!");
	    System.exit(-1);
	}
	String[] appArgs = new String[args.length-1];
	System.arraycopy(args, 1,
			 appArgs, 0,
			 appArgs.length);
	Activity.Async boot 
	    = (Activity.Async) Class
	    .forName(args[0])
	    .getDeclaredConstructor(new Class[] { String[].class })
	    .newInstance(new Object[] { appArgs} );
	Place[] p = initializePlaces();
	p[0].runAsync(boot);
    }
    
    public void registerThread(Thread t, 
4			       Place p) {
	thread2place_.put(t, p);
    }

    public Place here() {
	Place p = (Place) thread2place_.get(Thread.currentThread());
	if (p == null)
	    throw new Error("This thread is not an X10 thread!");
	return p;
    }

    /**
     * Create a new Clock at another place.
     */
    public Clock createClock() {
	throw new Error("not implemented");
    }

    /**
     * Return all places available in this VM.
     */
    public Place[] initializePlaces() {
	return places_;
    }

    /**
     * Create a new array.
     */
    public Array createArray(Distribution d) {
	throw new Error("not implemented");
    }
				      
    /**
     * Get the 'native' API.
     */ 
    public Native getNative() {
	return native_;
    }


} // end of DefaultRuntime_c