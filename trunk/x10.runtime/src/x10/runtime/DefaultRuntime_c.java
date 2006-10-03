package x10.runtime;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

import x10.array.ArrayFactory;
import x10.array.DistributionFactory;
import x10.array.point_c;
import x10.array.sharedmemory.DefaultArrayFactory;
import x10.array.sharedmemory.RegionFactory;
import x10.lang.Runtime;
import x10.lang.clock;
import x10.lang.dist;
import x10.lang.place;
import x10.lang.point;
import x10.lang.region;

/**
 * Default implementation of Runtime. Considerably revised 5/16 by vj
 * to remove activity helper classes, and synchronization between
 * main thread and the Boot Activity.
 *
 * @author Christian Grothoff, Christoph von Praun
 * @author vj
 *
 * @author Raj Barik, Vivek Sarkar
 * 3/6/2006: use new getPlace/setPlace interfaces in PoolRunner.
 * Also call runBootAsync() instead of runAsync() for boot activity.
 */
public class DefaultRuntime_c extends Runtime {
	private final class BootActivity extends Activity {
	private final Activity fMain;
	protected Throwable fBootException;
	private boolean ended;

	private BootActivity(Activity main) {
	    super();
	    fMain= main;
	}

	public String myName() {
	    return "Boot activity";
	}

	public void run() {
	    if (Report.should_report("activity", 5)) {
		Report.report(5, PoolRunner.logString() + " starts running the Boot Activity.");
	    }
	    try {
		runWithinFinish(fMain);
		if (Report.should_report("activity", 5)) {
		    Report.report(5, PoolRunner.logString() + " finished running the Boot Activity.");
		}
	    } catch (Error e) {
		// Exception thrown by the activity!
		fBootException= e;
	    } catch (RuntimeException re) {
		// Exception thrown by the activity!
		fBootException= re;
	    } finally {
		synchronized (this) {
		    this.notifyAll();
		    // boolean assign is protected from race condition  
		    // with main thread by the synchronized block
		    this.ended = true;
		}
	    }
	}
	
	/**
	 * Check if boot activity has ended
	 * @return true if boot activity has ended
	 * Needs to be synchronized or this.ended needs to be declared volatile
	 * since this.ended is going to be touched by two different threads.
	 */
	public synchronized boolean isFinished()
	{
		return this.ended;
	}
    }

	/**
	 * The places of this X10 Runtime (for now a constant set).
	 */
	private Place[] places_;
	
	public DefaultRuntime_c() {
	}

	/**
	 * Initialize the places in the XVM.
	 */
	private synchronized void createPlaces() {
		int pc = Configuration.NUMBER_OF_LOCAL_PLACES;
		this.places_ = new Place[pc];
		x10.lang.place.MAX_PLACES = pc;
		for (int i=0;i<pc;i++)
			places_[i] = new LocalPlace_c();
		place.initialize();
	}

	protected synchronized void initialize() {
	    createPlaces();
	}

	protected synchronized void loadAndInitLibs() {
		if (null != Configuration.LOAD) {
			String[] libs = Configuration.LOAD.split(":");
			for (int i=libs.length-1;i>=0;i--)
				System.loadLibrary(libs[i]);
		}
	}

	public void prepareForBoot() {
	    initialize();
	    if (Report.should_report("activity", 5)) {
	    	Thread t = Thread.currentThread();
	    	int tCount = Thread.activeCount();
	    	Report.report(5, Thread.currentThread() + ":" + System.currentTimeMillis() +" starts in group " + t.getThreadGroup()
	    	              + " with " + tCount + " threads active.");
	    	Thread[] a = new Thread[tCount];
	    	int count = Thread.enumerate(a);

	    	for (int i = 0; i < count; i++) {
	    		Report.report(5, "Thread " + (a[i] == null ? "null" : a[i].getName()) + " is active.");
	    	}
	    }
	    // first: load libraries!
	    loadAndInitLibs();
//		// then: initialize the runtime
//		initialize();
	}

	public void shutdown() {
	    shutdownAllPlaces();
	    if (Report.should_report("activity", 5)) {
		Report.report(5, PoolRunner.logString() + " terminates.");
	    }
	    finalizeAndTermLibs();
	    // Dump abstract execution statistics on stderr if requested to do so.
	    // Should be safe to do so after shutting down all places, thread pool, and libs (in theory - kaBOOM).
	    dumpStatistics();
	}

	private void shutdownAllPlaces() {
	    for (int i= 0; i < places_.length; i++) {
		places_[i].shutdown();
//		places_[i]= null;
	    }
	}
	
	protected void finalizeAndTermLibs() {
	}

	private static final Class[] STRING_ARRAYS = new Class[] { String[].class };

