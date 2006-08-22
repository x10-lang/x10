package x10.runtime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import x10.cluster.Debug;
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
 * @author xinb
 */
public abstract class Activity implements Runnable/*, Serializable*/ {

    /**
     * The place on which this activity runs. 
     * 
     */
    protected Place place_ ;
    protected List clocks_;
    protected FinishStateOps finishState_ = null;
    protected Stack finishStack_ = new Stack();
    /**
     * Exception collector for this activity. This is field is used by the default
     * runtime to manage activities.  The stack is a stack of Throwables.
     */
   
    /**
     * The FinishState up in the invocation tree within who's finish
     * this activity is being executed.
     */
    protected FinishStateOps rootNode_;
    
    /**
     * Whether this activity is spawned from <code>rootNode_</code>'s home node.
     */
    private boolean fromRoot = false;

    /** Create an activity with the given set of clocks.  
     * 
     * @param clocks
     */
    public Activity( List clocks) {
	//assert(clocks!=null); //cluster: accommodate reflective calls of anonymous subclass
    	if (clocks!= null && ! clocks.isEmpty()) {
    		if (Report.should_report("activity", 3)) {
    			Report.report(3, PoolRunner.logString() + " adding clocks " + clocks + " to " + this);
    		}
    	}
    	this.clocks_ = clocks;
    }
    
