/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

import java.util.List;
import java.util.Stack;

import x10.core.MultipleExceptions;
import x10.runtime.abstractmetrics.AbstractMetrics;
import x10.runtime.abstractmetrics.AbstractMetricsFactory;
import x10.runtime.clock.ClockManager;
import x10.runtime.clock.ClockManagerFactory;
import x10.runtime.clock.ClockUseException;

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
public abstract class Activity implements X10Runnable, AbstractMetrics {

	/** The place on which this activity runs. **/ 
	protected Place place_;

	/** The FinishState of this activity. 
	 * != null If this activity as started a finish **/
	protected FinishState finishState_ = null;

	/** The finishStack is lazily created **/
	protected Stack<FinishState> finishStack_ = null;
	
	/** Manage all clock of this activity.
	 * The clockManager is lazily created **/
	protected ClockManager activityClockManager;
	
	/**
	 * The FinishState up in the invocation tree within
	 * this activity is being executed.
	 */
	protected FinishState rootNode_;

	/**
	 * Abstract Metrics performance manager, only created if
	 * JITTimeConstants.ABSTRACT_EXECUTION_STATS is set to true
	 */
	private AbstractMetrics abstractMetricsManager;

	/**
	 * Until the activity has terminate, we consider it is not finished
	 */
	private boolean notFinished = true;

	private InvocationStrategy invocationStrategy = InvocationStrategy.ASYNC;

	private final String name;

	/********** ACTIVITY CREATION AND INITIALIZATION **********/

	/**
	 * Create an activity.
	 * @thread mySpawningThread
	 */
	public Activity() {
	    this(new Throwable().getStackTrace()[2].toString());
	}

	/**
	 * Create an activity.
	 * @thread mySpawningThread
	 * @param name
	 */
	public Activity(String name) {
	    this.name= name;
	    if (VMInterface.ABSTRACT_EXECUTION_STATS)
		this.abstractMetricsManager = AbstractMetricsFactory.getAbstractMetricsManager();
	    this.initializeActivity();
	}

	/**
	 * Create an activity with the given list of clocks.
	 * @thread mySpawningThread  
	 * @param clocks
	 * @param name
	 */
	public Activity(List clocks, String name) {
	    this.name= name;
	    if (VMInterface.ABSTRACT_EXECUTION_STATS)
		this.abstractMetricsManager = AbstractMetricsFactory.getAbstractMetricsManager();
	    this.activityClockManager = ClockManagerFactory.getClockManager(this, clocks);
	    this.initializeActivity();
	}

	/**
	 * Create an activity with the given list of clocks.
	 * @thread mySpawningThread  
	 * @param clocks
	 */
	public Activity(List clocks) {
	    this(clocks, new Throwable().getStackTrace()[2].toString());
	}

	/**
	 * Create an activity with the given clock.
	 * @thread mySpawningThread  
	 * @param clock
	 * @param name
	 */
	public Activity(Clock clock, String name) {
	    this.name= name;
	    if (VMInterface.ABSTRACT_EXECUTION_STATS)
		this.abstractMetricsManager = AbstractMetricsFactory.getAbstractMetricsManager();
	    this.activityClockManager = ClockManagerFactory.getClockManager(this, clock);
	    this.initializeActivity();
	}

	/**
	 * Create an activity with the given clock.
	 * @thread mySpawningThread  
	 * @param clock
	 */
	public Activity(Clock clock) {
	    this(clock, new Throwable().getStackTrace()[2].toString());
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
		
		if (VMInterface.ABSTRACT_EXECUTION_TIMES)
			// Record time at which activity was started
			setResumeTime();
	}
	
	/**
	 * Default implementation of the Runnable interface.
	 * An Activity executing in a place should always be invoked using it's runnable interface,
	 * and not directly by calling it's runX10Task method; which is an abstract method in this class.
	 *  This run method allows performing actions before and after activity execution allowing
	 *  to submit the activity safely as regard to runtime and pool thread.
	 */
	public void run() {
		PoolRunner activityRunner = (PoolRunner) Thread.currentThread();
		if (Report.should_report(Report.ACTIVITY, 5)) {
			Report.report(5, activityRunner + " is running " + this);
		}
		
		// Binding current thread to this activity
		activityRunner.setActivity(this);
		// TODO Remove this next line later <-- who wrote this comment ?
 		// binding current thread to the activity place
		activityRunner.setPlace(this.getPlace());
		try { 
		this.invocationStrategy.invokeX10Task(this);
		} catch (Throwable t) {
			if (t instanceof java.lang.Error) {
				throw (Error) t;
			}
			if (t instanceof java.lang.RuntimeException) {		
				throw (RuntimeException) t;
			}
		} finally {
			// this activity has terminated, updating its status.
			this.setFinished();
		}
	}

	/**
	 * Implemented by X10 activities, actual activity "user" code 
	 * generated by xcd templates.
	 */
	public abstract void runX10Task() throws Throwable;
	
	/**
	 * Waits for this activity to be fully executed by the X10 runtime.
	 * @throws InterruptedException
	 */
	public synchronized void join() throws InterruptedException {
		while (this.notFinished) {
			this.wait();
		}
	}
	
	/**
	 * Set activity's current state to finished.
	 */
	private synchronized void setFinished() {
		this.notFinished = false;
		this.notifyAll();
	}
	
