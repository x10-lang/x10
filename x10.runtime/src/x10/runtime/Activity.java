package x10.runtime;

import java.util.List;
import java.util.Stack;

import x10.lang.ClockUseException;
import x10.lang.MultipleExceptions;
import x10.runtime.abstractmetrics.AbstractMetrics;
import x10.runtime.abstractmetrics.AbstractMetricsFactory;
import x10.runtime.clock.ClockManager;
import x10.runtime.clock.ClockManagerFactory;

/** The representation of an X10 async activity.
 * <p>The code below uses myThread/someThread annotations on methods. 
 * <p>A thread is said to govern the Activity it is executing. An Activity governs
 * every FinishState it creates to implement its finishes. (An Activity does
 * not govern the FinishState it was created with -- that is governed by the parent
 * activity under whose finish this async was spawned.)
 * <p>An Activity governs itself.
 * <p>An Activity may govern other objects as well. (Note that govern does not imply
 * own; where own is taken in the graph-theoretical sense of being a dominator. That is,
 * if A governs B it may be the case that objects C not governed by A reference B.)
 * <p>A method on an object O is annotated myThread if the only thread that can invoke the method is one
 * which is executing the activity that governs O. 
 * <p> If a field is accessed only by methods labeled myThread, then the field is not used for
 * inter-thread communication.
 * <p> A method on an object O is annotated someThread if there is no constraint on the thread
 * that may invoke it.
 * <p> A method on an object O is annotated mySpawningThread if the only thread that can invoke
 * the method is the one executing the activity, and the thread can invoke this method only before
 * it has invoked the activity's run method, i.e. initiated the activity.
 * <p> Note that an Activity object is created by a different thread than the one that executes it. 
 * We require that the initiating thread not invoke any methods on the Activity object and merely
 * pass it to the thread that executes the Activity.
 * @author Christian Grothoff, Christoph von Praun, vj
 * 
 * @author Raj Barik, Vivek Sarkar
 * 3/6/2006: inserted addPoolCalled flag in support of JCU implementation 
 * to ensure that addPool() is called at most once per activity.
 */
public abstract class Activity implements Runnable, AbstractMetrics {


	/*
	 * The place on which this activity runs. 
	 * 
	 */
	protected Place place_;

	protected FinishState finishState_ = null;
	
	// The finishStack is lazily created
	protected Stack finishStack_ = null;
	
	// The clockManager is lazily created
	protected ClockManager activityClockManager;
	
	/**
	 * Exception collector for this activity. This is field is used by the default
	 * runtime to manage activities.  The stack is a stack of Throwables.
	 */

	/**
	 * The FinishState up in the invocation tree within who's finish
	 * this activity is being executed.
	 */
	protected FinishState rootNode_;

	// abstract metrics manager
	private AbstractMetrics abstractMetricsManager;

	
	/********** ACTIVITY CREATION AND INITIALIZATION **********/
	
	
	/**
	 * Create an activity.
	 * @thread mySpawningThread
	 */
	public Activity() {
		if (JITTimeConstants.ABSTRACT_EXECUTION_STATS)
			this.abstractMetricsManager = AbstractMetricsFactory.getAbstractMetricsManager();
		this.initializeActivity();
	}
	
	/**
	 * Create an activity with the given set of clocks.
	 * @thread mySpawningThread  
	 * @param clocks
	 */
	public Activity(List clocks) {
		if (JITTimeConstants.ABSTRACT_EXECUTION_STATS)
			this.abstractMetricsManager = AbstractMetricsFactory.getAbstractMetricsManager();
		this.activityClockManager = ClockManagerFactory.getClockManager(this, clocks);
		this.initializeActivity();
	}

	/**
	 * Create an activity with the given clock.
	 * @thread mySpawningThread  
	 * @param clock
	 */
	public Activity(Clock clock) {
		if (JITTimeConstants.ABSTRACT_EXECUTION_STATS)
			this.abstractMetricsManager = AbstractMetricsFactory.getAbstractMetricsManager();
		this.activityClockManager = ClockManagerFactory.getClockManager(this, clock);
		this.initializeActivity();
	}

	/**
	 * This activity is about to be executed. 
	 * Perform whatever initialization is necessary,
	 * informing whoever is interested. Must be performed by the thread
	 * that will spawn this activity onto another thread.
	 * @thread mySpawningThread
	 */
	private void initializeActivity() {
		if (Report.should_report(Report.ACTIVITY, 5)) {
			Report.report(5, PoolRunner.logString()
					+ " Activity: initializing " + this);
		}
		
		if(this.activityClockManager != null)
			this.activityClockManager.registerClocks();
		
		if (JITTimeConstants.ABSTRACT_EXECUTION_TIMES) 
			// Record time at which activity was started
			setResumeTime();
	}
	
