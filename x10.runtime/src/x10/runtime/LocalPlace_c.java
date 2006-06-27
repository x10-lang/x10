package x10.runtime;

import java.util.ArrayList;
import java.util.Stack;

import x10.lang.Future;
import x10.lang.place;
/**
 * A LocalPlace_c is an implementation of a place
 * that runs on this Java Virtual Machine.  In the
 * future we will have RemotePlaces that refer to
 * Places on other machines.
 * 
 * @author Christian Grothoff
 * @author vj
 * 
 * @author Raj Barik, Vivek Sarkar
 * 3/6/2006: add runBootAsync() method, which is like runAsync() except that it is only used for the boot activity
 */
public class LocalPlace_c extends Place {
	
	
	
	
	/**
	 * Is this place shutdown?
	 */
	boolean shutdown;
	
	
	// ahk ... need to do something about this!!
    public  void mapToCorrectPlace(java.lang.Object o){ }
   
	public void runAsync(final Activity a) {
		if (a.activityAsSeenByInvokingVM == Activity.thisActivityIsLocal ||
				a.activityAsSeenByInvokingVM == Activity.thisActivityIsASurrogate) {
			
			mapToCorrectPlace(a);
			prepareAsync(a);
			runAsync( a, false);	
		} else {
			
			prepareAsync(a);
			runAsync( a, true);
		}
	}
	
	/*
     * runBootAsync is a special version of runAsync reserved for only the boot activity
     */
	public void runBootAsync(final Activity a) {
		if (a.activityAsSeenByInvokingVM == Activity.thisActivityIsLocal ||
				a.activityAsSeenByInvokingVM == Activity.thisActivityIsASurrogate) {
			
			mapToCorrectPlace(a);
			a.initializeActivity();
			runAsync( a, false);	
		} else {
			
			a.initializeActivity();
			runAsync( a, true);
		}
	}
	
	
   
	public void runAsyncNoRemapping(final Activity a) {
		prepareAsync(a);
		if (a.activityAsSeenByInvokingVM == Activity.thisActivityIsLocal ||
				a.activityAsSeenByInvokingVM == Activity.thisActivityIsASurrogate) {			
			runAsync( a, false);
		} else {
			runAsync( a, true);
		}
	}

	public void runAsyncLater(Activity a) {
		if (Configuration.OPTIMIZE_FOREACH) {
			if (a.activityAsSeenByInvokingVM == Activity.thisActivityIsLocal ||
					a.activityAsSeenByInvokingVM == Activity.thisActivityIsASurrogate) {
				mapToCorrectPlace(a);
				prepareAsync(a);
				
			} else {
				prepareAsync(a);
				
			}
		} else {
			runAsync(a);
		}
	}	
	/**
	 * TODO -- CHANGE THE CODE
	 * @param a
	 */
	private void prepareAsync(Activity a) {
		Thread t = Thread.currentThread();
		
		if ( t instanceof ActivityRunner && a.activityAsSeenByInvokingVM == Activity.thisActivityIsLocal) {
			Activity parent =  ((ActivityRunner) t).getActivity();
			parent.finalizeActivitySpawn(a);
		}
		a.initializeActivity();
	}
    
	/**
	 * Run this activity asynchronously, as if it is wrapped in a finish.
	 * That is, wait for its global termination.
	 * @param a
	 */
	public void finishAsync( final Activity a) {
		prepareAsync(a);
		runAsync( a, true);
	}
	
	protected void runAsync(final Activity a, final boolean finish) {
		final boolean performDeserialization = !(a.activityAsSeenByInvokingVM == Activity.thisActivityIsLocal ||
                a.activityAsSeenByInvokingVM == Activity.thisActivityIsASurrogate);
		
		threadPoolService.execute(new Runnable() {
			public void run() {
				// Get a thread to run this activity.
				Thread t = Thread.currentThread();
				if (Report.should_report("activity", 5)) {
					Report.report(5, t + " is running " + this);
				}
				
				// Install the activity.
				((PoolRunner)t).setActivity(a);
				((PoolRunner)t).setPlace(LocalPlace_c.this); // TODO Remove this later
				
				a.setPlace(LocalPlace_c.this);
				

				try {
					if (finish) {
						a.finishRun();
					} else {
						a.run();
					}
				} catch (Throwable e) {
					a.finalizeTermination(e);
					return;
				}
				
				a.finalizeTermination(); //should not throw an exception.
			}
			public String toString() { return "<Executor " + a + ">";}
			
		});
	}
	
	/**
	 * Run the given activity asynchronously.  Return a handle that
	 * can be used to force the future result.
	 */
	public Future runFuture(final Activity.Expr a) {
		
		Future_c result = a.future = new Future_c();
		runAsync(a);
		
		return result;
	}
	 
	public String longName() {
		return this.toString() + "(shutdown="+shutdown+")";
	}
	
	public static void shutdownAll() {
		Place[] places = x10.lang.Runtime.places();
		for (int i=places.length-1;i>=0;i--) {
			if (places[i] instanceof LocalPlace_c) {
				((LocalPlace_c) places[i]).shutdown();
			}
		}
	}
	
	/**
	 * Shutdown this place, the current X10 runtime will exit.
	 */
	public synchronized void shutdown () {
		//System.out.println("Shutdown Place="+this+"::==>#of threads="+threadPoolService.getCorePoolSize());
	    threadPoolService.shutdownNow();
	    shutdown=true;
	}

	/**
	 * Get threadpool
	 */
	public X10ThreadPoolExecutor getThreadPool() {
		return threadPoolService;
	}
	
	/**
	 * get number of blocking threads at the current 
	 * 
	 */
	public synchronized int getNumBlocked() {
		return numBlocked;
	}
	
	/**
	 * increase number of blocking threads
	 * 
	 */
	public synchronized void incNumBlocked() {
		numBlocked++;
	}
	
	/**
	 * 
	 * Decrease number of blocking threads 
	 */
	public synchronized void decNumBlocked() {
		numBlocked--;
	}
		
	public boolean isShutdown() { 
		return shutdown; 
	}
	
	/**
	 * Start of code to support abstract execution model
	 */
	
	/*
	 * totalOps and critPathOps keep track of operations defined by user by calls to x10.lang.perf.addLocalOps()
	 */
	private long totalOps = 0; // Total unblocked work done by this activity (in units of user-defined ops)
	private long critPathOps = 0; // Critical path length for this activity, including dependences due to child activities (in units of user-defined ops)
	
	synchronized public long getTotalOps() { return totalOps; }
	
	synchronized public long getCritPathOps() { return critPathOps; }
	
	synchronized public void addLocalOps(long n) { totalOps += n; }
	
	synchronized public void maxCritPathOps(long n) { critPathOps = Math.max(critPathOps, n); }
	
	private long totalUnblockedTime = 0; // Total unblocked work done at this place
	private long critPathTime = 0; // Ideal time = max critical path length of all activities executed at this place

	synchronized public long getTotalUnblockedTime() { return totalUnblockedTime; }
	
	synchronized public long getCritPathTime() { return critPathTime; }
	
	synchronized public void addUnblockedTime(long t) { totalUnblockedTime += t; }
	
	synchronized public void maxCritPathTime(long t) { 
		critPathTime = Math.max(critPathTime, t); 
		}

	/**
	 * End of code to support abstract execution model
	 */
		
	

} // end of LocalPlace_c

