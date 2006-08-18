package x10.runtime;

import java.util.ArrayList;
import java.util.Stack;

import x10.lang.Future;
import x10.lang.place;
import x10.runtime.abstractmetrics.AbstractMetrics;
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
	private AbstractMetrics jitTimeAnalyzer;
	
	
	/********** ACTIVITY RUNNING **********/
	
	
	/*
     * runBootAsync is a special version of runAsync reserved for only the boot activity
     */
	public void runBootAsync(final Activity a) {
		// the boot activity is runned by a classical 
		// java Thread (i.e. not a ActivityRunner)
		// cf prepareAsync method
           this.runActivity(a);	
	}
	
	public void runAsync(final Activity a) {
		this.runActivity(a);	
	}

	
	/**
	 * TODO -- CHANGE THE CODE
	 * @param a
	 */
	private void prepareAsync(Activity a) {
		Thread t = Thread.currentThread();
		
		if ( t instanceof ActivityRunner) {
			Activity parent =  ((ActivityRunner) t).getActivity();
			parent.finalizeActivitySpawn(a);
		}
	}

	/**
	 * @param activity
	 */
	protected void runActivity(final Activity activity) {
        this.prepareAsync(activity);
		threadPoolService.execute(new Runnable() {
			public void run() {
				// Get a thread to run this activity.
				PoolRunner activityRunner = (PoolRunner) Thread.currentThread();
				if (Report.should_report(Report.ACTIVITY, 5)) {
					Report.report(5, activityRunner + " is running " + this);
				}
				
				// Install the activity.
				activityRunner.setActivity(activity);
				activityRunner.setPlace(LocalPlace_c.this); // TODO Remove this later
				activity.setPlace(LocalPlace_c.this);

				try {
						activity.run();
				} catch (Throwable e) {
					activity.finalizeTermination(e);
					return;
				}
				
				activity.finalizeTermination(); //should not throw an exception.
			}
			public String toString() { return "<Executor " + activity + ">";}
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
	public int getNbThreadBlocked() {
		return nbThreadBlocked.get();
	}
	
	/**
	 * Increase place's number of blocked threads counter.
	 * Add a new thread to the pool only if number of threads 
     * waiting is greater or equal to corepoolsize
	 */
	public void threadBlockedNotification() {
		nbThreadBlocked.addAndGet(1);
	
		if(this.getNbThreadBlocked() >= this.getThreadPool().getCorePoolSize()) 
    		this.getThreadPool().increasePoolSize();
	}
	
	/**
	 * 
	 * Decrease number of blocking threads 
	 */
	public void threadUnblockedNotification() {
		nbThreadBlocked.addAndGet(-1);
		this.getThreadPool().decreasePoolSize();
	}
		
	public boolean isShutdown() { 
		return shutdown; 
	}

	
	/**
	 * Start of code to support abstract execution model
	 */
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#addUnblockedTime(long)
	 */
	public void addUnblockedTime(long t) {
		this.jitTimeAnalyzer.addUnblockedTime(t);
	}

	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getTotalOps()
	 */
	public long getTotalOps() { return this.jitTimeAnalyzer.getTotalOps(); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getCritPathOps()
	 */
	public long getCritPathOps() { return this.jitTimeAnalyzer.getCritPathOps(); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#addLocalOps(long)
	 */
	public void addLocalOps(long n) { this.jitTimeAnalyzer.addLocalOps(n); }
		
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#maxCritPathOps(long)
	 */
	public void maxCritPathOps(long n) {this.jitTimeAnalyzer.maxCritPathOps(n);}

	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getTotalUnblockedTime()
	 */
	public long getTotalUnblockedTime() { return this.jitTimeAnalyzer.getTotalUnblockedTime(); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getCritPathTime()
	 */
	public long getCritPathTime() { return this.jitTimeAnalyzer.getCritPathTime(); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#maxCritPathTime(long)
	 */
	public void maxCritPathTime(long t) { this.jitTimeAnalyzer.maxCritPathTime(t); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getResumeTime()
	 */
	public long getResumeTime() { return this.jitTimeAnalyzer.getResumeTime(); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#setResumeTime()
	 */
	public void setResumeTime() { this.jitTimeAnalyzer.setResumeTime(); }

	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#updateIdealTime()
	 */
	public void updateIdealTime() {
		this.jitTimeAnalyzer.updateIdealTime();
	}
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getCurrentTime()
	 */
	public long getCurrentTime() { return this.jitTimeAnalyzer.getCurrentTime(); }

	/**
	 * End of code to support abstract execution model
	 */
		
	

} // end of LocalPlace_c