    /**
	 * Create an activity with the given clock.
	 * @thread mySpawningThread  
	 * @param clock
	 */
	public Activity(Clock clock) {
		this.clocks_ = new LinkedList();
		if(clock != null) 
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
    public /*mySpawningThread*/ void initializeActivity() {
    	if (Report.should_report("activity", 5)) {
    		Report.report(5, PoolRunner.logString() + " Activity: initializing " + this);
    	}
    	
    	Iterator it = clocks_.iterator();
    	while (it.hasNext()) {
    		Clock c = (Clock) it.next(); 
    		c.register( this );
    	}
    	
    }

    /**
     * Register clocks with the activity, in Cluster x10.  
     * Called from child Activity context after X10Serializer.deserilizeAsync
     * The other half: 
     * 	Activity is registered with the clock during serialization of "clocks_" field.
     * @author xinb
     */
    public void registerWithClock() {
    	if(Report.should_report("cluster", Debug.comm))
    		Report.report(Debug.comm, "Activity.registerWithClock ");
    	
    	for(Iterator it = clocks_.iterator(); it.hasNext();) {
    		Clock c = ((Clock)it.next());
    		synchronized(c) {
    			if(c.activityCount() == 0)
    				c.addChild(this);

    			c.register(this);
    		}
    	}
    }
    /**
     * Increase parent's child count for the clock passed to child activity. 
     * Called from parent Activity context, right before a child is shipped away,
     * replacing call to LocalPlace_c.prepareActivity
     * @author xinb
     */
    public void initRemoteActivity(Activity child) {
    	/*
    	if(Report.should_report("cluster", Debug.comm))
    		Report.report(Debug.comm, "Activity.initRemoteActivity>>> addChild to: "+child.clocks_);
    	
    	Iterator it = child.clocks_.iterator();
    	while (it.hasNext()) {
    		Clock c = (Clock) it.next(); 
    		c.addChild(child);
    	}*/
    }
    
    public /*mySpawningThread*/ void setRootActivityFinishState (FinishStateOps root ) {    	
    	this.rootNode_ = root;
		if(root.notShadow())
			fromRoot = true;
    }
    
    public /*mySpawningThread*/ void setPlace(Place p) {
    	this.place_ = p;
    }
    
    public Place getPlace() {
    	return place_;
    }
    /**
     * Start executing a finish operation.
     *
     */
    public /*myThread*/ void startFinish() {
    	if (finishState_ != null) {
    		finishStack_.push(finishState_);
    	}
    	finishState_=new FinishState(this);  
    	if (Report.should_report("activity", 3)) {
    		Report.report(3, PoolRunner.logString() + " " + this + " starts finish " + finishState_);
    	}
    }
       
    /** Push the exception thrown while executing s in a finish s, onto the finish state.
     * 
     * @param t
     */
    public /*myThread*/ void pushException(Throwable t) {
    	if (Report.should_report("activity", 3)) {
    		Report.report(3, PoolRunner.logString() + " " + this + " pushing exception " + t + " onto " + finishState_);
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
    		Report.report(5, PoolRunner.logString() + " " + this + " enters stopfinish ");
    	}
    	try { finishState_.waitForFinish(); } catch (Exception ex) { ex.printStackTrace(); }
    	if (Report.should_report("activity", 5)) {
    		Report.report(5, PoolRunner.logString() + " " + this + " continues stopfinish ");
    	}
    	FinishStateOps state = null;
    	
    	synchronized (this ) {
    		state = finishState_;
    		// Update finishState_ before throwing an exception.
    		if (finishStack_.empty()) {
    			finishState_ = null;
    		} else {
    			finishState_ = (FinishState) finishStack_.pop();
    		}
    	}
    	// Do not reference finishState_ below, instead reference state.
    	
    	Stack result = new Stack();
    	result = state.exceptions();
    	if (! result.empty()) {
    		if (result.size()==1) {
    			Throwable t = (Throwable) result.pop();
    			if (Report.should_report("activity", 3)) {
    				Report.report(3, PoolRunner.logString() + " " + this + " throws  " +  t);
    			}
    			if (t instanceof java.lang.Error)
    				throw (Error) t;
    			if (t instanceof java.lang.RuntimeException)
    				throw (RuntimeException) t;
    		}
    		throw new MultipleExceptions( result );
    	}
    	if (Report.should_report("activity", 3)) {
    		Report.report(3, PoolRunner.logString() + " " + this + " exits from finish.");
    	}
    	
    	
    }
    /** Is this activity currently executing a finish?
     * 
     * @return true iff the activity is executing a finish.
     */
    public /*myThread*/ boolean inFinish() {
    	return finishState_ != null;
    }
    
    /** Check whether it is ok to use the given clock c (by spawning an async
     * registered on the clock), throwing a ClockUseException if it is not.
     * Checks that the clock has not been resumed or dropped, and that the 
     * async is not being spawned inside a finish.
     * Invoked from code generated from the X10 source by the translator.
     * @param c -- The clock being checked for.
     */
    public /*myThread*/ Clock checkClockUse(Clock c) {
    	if (c.dropped()) throw new ClockUseException("Cannot transmit dropped clock.");
    	if (c.quiescent()) throw new ClockUseException("Cannot transmit resumed clock.");
    	if (inFinish()) throw new ClockUseException("Finish cannot spawn clocked async.");
    	return c;
    }
    /**
     * Execute this activity as if it is executed within a finish. 
     * Thus it throws an exception iff this activity or some activity asynchronously
     * spawned by it (transitively, and not within the scope of another finish)
     * throws an exception.
     *
     */
    public /*myThread*/ void finishRun() {
    	if (Report.should_report("activity", 5)) {
    		Report.report(5, PoolRunner.logString() + " " + this + ".finishRun() started." );
    	}
	
    	finishRun( this );
    }
    public /*myThread*/ void finishRun( Runnable r) {
    	try {
    		startFinish();
		assert(r!=null);
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
    public /*myThread*/ void addClock(Clock c) {
    	if (Report.should_report("clock", 3)) {
    		Report.report(3, PoolRunner.logString() + " " + this + " adds " +  c + ".");
    	}
    	clocks_.add(c);
    }
    /**
     * Drop a clock from this activity's clock list.
     * @param c
     */
    public /*myThread*/ void dropClock(Clock c) {
    	if (Report.should_report("clock", 3)) {
    		Report.report(3, PoolRunner.logString() + " " + this + " drops " +  c + ".");
    	}
    	clocks_.remove(c);
    }
    /**
     * Drop all clocks associated with this activity, and deregister this
     * activity from all these clocks.
     *
     */
    protected /*myThread*/ void dropAllClocks() {
    	for (Iterator it = clocks_.iterator(); it.hasNext();) {
    		Clock c = (Clock) it.next();
    		c.drop( this );
    	}
    	if (Report.should_report("clock", 3)) {
    		Report.report(3, PoolRunner.logString() + " " + this + " drops all clocks.");
    	}
    }
    /**
     * Implement next; for an activity. Blocks until each clock 
     * this activity is registered on has moved to the next phase.
     *
     */
    public /*myThread*/ void doNext() {
    	if (Report.should_report("activity", 3)) {
    		Report.report(3, PoolRunner.logString() + " " + this + ".doNext() on " +  clocks_);
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
    public  /*myThread*/ Activity finalizeActivitySpawn( final Activity child ) {
    	if (Report.should_report("activity", 3)) {
    		Report.report(3, PoolRunner.logString() + " " + this + " spawns " + child);
    	}
    	FinishStateOps target = finishState_ == null ? rootNode_ : finishState_;
    	child.setRootActivityFinishState( target );
    	if(target != null) {
    		target.notifySubActivitySpawn();
    	}
    	
    	ArrayList myASL = null;
    	synchronized (this) { myASL = asl_; }
    	if (myASL != null) {
    		for (int j=0;j< myASL.size();j++) {
    			// tell other activities that want to know that this has spawned child
    			ActivitySpawnListener asl = (ActivitySpawnListener) myASL.get(j);
    			asl.notifyActivitySpawn(child, this);
    		}
    	}
    	return child;
    }
	
    /**
     * Run after this activity is spawned, but before it's actually run.  Set up code
     * related to cluster VM goes here.
     * 
     * @author xinb
     */
    public void finalizeActivitySpawnAtChild() {
    	if(rootNode_ != null) 
    		rootNode_.notifySubActivitySpawnAtChild(fromRoot);
    }
    
    /** Register an activity spawn listener for this activity.
     * 
     * @param a
     */
	public  synchronized void registerActivitySpawnListener(ActivitySpawnListener a) {
		if (asl_ == null) {
			asl_ = new ArrayList(2);
		}
		asl_.add( a);
	}

   /**
    * This activity has terminated normally. Now clean up. (Notify the finish ancestor,
    * if any, and any other listeners (e.g. Sample listeners).
    *
    */
    public /*myThread*/ void finalizeTermination() {
    	if (Report.should_report("activity", 5)) {
    		Report.report(5, PoolRunner.logString() + " " + this + "terminates.");
    	}
    	dropAllClocks();
    	
    	if (rootNode_ != null)
    		rootNode_.notifySubActivityTermination(); 
    		if (asl_ != null) {
    			for (int j=0;j< asl_.size();j++) {
    				// tell other activities that want to know that this has spawned child
    				ActivitySpawnListener asl = (ActivitySpawnListener) asl_.get(j);
    				asl.notifyActivityTerminated(this);
    			}
    		}
    }
   
   /**
    * This activity has terminated abruptly. Now clean up. (Notify the finish ancestor, if any, and
    * any other listeners (e.g. Sample listeners).
    * @param t -- the reason for the abrupt termination.
    */
  
    public /*myThread*/ void finalizeTermination(Throwable t) {
    	if (Report.should_report("activity", 5)) {
    		Report.report(5, Thread.currentThread() + " " + this + " terminates abruptly with " + t);
    	}
    	dropAllClocks();
    	if (Report.should_report("activity", 5)) {
    		Report.report(5, Thread.currentThread() + " " + this + " drops clocks, has rootNode_ " + rootNode_);
    	}
    	if (rootNode_ != null)
    		rootNode_.notifySubActivityTermination( t);
    	if (asl_ != null) {
    		for (int j=0;j< asl_.size();j++) {
    			// tell other activities that want to know that this has spawned child
    			ActivitySpawnListener asl = (ActivitySpawnListener) asl_.get(j);
    			asl.notifyActivityTerminated(this);
    		}
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
     * An activity used to implement an X10 future.
     */
    public static abstract class Expr extends Activity {
        
        protected Future_c future; 
        /**
         * Wait for the completion of this activity and return the
         * return value.
         */
        public abstract x10.lang.Object getResult();
        public abstract void runSource();
        byte[]  serializedX10Result;
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

