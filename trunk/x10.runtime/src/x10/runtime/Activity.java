package x10.runtime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.LinkedList;
import java.util.List;
import x10.lang.MultipleExceptions;

/** The representation of an X10 async activity.
 * @author Christian Grothoff, Christoph von Praun, vj
 */
public abstract class Activity implements Runnable {

    /**
     * The place on which this activity runs. 
     * 
     */
    protected Place place_ ;
    
    protected List clocks_;
    
    protected FinishState finishState_ = null;
    protected Stack finishStack_ = new Stack();
    /**
     * Exception collector for this activity. This is field is used by the default
     * runtime to manage activities.  The stack is a stack of Throwables.
     */
   
    /**
     * The FinishState up in the invocation tree within who's finish
     * this activity is being executed.
     */
    protected FinishState rootNode_;
    
    /** Create an activity with the given set of clocks.  
     * 
     * @param clocks
     */
    public Activity( List clocks) {
    	if (! clocks.isEmpty()) {
    		if (Report.should_report("activity", 3)) {
    			Report.report(3, Thread.currentThread() + " adding clocks " + clocks + " to " + this);
    		}
    	}
    	this.clocks_ = clocks;
    }
    public Activity() {
    	this.clocks_ = new LinkedList();
    }
    public void setPlace(Place p) {
    	this.place_ = p;
    }
    public Place getPlace() {
    	return place_;
    }
    /**
     * Start executing a finish operation.
     *
     */
    public synchronized void startFinish() {
    	if (finishState_ != null) {
    		finishStack_.push(finishState_);
    	}
    	finishState_=new FinishState(this);  
    	if (Report.should_report("activity", 3)) {
    		Report.report(3, Thread.currentThread() + " " + this + " starts finish " + finishState_);
    	}
    }
       
