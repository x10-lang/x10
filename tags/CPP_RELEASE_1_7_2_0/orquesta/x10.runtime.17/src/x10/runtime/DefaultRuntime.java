/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

import x10.core.RailFactory;
import x10.core.ValRail;

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
public class DefaultRuntime extends Runtime {
	/* Pre-load the VMInterface class */
	static { VMInterface foo = null; }

	/**
	 * The places of this X10 Runtime (for now a constant set).
	 */
	private Place[] places_;
	
	public DefaultRuntime() {
	}

        private ValRail<Place> placesRail;
        public synchronized ValRail<Place> getPlacesRail() {
            if (placesRail == null)
                placesRail = RailFactory.<Place>makeValRailFromJavaArray(places_);
            return placesRail;
        }

        public Place getFirstPlace() {
            return places_[0];
        }

        public int getMaxPlaces() {
            return places_.length;
        }

	/**
	 * Initialize the places in the XVM.
	 */
	private synchronized void createPlaces() {
		int pc = Configuration.NUMBER_OF_LOCAL_PLACES;
		this.places_ = new Place[pc];
		for (int i=0;i<pc;i++)
			places_[i] = new LocalPlace(i);
	}

	/**
	 * Initialize the default runtime by creating X10 places.
	 */
	protected synchronized void initialize() {
	    createPlaces();
            MAX_PLACES = places_.length;
            FIRST_PLACE = places_[0];
	}

	/**
	 * Load and init shared library
	 */
	protected synchronized void loadAndInitLibs() {
		if (null != Configuration.LOAD) {
			String[] libs = Configuration.LOAD.split(":");
			for (int i=libs.length-1;i>=0;i--)
				System.loadLibrary(libs[i]);
		}
	}

	/**
	 * Initialize the runtime:
	 * - Creates places
	 * - Load and init libs
	 */
	public void prepareForBoot() {
	    initialize();
	    if (Report.should_report(Report.ACTIVITY, 5)) {
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

	/**
	 * Shutdown the runtime, this means also shuting down all places associated with this runtime
	 */
	public void shutdown() {
	    shutdownAllPlaces();
	    if (Report.should_report(Report.ACTIVITY, 5)) {
		Report.report(5, PoolRunner.logString() + " terminates.");
	    }
	    finalizeAndTermLibs();
	    // Dump abstract execution statistics on stderr if requested to do so.
	    // Should be safe to do so after shutting down all places, thread pool, and libs (in theory - kaBOOM).
	    dumpStatistics();
	}

	/**
	 * Shutdown all places known by this runtime 
	 */
	private void shutdownAllPlaces() {
	    for (int i= 0; i < places_.length; i++) {
		places_[i].shutdown();
//		places_[i]= null;
	    }
	}
	
	/**
	 * @deprecated
	 */
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
	 * Wraps the main activity into a finish.
	 * @throws Throwable Exception thrown by the main activity.
	 */
	public void run(final Activity mainActivity) {
		
		if (Report.should_report(Report.ACTIVITY, 5)) {
			Report.report(5, PoolRunner.logString() + " starts running the Boot Activity.");
		    }

		// submit the main activity to the X10 Runtime
		// and wait for completion by wrapping it into a finish
		Runtime.getDefaultPlace().runAsyncInFinish(mainActivity);
		
    	if (Report.should_report(Report.ACTIVITY, 5)) {
    	    Report.report(5, PoolRunner.logString() + " finished running the Boot Activity.");
    	}
	}	
	
	/**
	 * Instantiate the main activity (i.e. the X10 program/class to run)
	 * @param args
	 * @return An instance of the main X10 program to run as an X10 activity.
	 * @throws Error Exception thrown while main activity is instanciated
	 */
	private Activity createMainActivity(String[] args) throws Error {
	    // Find the applications main activity.
	    java.lang.Object[] tmp = { args };
	    Activity atmp = null;
	    try {
	    	if (Report.should_report(Report.ACTIVITY, 5)) {
	    		Report.report(5, Thread.currentThread()  + ":" + System.currentTimeMillis() + " " + this + " starting user class |"
	    		              + Configuration.MAIN_CLASS_NAME+ "|.");
	    	}
	    	// instanciation using reflexion
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
	public synchronized void setCurrentPlace(Place p) {
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
	 * @see x10.runtime.Place
	 * @return All places available in this VM.
	 */
	@Override
	protected Place[] getPlaces() {
		return places_;
	}

	/**
	 * @deprecated
	 * @return
	 */
	protected Place[] getLocalPlaces() {
		return getPlaces();
	}

	/**
	 * Print Statistics using the abstract metrics mechanism 
	 */
	private void dumpStatistics() {
	    if (VMInterface.ABSTRACT_EXECUTION_STATS) {
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
		if (VMInterface.ABSTRACT_EXECUTION_TIMES) {
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

