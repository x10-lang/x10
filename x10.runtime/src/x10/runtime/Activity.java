package x10.runtime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.LinkedList;
import java.util.List;
import java.lang.reflect.*;
import java.io.*;
import x10.lang.x10Array;
import x10.lang.MultipleExceptions;
import x10.lang.ClockUseException;
import x10.runtime.distributed.RemoteClock;
import x10.runtime.distributed.RemoteObjectMap;
import x10.runtime.distributed.VMInfo;
import x10.runtime.distributed.Serializer;
import x10.runtime.distributed.Deserializer;
import x10.runtime.distributed.SerializerBuffer;
import x10.runtime.distributed.DeserializerBuffer;

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
    			Report.report(3, PoolRunner.logString() + " adding clocks " + clocks + " to " + this);
    		}
    	}
    	this.clocks_ = clocks;
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
    	// reg_.registerActivityStart(t, a, i);
    	if (Report.should_report("activity", 5)) {
    		Report.report(5, PoolRunner.logString() + " Activity: initializing " + this);
    	}
    	
    	Iterator it = clocks_.iterator();
    	while (it.hasNext()) {
    		Clock c = (Clock) it.next(); 
    		c.register( this );
    	}
    	
    }
    public /*mySpawningThread*/ void setRootActivityFinishState (FinishState root ) {
    	this.rootNode_ = root;
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
    	finishState_.waitForFinish();
    	if (Report.should_report("activity", 5)) {
    		Report.report(5, PoolRunner.logString() + " " + this + " continues stopfinish ");
    	}
    	FinishState state = null;
    	
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
    	
    	Stack result = state.exceptions();
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
    public /*myThread*/ void checkClockUse(Clock c) {
    	if (c.dropped()) throw new ClockUseException("Cannot transmit dropped clock.");
    	if (c.quiescent()) throw new ClockUseException("Cannot transmit resumed clock.");
    	if (inFinish()) throw new ClockUseException("Finish cannot spawn clocked async.");
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
    	FinishState target = finishState_ == null ? rootNode_ : finishState_;
    	child.setRootActivityFinishState( target );
    	target.notifySubActivitySpawn();
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
     * An Activity running on this VM has terminated.  Go tell the VM
     * that actually invoked it the news.  While you're at it, tell
     * that VM about any clocks of his that you no longer reference.
     **/
    public native void finalizeTerminationOfSurrogate(int invokingVM, long activityAsSeenByInvokingVM, long[] clks, byte[] serializedX10Result);
    
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
        if (activityAsSeenByInvokingVM == thisActivityIsLocal ||
            activityAsSeenByInvokingVM == thisActivityIsASurrogate) {
            if (rootNode_ != null)
    		rootNode_.notifySubActivityTermination();
            if (asl_ != null) {
                for (int j=0;j< asl_.size();j++) {
                    // tell other activities that want to know that this has spawned child
                    ActivitySpawnListener asl = (ActivitySpawnListener) asl_.get(j);
                    asl.notifyActivityTerminated(this);
                }
            }
    	} else {
            // Ready to finalize termination on surrogate (remote) Activity.
            // First, let's get a list of no longer used clocks on the
            // same VM as the surrogate.
            long[] clks = RemoteObjectMap.deleteClockEntries(invokingVM);
            byte[] serializedX10Result = null;
            if (this instanceof Expr) {
               try {
                  Field f = this.getClass().getDeclaredField("x10_result_");
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  ObjectOutputStream oos = new ObjectOutputStream(baos);
                  f.setAccessible(true);
                  oos.writeObject(f.get(this));
                  serializedX10Result = baos.toByteArray();
               } catch (Exception e) {
                  System.err.println("Could not serialize the future's return");
                  throw new Error(e);
               }
            }
            finalizeTerminationOfSurrogate(invokingVM, activityAsSeenByInvokingVM, clks, serializedX10Result);
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
        if (globalRefAddr == 0) {
            return "Activity " + Long.toHexString(hashCode());
        } else {
            return "Activity* " + Long.toHexString(globalRefAddr);
        }
    }
    /** A long descriptor for the activity. By default displays the finishState_ and the rootNode_.
     * 
     */
    public String toString() {
        String rv = "<" + myName();
        if (Configuration.VM_ != null &&
            activityAsSeenByInvokingVM != 0) {
            rv = rv + " on " + VMInfo.THIS_IS_VM;
        }
        rv = rv + " " + finishState_ + "," + rootNode_;
        if (activityAsSeenByInvokingVM != thisActivityIsLocal) {
            if (activityAsSeenByInvokingVM == thisActivityIsASurrogate) {
                rv = rv + ", Surrogate for vm " + placeWhereRealActivityIsRunning;//.vm_;
            } else {
                rv = rv + "," + Long.toHexString(activityAsSeenByInvokingVM) +
                    " on " + invokingVM;
            }
        }
        rv = rv + ">";
        return rv;
    }
    /* A short descriptor for the activity.
     * 
     */
    public String shortString() {
    	return "<" + myName() + ">";
    }

    public static final int thisActivityIsLocal = 0;
    public static final int thisActivityIsASurrogate = -1;
    /**
     * the following field has a value passed in from a remote VM
     * or one of the above two values
     **/
    public long activityAsSeenByInvokingVM;
    public int  invokingVM;
    public Place placeWhereRealActivityIsRunning;
    /**
     * if this Activity is a surrogate, the RemotePlace.runAsync will
     * cause it to be pinned and set globalRefAddr to its now fixed address.
     **/
    public long globalRefAddr;

    public void pseudoSerialize() {

       x10.runtime.distributed.Serializer serializer = new Serializer(this);
    
    
       clocksMappedToGlobalAddresses = new long[clocks_.size() << 1];
       SerializerBuffer clockBuffer = new SerializerBuffer(clocksMappedToGlobalAddresses);

       serializer.serializeClocks(clockBuffer,clocks_);
     
       int count = 0;
        
       count = serializer.calculateSize();
        
       pseudoSerializedLongArray = new long[count];

       serializer.serialize(new SerializerBuffer(pseudoSerializedLongArray));
       
       constructorSignature = serializer.getConstructorSignature();
        
       numArgsInConstructor = serializer.getNumArgsInConstructor();
       listOfClocksIsArgNum = serializer.getClockListPosition();
    }


    public void pseudoDeSerialize(LocalPlace_c pl) {
       final boolean trace = false;

       if(trace) System.out.println("Deserializing "+this.getClass().getName());
       Deserializer deserializer = new Deserializer(this);
       deserializer.deserializeClocks(clocks_,new DeserializerBuffer(clocksMappedToGlobalAddresses));

       deserializer.deserialize(pl,new DeserializerBuffer(pseudoSerializedLongArray));
  
    }

    String      constructorSignature;
    long[]      clocksMappedToGlobalAddresses;
    long[]      pseudoSerializedLongArray;
    int         numArgsInConstructor;
    int         listOfClocksIsArgNum;
    
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
                        if (activityAsSeenByInvokingVM == thisActivityIsLocal ||
                            activityAsSeenByInvokingVM == thisActivityIsASurrogate) {
                            future.setResult(getResult());
                        }
        	} catch (Throwable t) {
        		// Now nested asyncs have terminated.
        		future.setException(t);
        	}
        }

        public void finalizeTermination() {
            if (activityAsSeenByInvokingVM == thisActivityIsASurrogate) {
                try {
                    Field f = this.getClass().getDeclaredField("x10_result_");
                    ByteArrayInputStream bais = new ByteArrayInputStream(serializedX10Result);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    f.setAccessible(true);
                    f.set(this, ois.readObject());
                    future.setResult(getResult());
                } catch (Exception e) {
                    System.err.println("Could not deserialize the future's return " +e);
                    throw new Error(e);
                }
            }
            super.finalizeTermination();
        }
        
        public void finalizeTermination(Throwable t) {
            if (activityAsSeenByInvokingVM == thisActivityIsLocal) {
        	future.setException(t);
            }
            if (activityAsSeenByInvokingVM == thisActivityIsASurrogate) {
                try {
                    Field f = this.getClass().getDeclaredField("x10_result_");
                    ByteArrayInputStream bais = new ByteArrayInputStream(serializedX10Result);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    f.setAccessible(true);
                    f.set(this, ois.readObject());
                    future.setResult(getResult());
                } catch (Exception e) {
                    System.err.println("Could not deserialize the future's return");
                    throw new Error(e);
                }
            }
            super.finalizeTermination(t);
        }
        
    } // end of Activity.Expr

} // end of Activity