	/**
	 * @thread mySpawningThread
	 * @param root
	 */
	public void setRootActivityFinishState(FinishState root) {
		this.rootNode_ = root;
	}
	
	/**
	 * Set the place where the activity is executed
	 * @thread mySpawningThread
	 * @param p The place.
	 */
	public void setPlace(Place p) {
		this.place_ = p;
	}

	public Place getPlace() {
		return place_;
	}
	
	
	/********** FINISH ACTIVITY MANAGEMENT **********/
	
	
	/**
	 * Start executing this activity synchronously 
	 * (i.e. with a finish statement).
	 * @thread myThread
	 */
	public void startFinish() {
		if (finishState_ != null) {
			this.getFinishStack().push(finishState_);
		}
		finishState_ = new FinishState(this);
		if (Report.should_report(Report.ACTIVITY, 3)) {
			Report.report(3, PoolRunner.logString() + " " + this
					+ " starts finish " + finishState_);
		}
	}

	/**
	 * Suspend until all activities spawned during this finish 
	 * operation have terminated. Throw an exception if any
	 * async terminated abruptly. Otherwise continue normally.
	 * Should only be called by the thread executing the current activity.
	 */
	public void stopFinish() {

		if (Report.should_report(Report.ACTIVITY, 5)) {
			Report.report(5, PoolRunner.logString() + " " + this
					+ " enters stopfinish ");
		}
		finishState_.waitForFinish();
		if (Report.should_report(Report.ACTIVITY, 5)) {
			Report.report(5, PoolRunner.logString() + " " + this
					+ " continues stopfinish ");
		}
		// NOTE SYNCHRONIZATION : It is not necessary to synchronize this code as being
		// here implies all spawned activity are terminated.
		
		FinishState state = finishState_;
		// Update finishState_ before throwing an exception.
		if (finishStack_ == null){
			finishState_ = null;
			//else we are sure finishStack_ is defined != null
		} else if (!finishStack_.isEmpty()){
			finishState_ = (FinishState) this.finishStack_.pop();
		}
		
		// END NOTE SYNCHRONIZATION
		// Do not reference finishState_ below, instead reference state.

		Stack result = state.exceptions();

		if ((result != null) && (!result.empty())){
			if (result.size() == 1) {
				Throwable t = (Throwable) result.pop();
				if (Report.should_report(Report.ACTIVITY, 3)) {
					Report.report(3, PoolRunner.logString() + " " + this
							+ " throws  " + t);
				}
				if (t instanceof java.lang.Error)
					throw (Error) t;
				if (t instanceof java.lang.RuntimeException)
					throw (RuntimeException) t;
			}
			throw new MultipleExceptions(result);
		}
		if (Report.should_report(Report.ACTIVITY, 3)) {
			Report.report(3, PoolRunner.logString() + " " + this
					+ " exits from finish.");
		}
	}
	
	/**
	 * Stack must be only access throught this method that lazily creates it.
	 * @return a new stack if non has been ever created.
	 */
	private Stack getFinishStack()
	{
		if (this.finishStack_ == null)
			this.finishStack_ = new Stack();
		
		return this.finishStack_;		
	}

	/** Is this activity currently executing a finish?
	 * @thread myThread
	 * @return true iff the activity is executing a finish.
	 */
	private boolean inFinish() {
		return finishState_ != null;
	}

	/**
	 * Execute this activity as if it is executed within a finish. 
	 * Thus it throws an exception iff this activity or some activity asynchronously
	 * spawned by it (transitively, and not within the scope of another finish)
	 * throws an exception.
	 * @thread myThread
	 * @param r The runnable activity to run
	 */
	public void runWithinFinish(Runnable r) {
		try {
			this.startFinish();
			assert (r != null);
			r.run();
		} catch (Throwable t) {
			this.pushException(t);
		}
		this.stopFinish();
	}

