package x10.runtime;

import java.util.HashMap;
import java.util.WeakHashMap;

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

    /**
     * At what place is the given thread running (needed to support
     * running multiple Places within the same VM). Note that we're
     * using a weak hash map here since the threadpool code may decide
     * to reduce the number of threads by just letting one exit, and
     * we would in that case not want to hold on to the memory.
     */
    private final WeakHashMap thread2place_ = new WeakHashMap(); // <Thread,Place>

    /**
     * Which activity is the current thread executing?  This one does
     * not have to be a weak hash map since threads are explicitly 
     * removed from the map once they complete a particular activity.
     */
    private final HashMap thread2activity_ = new HashMap(); // <Thread,Activity>
    
    /**
     * What listeners are registered for termination/spawning events
     * for the given activity?
     */
    private final HashMap activity2asl_ = new HashMap(); // <Activity,ActivitySpawnListener>
    
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
    
    /**
     * Notify the asl via a callback whenever the given activity
     * starts another Activity (via async, future or now).
     */
    public synchronized void registerActivitySpawnListener(Activity i,
                                                           ActivitySpawnListener asl) {
        assert null == activity2asl_.get(asl);
        activity2asl_.put(i, asl);
    }

    /**
     * Notification that an activity completed.
     */
    public synchronized void registerActivityStop(Thread t,
                                                  Activity a) {
        thread2activity_.remove(t);
        ActivitySpawnListener asl = (ActivitySpawnListener) activity2asl_.get(a);
        if (asl != null) {
            activity2asl_.remove(a);
            asl.notifyActivityTerminated(a);
        }
    }
    
    /**
     * Notifiation that an activity was started.
     * 
     * @param t the thread that runs the activity
     * @param a the activity that is being run
     * @param i the activity that started a (null for boot/main).
     */
    public synchronized void registerActivityStart(Thread t,
                                                   Activity a,
                                                   Activity i) {
        thread2activity_.put(t,a);
        if (i == null)
            return;
        ActivitySpawnListener m = (ActivitySpawnListener) activity2asl_.get(i);
        if (m != null)
            m.notifyActivitySpawn(a, i);
    }

    public synchronized Place currentPlace() {
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
    public synchronized Activity getCurrentActivity() {
        Activity a = (Activity) thread2activity_.get(Thread.currentThread());
        if (a == null)
            throw new Error("This Thread is not an X10 Thread running X10 code!");
        return a;
    }

} // end of DefaultRuntime_c