	/**
	 * Run the X10 application.
	 */
	protected void run(String[] args) {
	    prepareForBoot();
	    try {
		run(createMainActivity(args));
	    } catch (Throwable e) {
		e.printStackTrace();
	    }
	    shutdown();
	}

	/**
	 * Run the X10 application.
	 * @throws Throwable 
	 */
	public void run(final Activity appMain) {
	    // Create the boot activity
	    BootActivity boot = new BootActivity(appMain);

	    // Run the boot activity.
	    Runtime.runBootAsync(boot);

	    synchronized(boot) {
		try {
			// check if the boot activity has finished before
			// the main thread has called the wait method
	    	if (!boot.isFinished()) {
	    		boot.wait();
	    	}
		} catch (InterruptedException e) {
		    // NOTREACHED
		    e.printStackTrace();
		}
	    }
	    	
	    if (boot.fBootException instanceof Error)
		throw (Error) boot.fBootException;
	    else if (boot.fBootException instanceof RuntimeException)
		throw (RuntimeException) boot.fBootException;
	    return;
	}
	
	private Activity createMainActivity(String[] args) throws Error {
	    // Find the applications main activity.
	    java.lang.Object[] tmp = { args };
	    Activity atmp = null;
	    try {
	    	if (Report.should_report("activity", 5)) {
	    		Report.report(5, Thread.currentThread()  + ":" + System.currentTimeMillis() + " " + this + " starting user class |"
	    		              + Configuration.MAIN_CLASS_NAME+ "|.");
	    	}
	    	Class main = Class.forName(Configuration.MAIN_CLASS_NAME + "$Main");
	    	if (Configuration.PRELOAD_CLASSES)
	    		PreLoader.preLoad(main, Configuration.PRELOAD_STRINGS);
	    	atmp = (Activity) main.getDeclaredConstructor(STRING_ARRAYS).newInstance(tmp);
	    } catch (Exception e) {
	    	System.err.println("Could not find default constructor of main class '"
	    	                   + Configuration.MAIN_CLASS_NAME+ "$Main" + "'!");
	    	throw new Error(e);
	    }
	    final Activity appMain = atmp;
	    return appMain;
	}

	/**
	 * Set the current place in the PoolRunner. Should only
	 * be used by the X10 runtime with care. In general
	 * the X10 runtime should use currentActivity().getPlace().
	 * @see currentPlace()
	 */
	public synchronized void setCurrentPlace(place p) {
		assert p != null;
		Thread t = Thread.currentThread();
		if (t instanceof PoolRunner)
			((PoolRunner)t).setPlace((Place)p);
	}

	/**
	 * The place at which the current Thread is running, as recorded in the PoolRunner.
	 * Is different from currentActivity().getPlace() only in very special circumstances,
	 * for instance during the execution of the body of an array initializer.
	 * @see setCurrentPlace(place)
	 */
	public synchronized Place currentPlace() {
		if (getPlaces().length == 1)
			return getPlaces()[0]; // fast path for simple test environments!
		Thread t = Thread.currentThread();
		Place ret = null;
		if (t instanceof PoolRunner)
			ret=(Place)((PoolRunner)t).getPlace();

	
		return ret;
	}

	/**
	 * Return the activity being executed by the current thread.
	 */
	public Activity currentActivity() {
		Thread t = Thread.currentThread();
		Activity result = null;
		if(t instanceof ActivityRunner) {
			result = ((ActivityRunner)t).getActivity();
		}

	
		return result;
	}

	/**
	 * Should be used only internally to the XVM. Should not
	 * be exposed to the X10 programmer.
	 * @see x10.lang.place
	 * @return All places available in this VM.
	 */
	protected Place[] getPlaces() {
		return places_;
	}

	protected Place[] getLocalPlaces() {
		return getPlaces();
	}

    public Factory getFactory() {
		Factory f = new Factory() {
			public region.factory getRegionFactory() {
				return new RegionFactory();
			}
			public dist.factory getDistributionFactory() {
				return new DistributionFactory();
			}
			public point.factory getPointFactory() {
				return new point_c.factory();
			}
			public clock.factory getClockFactory() {
				return new clock.factory() {
					public clock clock() {
						return new Clock();
					}
					public clock clock(String name) {
						return new Clock(name);
					}
				};
			}
			public ArrayFactory getArrayFactory() {
				return new DefaultArrayFactory();
			}
			public place.factory getPlaceFactory() {
				return new place.factory() {
					public place place(int i) {
						int index =(i % place.MAX_PLACES);
						return places_[index];
					}
					/**
					 * Return the set of places from place(0) to place(last) (inclusive).
					 */
					public Set/*<place>*/ places (int last) {
						Set result = new TreeSet();
						for (int i=0; i <= last % (place.MAX_PLACES); i++)
							result.add(places_[i]);
						return result;
					}
					public place here() {
						return currentPlace();
					}
				};
			}
		};
		return f;
	}