	/** Push the exception thrown while executing s in a finish s, onto the finish state.
	 * 
	 * @param t
	 */
	public/*myThread*/void pushException(Throwable t) {
		if (Report.should_report(Report.ACTIVITY, 3)) {
			Report.report(3, PoolRunner.logString() + " " + this
					+ " pushing exception " + t + " onto " + finishState_);
		}
		finishState_.pushException(t);
	}
	
	
	/********** CLOCK MANAGER DELEGATION **********/
	
	
	/* (non-Javadoc)
	 * @see x10.runtime.clock.ClockManager#addClock(x10.runtime.Clock)
	 * @thread myThread
	 */
	public void addClock(Clock c) {
		if(this.activityClockManager != null)
			this.activityClockManager.addClock(c);
		else 
			this.activityClockManager = ClockManagerFactory.getClockManager(this,c);
	}
	
	/* (non-Javadoc)
	 * @see x10.runtime.clock.ClockManager#dropClock(x10.runtime.Clock)
	 * @thread myThread
	 */
	public void dropClock(Clock c) {
		if(this.activityClockManager != null)
		{
			this.activityClockManager.dropClock(c);
			if (this.activityClockManager.getNbRegisteredClocks() == 0)
				this.activityClockManager = null;
		} else {
		if (Report.should_report("clock", 3)) {
			Report.report(3, PoolRunner.logString() + " " + this + 
					" dropClock attempt failed because no clocks are registered in this activity "
					+ c + ".");
		}
		}
	}

