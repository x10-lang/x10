package x10.runtime;

import java.util.HashMap;

import x10.lang.Activity;
import x10.lang.Array;
import x10.lang.Clock;
import x10.lang.Distribution;
import x10.lang.Place;
import x10.lang.Runtime;

/**
 * Default implementation of Runtime.
 *  
 * @author Christian Grothoff
 */
public class DefaultRuntime_c 
    extends Runtime
    implements 
        ThreadRegistry,
        ActivityInformationProvider {

    private final JavaRuntime native_ = new JavaRuntime();

    private final HashMap thread2place_ = new HashMap();

    private final HashMap thread2activity_ = new HashMap();

    /**
     * The places of this X10 Runtime (for now a constant set).
     */
    private final Place[] places_;

    public DefaultRuntime_c() {
        int pc = Configuration.NUMBER_OF_LOCAL_PLACES;
	this.places_ 
	    = new Place[pc];
	for (int i=pc-1;i>=0;i--)
           places_[i] = new LocalPlace_c(this, this);
    }

    /**
     * Shutdown the X10 runtime system.
     */
    public void shutdown() {
        for (int i=places_.length-1;i>=0;i--)
            places_[i].shutdown();
    }

    /**
     * Run the X10 application.
     */
    protected void run(String[] args) throws Exception {
	if (args.length < 1) {
	    System.err.println("Invoke with name of main X10 class!");
	    System.exit(-1);
	}
	String[] appArgs = new String[args.length-1];
	System.arraycopy(args, 1,
			 appArgs, 0,
			 appArgs.length);
	Activity boot 
	    = (Activity) Class
	    .forName(args[0])
	    .getDeclaredConstructor(new Class[] { String[].class })
	    .newInstance(appArgs);
	Place[] p = initializePlaces();
	Place p0 = p[0];
	registerThread(Thread.currentThread(), p0);
	p0.runAsync(boot);
    }
    
    public void registerThread(Thread t, 
			       Place p) {
        if (p == null)
            throw new NullPointerException();
	thread2place_.put(t, p);
    }
    
    public void registerActivityStop(Thread t,
                                     Activity a) {
        thread2activity_.remove(t);
    }
    
    /**
     * Notifiation that an activity was started.
     * 
     * @param t the thread that runs the activity
     * @param a the activity that is being run
     * @param i the activity that started a (null for boot/main).
     */
    public void registerActivityStart(Thread t,
                                      Activity a,
                                      Activity i) {
        thread2activity_.put(t,a);
        // FIXME: for 'now' we will use 'i' here, if 'i' was started
        // in a 'now' section, we have to start 'a' in the same way;
        // Clock will register a 'notify me' method with the Runtime,
        // and the runtime will pass on the news.
    }

    public Place currentPlace() {
	Place p = (Place) thread2place_.get(Thread.currentThread());
	if (p == null)
	    throw new Error("This thread is not an X10 thread!");
	return p;
    }

    /**
     * Create a new Clock.
     */
    public Clock createClock() {
        return new Clock_c(this);
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
    public JavaRuntime getJavaRuntime() {
	return native_;
    }

    /**
     * Get the Activity object that is executing this 
     * method.
     * @return
     */
    public Activity getCurrentActivity() {
        Activity a = (Activity) thread2activity_.get(Thread.currentThread());
        if (a == null)
            throw new Error("This Thread is not an X10 Thread running X10 code!");
        return a;
    }

} // end of DefaultRuntime_c