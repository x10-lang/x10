/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

import x10.runtime.abstractmetrics.AbstractMetrics;
import x10.runtime.abstractmetrics.AbstractMetricsFactory;
/**
 * A LocalPlace_c is an implementation of a place
 * that runs on this Java Virtual Machine.  In the
 * future we will have RemotePlaces that refer to
 * Places on other machines.
 * 
 * @author Christian Grothoff
 * @author vj
 * 
 * Extensions for JUC implementation
 * @author Raj Barik, Vivek Sarkar
 */
public class LocalPlace extends Place {

    protected LocalPlace(int id) {
        super(id);
    }
    
	/**
	 * Is this place shutdown?
	 */
	boolean shutdown;
	
	/**
	 * Abstract Metrics performance manager, only created if
	 * JITTimeConstants.ABSTRACT_EXECUTION_STATS is set to true
	 */
	private AbstractMetrics abstractMetricsManager = AbstractMetricsFactory.getAbstractMetricsManager();
	
	
	/********** SUBMIT ACTIVITY TO THE PLACE **********/
	
	
	/**
	 * Allows to run an activity asynchronously 
	 * regarding current activity, in this place.
	 */
		public void runAsync(final Activity a) {
		this.runActivity(a);
	}
		
	/**
	 * Allows to run an activity as if it is wrap in a finish construct.
	 * This method spawn a child activity wrap by startFinish and stopFinish 
	 * methods. 
	 * WARNING, the finish occurs inside spawned activity, not in the parent !
	 * @param activity The activity to run in a finish
	 */
	public void runAsyncInFinish(final Activity activity)
	{
		activity.setInvocationStrategy(InvocationStrategy.ASYNC_IN_FINISH);
		
		// asynchronously submitting the activity to the threadpool
		this.runAsync(activity);
		
		try {
	    	// We have to wait for the spawned activity
			// to complete because it is run by the threadpool 
				activity.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		// else throw exception
	}
	

	/**
	 * Prepare activity spawn:
	 * Update FinishState object of the current activity (the parent).
	 * This method must always be called when spawning a child activity as it is responsible
	 * to register the spawning in the parent activity.
	 * @param childActivity Child activity to spawn
	 */
	private void prepareActivity(Activity childActivity) {
		// spawned activity will execute in this place
		childActivity.setPlace(LocalPlace.this);

		// the boot activity is runned by a classical java Thread (i.e. not a ActivityRunner) 
		// hence the following code will not be executed which is normal as a boot activity has no parent ...
		Activity parent;
		if ((parent = Runtime.getCurrentActivity()) != null) {
			parent.finalizeActivitySpawn(childActivity);
		}
		// else throw exception ?
	}
	
	/**
	 * Submit an activity to place's thread pool
	 * This method is initialize some activity properties such as FinishState and current Place.
	 * @param activity The activity to spawn.
	 */
	protected void runActivity(final Activity activity) {
		//  Update parent finish state status
		this.prepareActivity(activity);
		
		// submitting the task to X10 thread pool
		threadPoolService.execute(activity); // exec in finish
	}
	
	/**
	 * Run the given activity asynchronously.  Return a handle that
	 * can be used to force the future result.
	 */
	public <T> Future<T> runFuture(final Future_c.Activity<T> a) {
		
		Future_c<T> result = new Future_c<T>(a.type);
		a.future = result;
		runAsync(a);
		
		return result;
	}
	 
	/**
	 * This method seems never being use...
	 * @deprecated
	 * @return
	 */
	public String longName() {
		return this.toString() + "(shutdown="+shutdown+")";
	}
	
	/**
	 * Shutdown this place, the current X10 runtime will exit.
	 * This method is useful to implements some place custom shutdown behaviour.
	 */
	public void customShutdown () {
		// this method was synchronized, can't see why this would have been necessary
		if (Report.should_report(Report.ACTIVITY, 5)) {
		Report.report(5, PoolRunner.logString() + " shutting down " + this);
	    }
	    shutdown=true;
	}
	
	
	/********** X10 Thread pool event notification **********/
	
	
	/**
	 * Get number of threads currently blocked in this place
	 */
	public int getNbThreadBlocked() {
		return nbThreadBlocked.get();
	}
	
	/**
	 * Increases place's number of blocked threads counter.
	 * Add a new thread to the pool only if number of threads 
     * waiting is greater or equal to corepoolsize
	 */
	public void threadBlockedNotification() {
		nbThreadBlocked.addAndGet(1);
	
		if(this.getNbThreadBlocked() >= this.getThreadPool().getCorePoolSize()) 
    		this.getThreadPool().increasePoolSize();
	}
	
	/**
	 * Decreases number of blocked threads 
	 */
	public void threadUnblockedNotification() {
		nbThreadBlocked.addAndGet(-1);
		this.getThreadPool().decreasePoolSize();
	}
		
	/**
	 * Is this place shutdown ?
	 * @return true if shutdown.
	 */
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
		this.abstractMetricsManager.addUnblockedTime(t);
	}

	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getTotalOps()
	 */
	public long getTotalOps() { return this.abstractMetricsManager.getTotalOps(); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getCritPathOps()
	 */
	public long getCritPathOps() { return this.abstractMetricsManager.getCritPathOps(); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#addLocalOps(long)
	 */
	public void addLocalOps(long n) { this.abstractMetricsManager.addLocalOps(n); }
		
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#addLocalOps(long)
	 */
	public void addCritPathOps(long n) { this.abstractMetricsManager.addCritPathOps(n); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#maxCritPathOps(long)
	 */
	public void maxCritPathOps(long n) {this.abstractMetricsManager.maxCritPathOps(n);}

	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getTotalUnblockedTime()
	 */
	public long getTotalUnblockedTime() { return this.abstractMetricsManager.getTotalUnblockedTime(); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getCritPathTime()
	 */
	public long getCritPathTime() { return this.abstractMetricsManager.getCritPathTime(); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#maxCritPathTime(long)
	 */
	public void maxCritPathTime(long t) { this.abstractMetricsManager.maxCritPathTime(t); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getResumeTime()
	 */
	public long getResumeTime() { return this.abstractMetricsManager.getResumeTime(); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#setResumeTime()
	 */
	public void setResumeTime() { this.abstractMetricsManager.setResumeTime(); }

	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#updateIdealTime()
	 */
	public void updateIdealTime() {
		this.abstractMetricsManager.updateIdealTime();
	}
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getCurrentTime()
	 */
	public long getCurrentTime() { return this.abstractMetricsManager.getCurrentTime(); }

	/**
	 * End of code to support abstract execution model
	 */
		
	

} // end of LocalPlace_c