	/* (non-Javadoc)
	 * @see x10.runtime.clock.ClockManager#dropAllClocks()
	 * @thread myThread
	 */
	protected void dropAllClocks() {
		if(this.activityClockManager != null) {
			this.activityClockManager.dropAllClocks();
			this.activityClockManager = null;
		} else {
			if (Report.should_report("clock", 3)) {
				Report.report(3, PoolRunner.logString() + 
						" dropAllClock attempt failed because no clocks are registered in this activity ");
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see x10.runtime.clock.ClockManager#doNext()
	 * @thread myThread
	 */
	public void doNext() {
		if(this.activityClockManager != null)
			this.activityClockManager.doNext();
		else {
			if (Report.should_report("clock", 3)) {
				Report.report(3, PoolRunner.logString() + 
						" doNext attempt failed because no clocks are registered in this activity ");
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see x10.runtime.clock.ClockManager#checkClockUse(x10.runtime.Clock)
	 * @thread myThread
	 */
	public Clock checkClockUse(Clock c) {
		if (c.dropped())
			throw new ClockUseException("Cannot transmit dropped clock.");
		if (c.quiescent())
			throw new ClockUseException("Cannot transmit resumed clock.");
		if (this.inFinish())
			throw new ClockUseException("Finish cannot spawn clocked async.");
		return c;
	}

	/* (non-Javadoc)
	 * @see x10.runtime.clock.ClockManager#getNbRegisteredClocks
	 */
	public int getNbRegisteredClocks() {
	if (this.activityClockManager == null)
		return 0;
	else
		return this.activityClockManager.getNbRegisteredClocks();
	}
	
	/* (non-Javadoc)
	 * @see x10.runtime.clock.ClockManager#registerClocks()
	 */
	public void registerClocks() {
		if(this.activityClockManager != null) {
			this.activityClockManager.registerClocks();
		}
	}
	
	
	/********** ABSTRACT METRICS MANAGER DELEGATION **********/
	
	
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#getTotalOps()
	 */
	public long getTotalOps() { return this.abstractMetricsManager.getTotalOps(); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#getCritPathOps()
	 */
	public long getCritPathOps() { return this.abstractMetricsManager.getCritPathOps(); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#addLocalOps(long)
	 */
	public void addLocalOps(long n) { this.abstractMetricsManager.addLocalOps(n); }
		
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#maxCritPathOps(long)
	 */
	public void maxCritPathOps(long n) {this.abstractMetricsManager.maxCritPathOps(n);}

	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#getTotalUnblockedTime()
	 */
	public long getTotalUnblockedTime() { return this.abstractMetricsManager.getTotalUnblockedTime(); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#getCritPathTime()
	 */
	public long getCritPathTime() { return this.abstractMetricsManager.getCritPathTime(); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#maxCritPathTime(long)
	 */
	public void maxCritPathTime(long t) { this.abstractMetricsManager.maxCritPathTime(t); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#getResumeTime()
	 */
	public long getResumeTime() { return this.abstractMetricsManager.getResumeTime(); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#setResumeTime()
	 */
	public void setResumeTime() { this.abstractMetricsManager.setResumeTime(); }

	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#updateIdealTime()
	 */
	public void updateIdealTime() {
		this.abstractMetricsManager.updateIdealTime();
	}

	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#getCurrentTime()
	 */
	public long getCurrentTime() { return this.abstractMetricsManager.getCurrentTime(); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#addUnblockedTime(long)
	 */
	public void addUnblockedTime(long t) {
		this.abstractMetricsManager.addUnblockedTime(t);
	}
	

	/********** ACTIVITY SPAWNING TERMINATION **********/

	
	/** This activity has spawned child. Properly link child
	 * into the finish termination chain and notify all listeners.
	 * @thread myThread
	 * @param child -- the activity being spawned.
	 */
	public Activity finalizeActivitySpawn(final Activity child) {
		if (Report.should_report(Report.ACTIVITY, 3)) {
			Report.report(3, PoolRunner.logString() + " " + this + " spawns "
					+ child);
		}
		FinishState target = finishState_ == null ? rootNode_ : finishState_;
		child.setRootActivityFinishState(target);
		target.notifySubActivitySpawn();
		return child;
	}

	/**
	 * This activity has terminated normally. Now clean up. (Notify the finish ancestor,
	 * if any, and any other listeners (e.g. Sample listeners).
	 * @thread myThread
	 */
	public void finalizeTermination() {
		if (Report.should_report(Report.ACTIVITY, 5)) {
			Report.report(5, PoolRunner.logString() + " " + this
					+ "terminates.");
		}
		finalizeTerminationCleanup();
                if (rootNode_ != null)
                   rootNode_.notifySubActivityTermination();
	}

	/**
	 * This activity has terminated abruptly. Now clean up. (Notify the finish ancestor, if any, and
	 * any other listeners (e.g. Sample listeners).
	 * @thread myThread
	 * @param t -- the reason for the abrupt termination.
	 */
	public void finalizeTermination(Throwable t) {
		if (Report.should_report(Report.ACTIVITY, 5)) {
			Report.report(5, Thread.currentThread() + " " + this
					+ " terminates abruptly with " + t);
		}
		finalizeTerminationCleanup();
		if (rootNode_ != null)
			rootNode_.notifySubActivityTermination(t);
	}
	
	/**
	 * Helper method called by finalizeTermination() and finalizeTermination(t)
	 * @thread myThread
	 */
	public void finalizeTerminationCleanup() {
		if (JITTimeConstants.ABSTRACT_EXECUTION_STATS) {
		    x10.lang.Runtime.here().maxCritPathOps(getCritPathOps());
		    x10.lang.Runtime.here().addLocalOps(getTotalOps());
		    
		    if (JITTimeConstants.ABSTRACT_EXECUTION_TIMES) {
				updateIdealTime();
				x10.lang.Runtime.here().addUnblockedTime(getTotalUnblockedTime());
				x10.lang.Runtime.here().maxCritPathTime(getCritPathTime());
			}
		}
		dropAllClocks();
		if (Report.should_report(Report.ACTIVITY, 5)) {
			Report.report(5, Thread.currentThread() + " " + this
					+ " drops clocks, has rootNode_ " + rootNode_);
		}
	}

	
	/********** UTILS METHODS **********/
	
	
	/** The short name for the activity. Used in logging.
	 * 
	 * @return -- the short name for the activity.
	 */
	public String myName() {
           return "Activity " + Long.toHexString(hashCode());
	}

	/** A long descriptor for the activity. By default displays the finishState_ and the rootNode_.
	 * 
	 */
	public String toString() {
		String rv = "<" + myName();
		
		rv = rv + " " + finishState_ + "," + rootNode_;
		rv = rv + ">";
		return rv;
	}

	/** 
	 * A short descriptor for the activity.
	 */
	public String shortString() {
		return "<" + myName() + ">";
	}

	/**
	 * An activity used to implement an X10 future.
	 */
	public static abstract class Expr extends Activity {

		public Future_c future;

		/**
		 * Wait for the completion of this activity and return the
		 * return value.
		 */
		public abstract x10.lang.Object getResult();

		public abstract void runSource();

		public void run() {
			try {
				try {
					startFinish();
					runSource();
				} catch (Throwable t) {
					try {
						pushException(t);
						stopFinish(); // must throw an exception.
					} catch (Throwable t1) {
						// Now nested asyncs have terminated.
						future.setException(t1);
						return;
					}
				}
				stopFinish(); // this may throw an exception if a nested async did.
				// Normal termination.
                                future.setResult(getResult());
			} catch (Throwable t) {
				// Now nested asyncs have terminated.
				future.setException(t);
			}
		}

		public void finalizeTermination() {
			super.finalizeTermination();
		}

		public void finalizeTermination(Throwable t) {
                        future.setException(t);
			super.finalizeTermination(t);
		}

	} // end of Activity.Expr

} // end of Activity