    public synchronized void pushException(Throwable t) {
    	if (Report.should_report("activity", 3)) {
    		Report.report(3, Thread.currentThread() + " " + this + " pushing exception " + t + " onto " + finishState_);
    	}
    	
    	finishState_.finish_.push(t);
    }
    /**
     * Suspend until all activities spawned during this finish 
     * operation have terminated. Throw an exception if any
     * async terminated abruptly. Otherwise continue normally.
     *
     */
    public synchronized void stopFinish() {
    	if (Report.should_report("activity", 5)) {
    		Report.report(5, Thread.currentThread() + " " + this + " enters stopfinish ");
    	}
    	while ( finishState_.finishCount != 0) {
    		try {
    			this.wait();
    		} catch (InterruptedException z) {
    			// What should be done here?
    		}
    	}
    	if (Report.should_report("activity", 5)) {
    		Report.report(5, Thread.currentThread() + " " + this + " continues stopfinish ");
    	}
    	FinishState state = finishState_;
    	// Update finishState_ before throwing an exception.
    	if (finishStack_.empty()) {
    		finishState_ = null;
    	} else {
    		finishState_ = (FinishState) finishStack_.pop();
    	}
    	// Do not reference finishState_ below, instead reference state.
    	if (! state.finish_.empty()) {
    		if (state.finish_.size()==1) {
    			Throwable t = (Throwable) state.finish_.pop();
    			if (Report.should_report("activity", 3)) {
    	    		Report.report(3, Thread.currentThread() + " " + this + " throws  " +  t);
    	    	}
    			if (t instanceof java.lang.Error)
    				throw (Error) t;
    			if (t instanceof java.lang.RuntimeException)
    				throw (RuntimeException) t;
    		}
    		throw new MultipleExceptions( state.finish_ );
    	}
    	if (Report.should_report("activity", 3)) {
    		Report.report(3, Thread.currentThread() + " " + this + " exits from finish.");
    	}
    	
    	
    }
    /** Is this activity currently executing a finish?
     * 
     * @return true iff the activity is executing a finish.
     */
    public boolean inFinish() {
    	return finishState_ != null;
    }
    /**
     * Execute this activity as if it is executed within a finish. 
     * Thus it throws an exception iff this activity or some activity asynchronously
     * spawned by it (transitively, and not within the scope of another finish)
     * throws an exception.
     *
     */
    public void finishRun() {
    	if (Report.should_report("activity", 5)) {
    		Report.report(5, Thread.currentThread() + " " + this + ".finishRun() started." );
    	}
    	finishRun( this );
    }
    public void finishRun( Runnable r) {
    	try {
    		startFinish();
    		r.run();
    	} catch (Throwable t) {
    		pushException(t);
    	} 
    	stopFinish();
    	
    }
    /** Add a clock to this activity's clock list. (Called when
     * this activity creates a new clock.)
     * 
     * @param c
     */
    public void addClock(Clock c) {
    	if (Report.should_report("clock", 3)) {
    		Report.report(3, Thread.currentThread() + " " + this + " adds " +  c + ".");
    	}
    	clocks_.add(c);
    }
    /**
     * Drop a clock from this activity's clock list.
     * @param c
     */
    public void dropClock(Clock c) {
    	if (Report.should_report("clock", 3)) {
    		Report.report(3, Thread.currentThread() + " " + this + " drops " +  c + ".");
    	}
    	clocks_.remove(c);
    }
    /**
     * Drop all clocks associated with this activity, and deregister this
     * activity from all these clocks.
     *
     */
    protected void dropAllClocks() {
    	for (Iterator it = clocks_.iterator(); it.hasNext();) {
    		Clock c = (Clock) it.next();
    		c.drop( this );
    	}
    	if (Report.should_report("clock", 3)) {
    		Report.report(3, Thread.currentThread() + " " + this + " drops all clocks.");
    	}
    }
    /**
     * Execute a next operation. Blocks until each clock 
     * this activity is registered on has moved to the next phase.
     *
     */
    public void doNext() {
    	if (Report.should_report("activity", 3)) {
    		Report.report(3, Thread.currentThread() + " " + this + ".doNext() on " +  clocks_);
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
    public void setRootActivity (FinishState root ) {
    	this.rootNode_ = root;
    }
    
    /** This activity has spawned child. Properly link child
     * into the finish termination chain and notify all listeners.
     * 
     * @param child -- the activity being spawned.
     */
    public Activity finalizeActivitySpawn( Activity child ) {
    	if (Report.should_report("activity", 3)) {
    		Report.report(3, Thread.currentThread() + " " + this + " spawns " + child);
    	}
    	if (finishState_!=null) {
    		child.setRootActivity( finishState_ );
    		synchronized (this ) {
    			finishState_.finishCount++;
    			if (Report.should_report("activity", 5)) {
    	    		Report.report(5, " updating " + finishState_.toString());
    	    	}
    		}
    	} else {
    		child.setRootActivity( rootNode_ );
    		synchronized (rootNode_) {
    			rootNode_.finishCount++;
    			if (Report.should_report("activity", 5)) {
    	    		Report.report(5, " updating " + rootNode_.toString());
    	    	}
    		}
    	}
    	if (asl_ != null) {
    		for (int j=0;j< asl_.size();j++) {
    			// tell other activities that want to know that this has spawned child
    			ActivitySpawnListener asl = (ActivitySpawnListener) asl_.get(j);
    			asl.notifyActivitySpawn(child, this);
    		}
    	}
    	return child;
    }
    
    ArrayList asl_ = new ArrayList();
	
    /** Register an activity spawn listener for this activity.
     * 
     * @param a
     */
	public synchronized void registerActivitySpawnListener(ActivitySpawnListener a) {
		if (asl_ == null) {
			asl_ = new ArrayList(2);
		}
		asl_.add( a);
	}
    /**
    * This activity is about to be executed. 
    * Perform whatever initialization is necessary,
    * informing whoever is interested. Must be performed by the thread
    * that will spawn this activity onto another thread.
    *
    */
    public void initializeActivity() {
    	// reg_.registerActivityStart(t, a, i);
    	if (Report.should_report("activity", 5)) {
    		Report.report(5, Thread.currentThread() + " Activity: initializing " + this);
    	}
    	
    	Iterator it = clocks_.iterator();
    	while (it.hasNext()) {
    		Clock c = (Clock) it.next(); 
    		
    		c.register( this );
    	}
    	
    }
  
   /**
    * This activity has terminated normally. Now clean up. (Notify the finish ancestor,
    * if any, and any other listeners (e.g. Sample listeners).
    *
    */
    public void finalizeTermination() {
    	if (Report.should_report("activity", 5)) {
    		Report.report(5, Thread.currentThread() + " " + this + "terminates.");
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
  
    public void finalizeAbruptTermination(Throwable t) {
    	if (Report.should_report("activity", 5)) {
    		Report.report(5, Thread.currentThread() + " " + this + " terminates abruptly with " + t);
    	}
    	dropAllClocks();
    	if (Report.should_report("activity", 5)) {
    		Report.report(5, Thread.currentThread() + " " + this + " drops clocks, has rootNode_ " + rootNode_);
    	}
    	if (rootNode_ != null)
    		rootNode_.notifySubActivityAbruptTermination( t);
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
	 * @return -- the short name for hte activity.
	 */
    public String myName() {
    	return "Activity " + hashCode();
    }
    /** A long descriptor for the activity. By default displays the finishState_ and the rootNode_.
     * 
     */
    public String toString() {
    	return "<" + myName() + " " + finishState_ + "," + rootNode_ + ">";
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
        
        public void finalizeAbruptTermination(Throwable t) {
        	future.setException(t);
        	super.finalizeAbruptTermination(t);
        }
        
    } // end of Activity.Expr

} // end of Activity