	/**
	 * Set the root FinishState of this activity.
	 * i.e. the first finish state to report in the invocation tree 
	 * when this activity crash or finish 
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

	/**
	 * Get the place where activity is currently executing.
	 * @return current Place.
	 */
	public Place getPlace() {
		return place_;
	}
	
	
	/********** FINISH ACTIVITY MANAGEMENT **********/
	
	
	/**
	 * Start executing this activity synchronously 
	 * (i.e. within a finish statement).
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
				if (t instanceof java.lang.Error) {
					throw (Error) t;
				}
				if (t instanceof java.lang.RuntimeException) {		
					throw (RuntimeException) t;
				}
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
	private Stack<FinishState> getFinishStack()
	{
		if (this.finishStack_ == null)
			this.finishStack_ = new Stack<FinishState>();
		
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
	 * Push the exception thrown while executing s in a finish s, 
	 * onto the finish state.
	 * @param t the raised exception
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
			if (Report.should_report(Report.CLOCK, 3)) {
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
			if (Report.should_report(Report.CLOCK, 3)) {
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
			if (Report.should_report(Report.CLOCK, 3)) {
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
	public long getTotalOps() {
		if (VMInterface.ABSTRACT_EXECUTION_STATS)
			return this.abstractMetricsManager.getTotalOps();
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#getCritPathOps()
	 */
	public long getCritPathOps() {
		if (VMInterface.ABSTRACT_EXECUTION_STATS)
			return this.abstractMetricsManager.getCritPathOps();
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#addLocalOps(long)
	 */
	public void addLocalOps(long n) {
		if (VMInterface.ABSTRACT_EXECUTION_STATS)
			this.abstractMetricsManager.addLocalOps(n);
	}
		
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#addLocalOps(long)
	 */
	public void addCritPathOps(long n) {
		if (VMInterface.ABSTRACT_EXECUTION_STATS)
			this.abstractMetricsManager.addCritPathOps(n);
	}
	
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#maxCritPathOps(long)
	 */
	public void maxCritPathOps(long n) {
		if (VMInterface.ABSTRACT_EXECUTION_STATS)
			this.abstractMetricsManager.maxCritPathOps(n);
	}

	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#getTotalUnblockedTime()
	 */
	public long getTotalUnblockedTime() {
		if (VMInterface.ABSTRACT_EXECUTION_STATS)
			return this.abstractMetricsManager.getTotalUnblockedTime();
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#getCritPathTime()
	 */
	public long getCritPathTime() {
		if (VMInterface.ABSTRACT_EXECUTION_STATS)
			return this.abstractMetricsManager.getCritPathTime();
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#maxCritPathTime(long)
	 */
	public void maxCritPathTime(long t) {
		if (VMInterface.ABSTRACT_EXECUTION_STATS)
			this.abstractMetricsManager.maxCritPathTime(t);
	}
	
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#getResumeTime()
	 */ 
	public long getResumeTime() {
		if (VMInterface.ABSTRACT_EXECUTION_STATS)
			return this.abstractMetricsManager.getResumeTime();
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#setResumeTime()
	 */
	public void setResumeTime() {
		if (VMInterface.ABSTRACT_EXECUTION_STATS)
			this.abstractMetricsManager.setResumeTime();
	}

	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#updateIdealTime()
	 */
	public void updateIdealTime() {
		if (VMInterface.ABSTRACT_EXECUTION_STATS)
			this.abstractMetricsManager.updateIdealTime();
	}

	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#getCurrentTime()
	 */
	public long getCurrentTime() {
		if (VMInterface.ABSTRACT_EXECUTION_STATS)
			return this.abstractMetricsManager.getCurrentTime();
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see x10.runtime.AbstractMetrics#addUnblockedTime(long)
	 */
	public void addUnblockedTime(long t) {
		if (VMInterface.ABSTRACT_EXECUTION_STATS)
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
		
		// if current activity is within a finish then finishState_ != null
		// and the child activity must refer to the finishState
		
		// Otherwise current activity is not directly in a finish thus child activity
		// should report to the first common activity ancestor in a finish (i.e the rootNode)
		FinishState target = finishState_ == null ? rootNode_ : finishState_;
		
		// set where the child should refer when is activity is over
		// i.e. the first finish when going ip in the activation tree 
		child.setRootActivityFinishState(target);
		
		// increment the number of spawned activities in the parent
		target.notifySubActivitySpawn();
		if (VMInterface.ABSTRACT_EXECUTION_STATS) {
			// Initialize this activity's critical path time to that of its parent
			child.maxCritPathOps(getCritPathOps());
		}
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
		// drop all clocks registered with this activity
		finalizeTerminationCleanup();
        if (rootNode_ != null)
           rootNode_.notifySubActivityTermination();
//        this.setFinished();
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
//        this.setFinished();
	}
	
	/**
	 * Helper method called by finalizeTermination() and finalizeTermination(t)
	 * @thread myThread
	 */
	public void finalizeTerminationCleanup() {
		if (VMInterface.ABSTRACT_EXECUTION_STATS) {
		    x10.runtime.Runtime.here().maxCritPathOps(getCritPathOps());
		    x10.runtime.Runtime.here().addLocalOps(getTotalOps());
		    
		    if (VMInterface.ABSTRACT_EXECUTION_TIMES) {
				updateIdealTime();
				x10.runtime.Runtime.here().addUnblockedTime(getTotalUnblockedTime());
				x10.runtime.Runtime.here().maxCritPathTime(getCritPathTime());
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
           return name != null ? name : ("Activity " + Long.toHexString(System.identityHashCode(this)));
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

	public void setInvocationStrategy(InvocationStrategy async_in_finish) {
		this.invocationStrategy = async_in_finish;
	}

} // end of Activity
