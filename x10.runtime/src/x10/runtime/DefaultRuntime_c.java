package x10.runtime;

import java.util.HashMap;
import java.util.Vector;
import java.util.WeakHashMap;


import x10.array.Distribution;
import x10.array.DoubleArray;
import x10.array.IntArray;
import x10.array.Range;
import x10.array.Region;
import x10.array.sharedmemory.SharedMemoryArrayFactory;
import x10.lang.Activity;
import x10.lang.Clock;
import x10.lang.Place;
import x10.lang.Runtime;

/**
 * Default implementation of Runtime.
 * 
 * @author Christian Grothoff, Christoph von Praun
 */
public class DefaultRuntime_c 
    extends Runtime
    implements 
        ThreadRegistry,
        ActivityInformationProvider {

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
    private final HashMap activity2asl_ = new HashMap(); // <Activity,Vector<ActivitySpawnListener>>
    
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
	Object[] tmp = {args};
	Activity boot 
	    = (Activity) Class
	    .forName(args[0])
	    .getDeclaredConstructor(new Class[] { String[].class })
	    .newInstance(tmp);
	Place[] p = getPlaces();
	Place p0 = p[0];
	Thread t = Thread.currentThread();
	registerThread(t, p0);
	registerActivityStart(t, boot, null);
	Statistics_c.boot();
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
        Vector v = (Vector) activity2asl_.get(i);
        if (v == null) {
            v = new Vector(2);
            activity2asl_.put(i,v);
        }
        v.add(asl);
    }

    /**
     * Notification that an activity completed.
     */
    public synchronized void registerActivityStop(Thread t,
                                                  Activity a) {
        thread2activity_.remove(t);
        Vector v = (Vector) activity2asl_.get(a);
        if (v == null) 
            return;
        for (int i=0;i<v.size();i++) {
            ActivitySpawnListener asl = (ActivitySpawnListener) v.elementAt(i);
            asl.notifyActivityTerminated(a);
        }
        activity2asl_.remove(a);
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
        Vector v = (Vector) activity2asl_.get(i);
        if (v == null) 
            return;
        for (int j=0;j<v.size();j++) {
            ActivitySpawnListener asl = (ActivitySpawnListener) v.elementAt(j);
            asl.notifyActivitySpawn(a, i);
        }
    }

    public synchronized Place currentPlace() {
        if (places_.length == 1)
            return places_[0]; // fast path for simple test environments!
    	Place p = (Place) thread2place_.get(Thread.currentThread());
    	if (p == null)
    		throw new Error("This thread is not an X10 thread!");
    	return p;
    }

    /**
     * this method should not be exposed to x10.lang and 
     * application programmers, because the X10 programming model 
     * does not know such construct (Places are obtained indirectly 
     * through distributions).
     *  
     * @return All places available in this VM.
     */
    Place[] getPlaces() {
    	// return defensive copy
    	return (Place[]) places_.clone();
    }
    
    /**
     * Create a new Clock.
     */
    public Clock newClock() {
        return new Clock_c(this);
    }

    /**
     * @return New Range.
     */
    public Range newRange(int lo, int hi)  {
    	return SharedMemoryArrayFactory.newRange(lo, hi);
    }
    
    /**
     * @return New Region.
     */
    public Region newRegion(Range[] dims)  {
    	return SharedMemoryArrayFactory.newRegion(dims);
    }
    
    /**
     * @return New array.
     */
    public IntArray newIntArray(Distribution d) {
        return SharedMemoryArrayFactory.newIntArray(d);
    }

    /**
     * @return New array.
     */
    public DoubleArray newDoubleArray(Distribution d) {
        return SharedMemoryArrayFactory.newDoubleArray(d);
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

    
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over all available Places.
     * @param r
     * @return
     */
    public Distribution newBlockDistribution(Region R, Place[] Q) {
        return SharedMemoryArrayFactory.newBlockDistribution(R, Q);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks of size n over all available Places.
     * @param r
     * @return
     */
    public Distribution newBlockDistribution(Region r, int n, Place[] p) {
        return SharedMemoryArrayFactory.newBlockDistribution(r, n, p);
    }
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public Distribution newCyclicDistribution(Region r, Place[] p) {
        return SharedMemoryArrayFactory.newCyclicDistribution(r,  p);
    }
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public Distribution newBlockCyclicDistribution(Region r, int n, Place[] p) {
        return SharedMemoryArrayFactory.newBlockCyclicDistribution(r, n, p);
    }
    
    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    public Distribution newArbitraryDistribution(Region r, Place[] p) {
        return SharedMemoryArrayFactory.newArbitraryDistribution(r, p);
    }
    
    /**
     * Create a Distribution where all points in the given
     * Region are mapped to the same Place.
     * @param r
     * @param p specifically use the given place for all points
     * @return
     */
    public Distribution newConstantDistribution(Region r, Place p) {
        return SharedMemoryArrayFactory.newConstantDistribution(r, p);
    }
    
    /**
     * Create a Distribution where the points in the
     * region 1...p.length are mapped to the respective
     * places.
     * @param p the list of places (implicitly defines the region)
     * @return
     */
    public Distribution newUniqueDistribution(Place[] p) {
        return SharedMemoryArrayFactory.newUniqueDistribution(p);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over all available Places.
     * @param r
     * @return
     */
    public Distribution newBlockDistribution(Region R) {
        return SharedMemoryArrayFactory.newBlockDistribution(R, places_);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks of size n over all available Places.
     * @param r
     * @return
     */
    public Distribution newBlockDistribution(Region r, int n) {
        return SharedMemoryArrayFactory.newBlockDistribution(r, n, places_);
    }
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public Distribution newCyclicDistribution(Region r) {
        return SharedMemoryArrayFactory.newCyclicDistribution(r,  places_);
    }
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public Distribution newBlockCyclicDistribution(Region r, int n) {
        return SharedMemoryArrayFactory.newBlockCyclicDistribution(r, n, places_);
    }
    
    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    public Distribution newArbitraryDistribution(Region r) {
        return SharedMemoryArrayFactory.newArbitraryDistribution(r, places_);
    }
    
    /**
     * Create a Distribution where the points in the
     * region 1...p.length are mapped to the respective
     * places.
     * @param p the list of places (implicitly defines the region)
     * @return
     */
    public Distribution newUniqueDistribution() {
        return SharedMemoryArrayFactory.newUniqueDistribution(places_);
    }

} // end of DefaultRuntime_c