	private void dumpStatistics() {
	    if (JITTimeConstants.ABSTRACT_EXECUTION_STATS) {
		System.err.println("\n#### START OF ABSTRACT EXECUTION STATISTICS (EXCLUDING MAIN ACTIVITY) ####");
		{
		    // PRINT STATISTICS ON NUMBER OF ACTIVITES
		    {
			long sum= 0;
			for(int i= 0; i <= getPlaces().length - 1; i++) {
			    sum+= getPlaces()[i].getThreadPool().getCompletedTaskCount();
			}
			System.err.println("  TOTAL NUMBER OF ACTIVITIES = " + sum);
			System.err.print("  TOTAL NUMBER OF ACTIVITIES PER PLACE = [ ");
			for(int i= 0; i <= getPlaces().length - 1; i++) {
			    System.err.print(getPlaces()[i].getThreadPool().getCompletedTaskCount() + " ");
			}
			System.err.println("]");
		    }
		    // PRINT STATISTICS ON NUMBER OF OPS DEFINED BY CALLS TO
		    // x10.lang.perf.addLocalOps()
		    long sum= 0;
		    for(int i= 0; i <= getPlaces().length - 1; i++) {
			sum+= getPlaces()[i].getTotalOps();
		    }
		    System.err.println("\n  TOTAL NUMBER OF OPS DEFINED BY CALLS TO x10.lang.perf.addLocalOps() = " + sum);
		    System.err.print("  TOTAL NUMBER OF OPS PER PLACE = [ ");
		    for(int i= 0; i <= getPlaces().length - 1; i++) {
			System.err.print(getPlaces()[i].getTotalOps() + " ");
		    }
		    System.err.println("]");
		    // PRINT STATISTICS ON CRITICAL PATH LENGTHS OF OPS
		    // DEFINED BY CALLS TO x10.lang.perf.addLocalOps()
		    long max= 0;
		    for(int i= 0; i <= getPlaces().length - 1; i++) {
			max= Math.max(max, getPlaces()[i].getCritPathOps());
		    }
		    System.err.println("\n  CRITICAL PATH LENGTH OF OPS DEFINED BY CALLS TO x10.lang.perf.addLocalOps() = " + max);
		    System.err.print("  CRITICAL PATH LENGTH OF OPS PER PLACE = [ ");
		    for(int i= 0; i <= getPlaces().length - 1; i++) {
			System.err.print(getPlaces()[i].getCritPathOps() + " ");
		    }
		    System.err.println("]");
		    double speedup= (double) max > 0 ? (double) sum / (double) max : 0;
		    System.err.println("\n  IDEAL SPEEDUP IN NUMBER OF OPS, (TOTAL NUMBER) / (CRIT PATH LENGTH) = " + speedup);
		}
		if (JITTimeConstants.ABSTRACT_EXECUTION_TIMES) {
		    // PRINT STATISTICS ON TOTAL UNBLOCKED EXECUTION TIME
		    long sum= 0;
		    for(int i= 0; i <= getPlaces().length - 1; i++) {
			sum+= getPlaces()[i].getTotalUnblockedTime();
		    }
		    System.err.println("\n  TOTAL UNBLOCKED TIME FOR ALL ACTIVITIES (in milliseconds) = " + sum);
		    System.err.print("  TOTAL UNBLOCKED TIME PER PLACE = [ ");
		    for(int i= 0; i <= getPlaces().length - 1; i++) {
			System.err.print(getPlaces()[i].getTotalUnblockedTime() + " ");
		    }
		    System.err.println("]");
		    // PRINT STATISTICS ON ESTIMATED EXECUTION TIMES
		    long max= 0;
		    for(int i= 0; i <= getPlaces().length - 1; i++) {
			max= Math.max(max, getPlaces()[i].getCritPathTime());
		    }
		    System.err.println("\n  CRITICAL PATH LENGTH OF ALL ACTIVITIES (in milliseconds) = " + max);
		    System.err.print("  CRITICAL PATH LENGTH PER PLACE = [ ");
		    for(int i= 0; i <= getPlaces().length - 1; i++) {
			System.err.print(getPlaces()[i].getCritPathTime() + " ");
		    }
		    System.err.println("]");
		    double speedup= (double) max > 0 ? (double) sum / (double) max : 0;
		    System.err.println("\n  IDEAL SPEEDUP IN EXECUTION TIME,(TOTAL TIME) / (CRIT PATH LENGTH) = " + speedup);
		}
		System.err.println("#### END OF ABSTRACT EXECUTION STATISTICS (EXCLUDING MAIN ACTIVITY) ####");
	    }
	}
} // end of DefaultRuntime_c

