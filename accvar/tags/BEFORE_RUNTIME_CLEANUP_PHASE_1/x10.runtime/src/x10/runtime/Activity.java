package x10.runtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import x10.lang.ClockUseException;
import x10.lang.MultipleExceptions;

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
 * the method is the one executing the activity, and the thread can inoke this method only before
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
public abstract class Activity implements Runnable {

	/**
	 * The place on which this activity runs. 
	 * 
	 */
	protected Place place_;

	protected List clocks_;

	protected FinishState finishState_ = null;

	protected Stack finishStack_ = new Stack();

	protected boolean addPoolCalled = false;

	/**
	 * Exception collector for this activity. This is field is used by the default
	 * runtime to manage activities.  The stack is a stack of Throwables.
	 */

	/**
	 * The FinishState up in the invocation tree within who's finish
	 * this activity is being executed.
	 */
	protected FinishState rootNode_;

	/**
	 * Create an activity with the given set of clocks.  
	 * @param clocks
	 */
	public Activity(List clocks) {
		assert (clocks != null);
		if (!clocks.isEmpty()) {
			if (Report.should_report("activity", 3)) {
				Report.report(3, PoolRunner.logString() + " adding clocks "
						+ clocks + " to " + this);
			}
		}
		this.clocks_ = clocks;
	}

	/**
	 * Create an activity with the given clock.  
	 * @param clock
	 */
	public Activity(Clock clock) {
		assert (clock != null);
		this.clocks_ = new LinkedList();
		if (Report.should_report("activity", 3)) {
			Report.report(3, PoolRunner.logString() + " adding clock "
					+ clock + " to " + this);
		}
		this.clocks_.add(clock);
	}

	public Activity() {
		this.clocks_ = new LinkedList();
	}

	/**
	 * This activity is about to be executed. 
	 * Perform whatever initialization is necessary,
	 * informing whoever is interested. Must be performed by the thread
	 * that will spawn this activity onto another thread.
	 *
	 */
	public/*mySpawningThread*/void initializeActivity() {
		if (Report.should_report("activity", 5)) {
			Report.report(5, PoolRunner.logString()
					+ " Activity: initializing " + this);
		}

		Iterator it = clocks_.iterator();
		while (it.hasNext()) {
			Clock c = (Clock) it.next();
			c.register(this);
		}
		
		if (JITTimeConstants.ABSTRACT_EXECUTION_TIMES) 
			// Record time at which activity was started
			setResumeTime();
	}

	public/*mySpawningThread*/void setRootActivityFinishState(FinishState root) {
		this.rootNode_ = root;
	}

	public/*mySpawningThread*/void setPlace(Place p) {
		this.place_ = p;
	}

	public Place getPlace() {
		return place_;
	}

	/**
	 * Start executing a finish operation.
	 *
	 */
	public/*myThread*/void startFinish() {
		if (finishState_ != null) {
			finishStack_.push(finishState_);
		}
		finishState_ = new FinishState(this);

		if (Report.should_report("activity", 3)) {
			Report.report(3, PoolRunner.logString() + " " + this
					+ " starts finish " + finishState_);
		}

	}

	/** Push the exception thrown while executing s in a finish s, onto the finish state.
	 * 
	 * @param t
	 */
	public/*myThread*/void pushException(Throwable t) {
		if (Report.should_report("activity", 3)) {
			Report.report(3, PoolRunner.logString() + " " + this
					+ " pushing exception " + t + " onto " + finishState_);
		}

		finishState_.pushException(t);
	}

	/**
	 * Suspend until all activities spawned during this finish 
	 * operation have terminated. Throw an exception if any
	 * async terminated abruptly. Otherwise continue normally.
	 * Should only be called by the thread executing the current activity.
	 */
	public void stopFinish() {

		if (Report.should_report("activity", 5)) {
			Report.report(5, PoolRunner.logString() + " " + this
					+ " enters stopfinish ");
		}
		finishState_.waitForFinish();
		if (Report.should_report("activity", 5)) {
			Report.report(5, PoolRunner.logString() + " " + this
					+ " continues stopfinish ");
		}
		FinishState state = null;

		synchronized (this) {
			state = finishState_;
			// Update finishState_ before throwing an exception.
			if (finishStack_.empty()) {
				finishState_ = null;
			} else {
				finishState_ = (FinishState) finishStack_.pop();
			}
		}
		// Do not reference finishState_ below, instead reference state.

		Stack result = state.exceptions();
		if (!result.empty()) {
			if (result.size() == 1) {
				Throwable t = (Throwable) result.pop();
				if (Report.should_report("activity", 3)) {
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
		if (Report.should_report("activity", 3)) {
			Report.report(3, PoolRunner.logString() + " " + this
					+ " exits from finish.");
		}

	}

	/** Is this activity currently executing a finish?
	 * 
	 * @return true iff the activity is executing a finish.
	 */
	public/*myThread*/boolean inFinish() {
		return finishState_ != null;
	}

	/** Check whether it is ok to use the given clock c (by spawning an async
	 * registered on the clock), throwing a ClockUseException if it is not.
	 * Checks that the clock has not been resumed or dropped, and that the 
	 * async is not being spawned inside a finish.
	 * Invoked from code generated from the X10 source by the translator.
	 * @param c -- The clock being checked for.
	 */
	public/*myThread*/Clock checkClockUse(Clock c) {
		if (c.dropped())
			throw new ClockUseException("Cannot transmit dropped clock.");
		if (c.quiescent())
			throw new ClockUseException("Cannot transmit resumed clock.");
		if (inFinish())
			throw new ClockUseException("Finish cannot spawn clocked async.");
		return c;
	}

	/**
	 * Execute this activity as if it is executed within a finish. 
	 * Thus it throws an exception iff this activity or some activity asynchronously
	 * spawned by it (transitively, and not within the scope of another finish)
	 * throws an exception.
	 *
	 */
	public/*myThread*/void finishRun() {
		if (Report.should_report("activity", 5)) {
			Report.report(5, PoolRunner.logString() + " " + this
					+ ".finishRun() started.");
		}

		finishRun(this);
	}

	public/*myThread*/void finishRun(Runnable r) {

		try {
			startFinish();
			assert (r != null);
			r.run();
		} catch (Throwable t) {
			//System.out.println(x10.lang.Runtime.here()+":in finish run exception:"+t);
			pushException(t);
		}
		stopFinish();

	}

	/** Add a clock to this activity's clock list. (Called when
	 * this activity creates a new clock.)
	 * 
	 * @param c
	 */
	public/*myThread*/void addClock(Clock c) {
		if (Report.should_report("clock", 3)) {
			Report.report(3, PoolRunner.logString() + " " + this + " adds " + c
					+ ".");
		}
		clocks_.add(c);
	}

	/**
	 * Drop a clock from this activity's clock list.
	 * @param c
	 */
	public/*myThread*/void dropClock(Clock c) {
		if (Report.should_report("clock", 3)) {
			Report.report(3, PoolRunner.logString() + " " + this + " drops "
					+ c + ".");
		}
		clocks_.remove(c);
	}

	/**
	 * Drop all clocks associated with this activity, and deregister this
	 * activity from all these clocks.
	 *
	 */
	protected/*myThread*/void dropAllClocks() {
		for (Iterator it = clocks_.iterator(); it.hasNext();) {
			Clock c = (Clock) it.next();
			c.drop(this);
		}
		if (Report.should_report("clock", 3)) {
			Report.report(3, PoolRunner.logString() + " " + this
					+ " drops all clocks.");
		}
	}

	/**
	 * Implement next; for an activity. Blocks until each clock 
	 * this activity is registered on has moved to the next phase.
	 *
	 */
	public/*myThread*/void doNext() {
		if (Report.should_report("activity", 3)) {
			Report.report(3, PoolRunner.logString() + " " + this
					+ ".doNext() on " + clocks_);
		}
		Iterator it = clocks_.iterator();
		while (it.hasNext()) {

			Clock c = (Clock) it.next();
			c.resume();
		}

		it = clocks_.iterator();
		while (it.hasNext()) {
			Clock c = (Clock) it.next();
			c.doNext();
		}
	}

	ArrayList asl_ = new ArrayList();

	/** This activity has spawned child. Properly link child
	 * into the finish termination chain and notify all listeners.
	 * 
	 * @param child -- the activity being spawned.
	 */
	public/*myThread*/Activity finalizeActivitySpawn(final Activity child) {
		if (Report.should_report("activity", 3)) {
			Report.report(3, PoolRunner.logString() + " " + this + " spawns "
					+ child);
		}
		FinishState target = finishState_ == null ? rootNode_ : finishState_;
		child.setRootActivityFinishState(target);
		target.notifySubActivitySpawn();
		ArrayList myASL = null;
		synchronized (this) {
			myASL = asl_;
		}
		if (myASL != null) {
			for (int j = 0; j < myASL.size(); j++) {
				// tell other activities that want to know that this has spawned child
				ActivitySpawnListener asl = (ActivitySpawnListener) myASL
						.get(j);
				asl.notifyActivitySpawn(child, this);
			}
		}
		return child;
	}

	/** Register an activity spawn listener for this activity.
	 * 
	 * @param a
	 */
	public synchronized void registerActivitySpawnListener(
			ActivitySpawnListener a) {
		if (asl_ == null) {
			asl_ = new ArrayList(2);
		}
		asl_.add(a);
	}

	/**
	 * This activity has terminated normally. Now clean up. (Notify the finish ancestor,
	 * if any, and any other listeners (e.g. Sample listeners).
	 *
	 */
	public/*myThread*/void finalizeTermination() {
		if (Report.should_report("activity", 5)) {
			Report.report(5, PoolRunner.logString() + " " + this
					+ "terminates.");
		}
		finalizeTerminationCleanup();
                if (rootNode_ != null)
                   rootNode_.notifySubActivityTermination();
                if (asl_ != null) {
                   for (int j = 0; j < asl_.size(); j++) {
                      // tell other activities that want to know that this has spawned child
                      ActivitySpawnListener asl = (ActivitySpawnListener) asl_
                         .get(j);
                      asl.notifyActivityTerminated(this);
                   }
                }
	}

	/**
	 * This activity has terminated abruptly. Now clean up. (Notify the finish ancestor, if any, and
	 * any other listeners (e.g. Sample listeners).
	 * @param t -- the reason for the abrupt termination.
	 */

	public/*myThread*/void finalizeTermination(Throwable t) {
		if (Report.should_report("activity", 5)) {
			Report.report(5, Thread.currentThread() + " " + this
					+ " terminates abruptly with " + t);
		}
		finalizeTerminationCleanup();
		if (rootNode_ != null)
			rootNode_.notifySubActivityTermination(t);
		if (asl_ != null) {
			for (int j = 0; j < asl_.size(); j++) {
				// tell other activities that want to know that this has spawned child
				ActivitySpawnListener asl = (ActivitySpawnListener) asl_.get(j);
				asl.notifyActivityTerminated(this);
			}
		}
	}
	
	/**
	 * Helper method called by finalizeTermination() and finalizeTermination(t)
	 */

	public/*myThread*/void finalizeTerminationCleanup() {
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
		if (Report.should_report("activity", 5)) {
			Report.report(5, Thread.currentThread() + " " + this
					+ " drops clocks, has rootNode_ " + rootNode_);
		}
	}


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

	/* A short descriptor for the activity.
	 * 
	 */
	public String shortString() {
		return "<" + myName() + ">";
	}

	/**
	 * Set AddPoolCalled boolean Flag ( is this activity has added a thread already into the pool )
	 */
	public void setAddPoolCalled() {
		addPoolCalled = true;
	}

	/**
	 * Get AddPoolCalled boolean flag ( if this activity has added a thread already or not )
	 */
	public boolean getAddPoolCalled() {
		return addPoolCalled;
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
	
	synchronized public void addLocalOps(long n) { totalOps += n; critPathOps += n; }
	
	synchronized public void maxCritPathOps(long n) { critPathOps = Math.max(critPathOps, n); }
	
	/*
	 * totalTime, critPathTime, and resumeTime keep tracks of actual unblocked execution in each activity.  The time that an activity is spent blocked
	 * in the X10 runtime is not counted.  However, this is still an approximate estimate because it does account for time that an activity is not
	 * executing because its Java thread in the thread pool does not have an available processor.
	 */
	private long totalUnblockedTime = 0; // Total unblocked time done by this activity (in milliseconds)
	private long critPathTime = 0; // Critical path length for this activity, including dependences due to child activities (in milliseconds)
	private long resumeTime = 0; // Time at which activity was started or unblocked (whichever is most recent)
	
	synchronized public long getTotalUnblockedTime() { return totalUnblockedTime; }
	
	synchronized public long getCritPathTime() { return critPathTime; }
	
	synchronized public void maxCritPathTime(long t) { critPathTime = Math.max(critPathTime, t); }
	
	synchronized public long getResumeTime() { return resumeTime; }
	
	synchronized public void setResumeTime() { resumeTime = getCurrentTime(); }

	synchronized public void updateIdealTime() {
		long delta = getCurrentTime() - getResumeTime();
		totalUnblockedTime += delta;
		critPathTime += delta;
	}
	
	// Use System.currentTimeMillis() for now to measure ideal execution time.
	// This can be changed in the future
	public long getCurrentTime() { return System.currentTimeMillis(); }
	
	/**
	 * End of code to support abstract execution model
	 */

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
