package x10.runtime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

import x10.cluster.ClusterConfig;
import x10.cluster.ClusterRuntime;
import x10.cluster.Debug;
import x10.cluster.HasResult;
import x10.cluster.X10RemoteRef;
import x10.cluster.X10Runnable;
import x10.cluster.comm.RPCHelper;
import x10.cluster.message.MessageType;
import x10.cluster.replication.RTReplication;
import x10.cluster.replication.RTReplication.IDType;
import x10.lang.ClockUseException;
import x10.lang.Runtime;
import x10.lang.clock;

/**
 * Implementation of Clock.  Technically the spec says that this is
 * a value class, but of course the implementation of clocks needs to
 * keep mutable state (somewhere).  Clocks are also integrated with the
 * Runtime and with places since they need to interact with activity
 * creation.  
 
 * 
 <p>Let each activity A track the phase of every clock c it is registered
 with, call this variable A_C. 
 
 <p>Let each clock c track its phase, call it Cp. 
 
 <p>A clock is said to be in whole phase if A_C = Cp for every activity A. 
 
 <p>It is in split phase if A_C = Cp-1 for some activities, and A_C = Cp
 for some others.
 
 <p>In the algorithm we dont actually need to track Cp and A_C, though it
 is helpful to think as if we are.
 
 <p>The clock tracks three sets of activities: activities_, resumed_, and
 nextResumed_. The clock also has a boolean variable, splitPhase, which
 is initialy false.
 
 <p>Initially the clock starts out in whole phase (splitPhase=false).
 
 <p>We will assume that activities always do a resume before doing a next. 
 
 <p>Say that an activity A in activities_ is 
 <ul>
 <li> activeNow: clock is in wholephase, A is not in resumed_.
 <li> quiescedNow: A is in resumed_.
 <li> activeNext: clock is in splitphase, A is not in resumed_ || nextResumed_.
 <li> quiescedNext: A is in nextResumed_.
 
 <li> active: activeNow or activeNext
 <li>quiesced: quiescedNow or quiescedNext
 <li> registered: active or quiesced
 <li> unregistered: not in activities_, resumed_ or nextResumed_
 </ul>
 
 <p>Invariants on the state of the sytems:
 <ul>
 <li>  (1) A clock is either in splitPhase or wholePhase.
 <li>  (2) activity_ superset resumed_ u nextResumed_
 <li>  (3) nextResumed_ and resumed_ are disjoint
 <li>  (4) In wholePhase, nextResumed_ = empty.
 <li>  (5) Whenever next is performed by an activity, activity is quiesced.
 <li>  (6) An activity is either active or quiesced.
 </ul>
 
 <P>Transition from wholePhase to splitPhase: when |activity_| = |resumed_|
 <p>Transition from splitPhase to wholePhase: when |resumed_| = 0
 
 <p>When an activity does a resume:
 <ul>
 <li>unregistered -> throw CUE
 <li>quiescedNow -> quiescedNow, return.    
 <li>quiescedNext -> quiescedNext, return.
 <li>activeNow -> quiescedNow, tryMoveToSplit, return.
 <li>activeNext -> quiescedNext, return.
 </ul>
 
 <p>When an activity does a next: {assumed to be quiesced by (5)}
 <ul>
 <li>unregistered -> throw CUE
 <li>quiescedNow, splitPhase -> activeNext, tryMoveToWhole, return.
 <li>quiescedNow, wholePhase -> block until splitPhase, move to activeNow on wakeup.
 <li> quiescedNext, splitPhase -> block until next splitPhase, move to activeNow on wakeup
 <li> quiescedNext, wholePhase : impossible start state by (4).
 </ul>
 
 <p>When an activity terminates:
 <ul>
 <li> unregistered -> throw CUE
 <li> registered -> unregistered, tryMoveToSplit, return.
 </ul>
 
 <p>When an activity B spawns another A, giving it the clock c:
 <ul>
 <li> B quiesced  -> throw CUE
 <li> B unregistered -> throw CUE
 <li> A unregistered -> A registered, return.
 </ul>
 
 <p>tryMoveToSplit: 
 If |activities_| == |resumed_|, move clock to splitPhase, wake up all
 suspended activities.
 
 <p>tryMoveToWhole:
 If |resumed_| == 0, move clock to wholePhase, move nextResumed_ to resumed_,
 set nextResumed_ to empty. {This causes all activities in quiescedNext state
 to move to the quiescedNow state.}
 
 * </code>
 * @author Christian Grothoff, Christoph von Praun
 * @author vj
 */

/* This really should be a final class.  However, for the time being,
   I allow RemoteClock to extend this class. */
public /* final */ class Clock extends clock {
	
	private static int nextId_ = 0;
	private int id_;
	private final String name_;

	
	/**
	 * A Set of all activities registered with this clock.
	 */
	protected final Set activities_ = new HashSet(); // <Activity>
	protected int activityCount_ = 0;
        public int activityCount() { return activityCount_; }
	
	/** A clock is split iff all activities have resumed, the clock has advanced,
	 * and there is at least one activity that has resumed that has not yet performed
	 * a next.
	 * In split phase, activities are executing in two phases: the current phase
	 * and the next phase. 
	 */
	private boolean splitPhase_ = false;
	
	/** The set of all activities that have executed a resume in the current clock cycle.
	 * 
	 */
	private final Set resumed_ = new HashSet(); // <Activity>
	private int resumedCount_ = 0;
	
	/**
	 * The Set of all activities that have executed a resume in the next phase of a 
	 * split clock.
	 */
	private final Set nextResumed_ = new HashSet(); // <Activity>
	private int nextResumedCount_ = 0;
	
	/**
	 * The first registered advance listener.  null if none is
	 * registered.
	 */
	private AdvanceListener listener1_;
	
	/**
	 * Lazy initialization (since we typically only have at most one
	 * listener).
	 */
	private ArrayList listeners_; // <AdvanceListener>
	
	/**
	 * The current phase of the Clock.  Incremented by one in each
	 * phase (may wrap around!).
	 */
	private int phase_;
	
	
	/**
	 * tree implementation of distributed clock.
	 */
	private long uniqueId_;
	private int childCnt_ = 0;
	private int childResumedCnt_ = 0; 
	private Set<Long> childResumed_ = new HashSet<Long>();
	private int childNextResumedCnt_ = 0;
	private Set<Long> childNextResumed_= new HashSet<Long>(); 
	private int childNextCnt_ = 0;
	private Set<Long> childNext_= new HashSet<Long>();
	private int childNextNextCnt_ = 0;
	private Set<Long> childNextNext_= new HashSet<Long>();
	
	private boolean isRoot_ = true;
	private boolean advancedParent = false;
	public static final int dbg_level = Debug.clock;
	
	public boolean getSplitState() { return splitPhase_; }
	public int getPhase() { return phase_; }
	public int setPhase(int ph) { int old = phase_;  phase_ = ph; return old; }
	
	/**
	 * Inform the root clock node of a new child clock node.
	 * 
	 * @param child
	 */
	public void addChild(Activity child) {
		assert(rref != null);
		final X10RemoteRef pclock = rref;
		RPCHelper.serializeCodeW(pclock.getPlace().id, new HasResult(MessageType.CLK) {
			public void run() {
				synchronized(Clock.this) { 
					((Clock)pclock.getObject()).childCnt_ ++;
				}
			}
			public Object getResult() { return null; }
		});
		//child_.add(child.getId());
	}
	
	public synchronized void signalResume(long id) {
		if(Report.should_report("cluster", Clock.dbg_level))
			Report.report(Clock.dbg_level, "Clock.signalResumet>>> before: "+this+" by "+id);
		
		if(splitPhase_) {
			if(childNextResumed_.add(new Long(id))) //filter duplicates
				childNextResumedCnt_ ++;
			//return;
		} else {
			if(childResumed_.add(new Long(id))) { //filter duplicates
				childResumedCnt_++; 
				tryMoveToSplit_();
			}
		}
		
		if(Report.should_report("cluster", Clock.dbg_level))
			Report.report(Clock.dbg_level, "Clock.signalResume>>> after: "+this);
	}
	
	public synchronized void signalDrop(long id, int curPh, boolean dropped, boolean isSplit) {
		if(Report.should_report("cluster", Clock.dbg_level))
			Report.report(Clock.dbg_level, "Clock.signalDrop>>> before: "+this+" by "+isSplit+" "+curPh+" "+dropped);
		
		if(dropped) { //regardless of child clock's phase or split state
			childCnt_ --;
			boolean test = false;
			if(childResumed_.remove(new Long(id))) { childResumedCnt_ --; test = true; }
			if(childNext_.remove(new Long(id))) { childNextCnt_ --;test = true;  }
			if(childNextResumed_.remove(new Long(id))) { childNextResumedCnt_ --;test = true;} 
			if(childNextNext_.remove(new Long(id))) { childNextNextCnt_ --;test = true; }
			assert(childCnt_ >= childResumedCnt_ && childCnt_ >= childNextCnt_);
		
			assert(isRoot_);
			tryChangeSplitState();
		} else {
			if(isSplit) 
				; 
			else {
				//if(! childResumed_.contains(new Long(id)))
					signalResume(id);
				//if(! childNext_.contains(new Long(id)))
					signalNext(id, curPh);
			}
		}
	}
	
	/**
	 * Inform root node that a shadow node wants to advance. 
	 * 
	 * splitPhase_ = false:  assert(ph == phase_)  childNextCnt_++
	 * splitPhase_ = true: ph < phase_ ? childNextCnt_++ : childNextNextCnt++
	 * 
	 */
	public synchronized void signalNext(long id, int ph) {
		if(Report.should_report("cluster", Clock.dbg_level))
			Report.report(Clock.dbg_level, "Clock.signalNext>>> before: "+this+" "+splitPhase_+" "+ph+" "+phase_);
		
		assert(splitPhase_ || ph == phase_);
		assert(ph == phase_ || ph+1 == phase_);
		
		if((!splitPhase_) || ph < phase_) {//2): child call resume, and then call next
			if(childNext_.add(new Long(id)))
				childNextCnt_ ++;
			if(splitPhase_) ; //non-block
			else if((activityCount_ +childCnt_) == (resumedCount_ +childResumedCnt_)) ; //non-block 
			else block_();
		} else { //(splitPhase_ && ph == phase_)
			if(childNextNext_.add(new Long(id)))
				childNextNextCnt_ ++;
			block_();
		}
		
		tryMoveToWhole_();
		
		if(Report.should_report("cluster", Clock.dbg_level))
			Report.report(Clock.dbg_level, "Clock.signalNext>>> after: "+this);
	}
	
	/**
	 * This constructor is used to contruct a shadow Clock when a clock is
	 * distributed.  Shadow clocks has its parent pointer 'rref_p' set, and
	 * every non-leaf clock node maintain a 'childCount_'.
	 */
	private Clock(X10RemoteRef p, int phase) {
		this.rref = p;
		this.phase_ = phase;
		this.name_ = "shadow clock";
		synchronized (getClass()) {
			id_ = nextId_++;
		}
		this.isRoot_ = false;
		uniqueId_ = RTReplication.getUniqueID(IDType.CLOCK);
		//Debug.printStack();
		
		//try { throw new Exception(); } catch (Exception e) { e.printStackTrace(); }
		//To be initialized in the right context: moved to initDistribution()
		//activities_.add(...)
		//activityCount++
	}
	
    
	/**
	 * Create a new Clock.  Registers the current activity with
	 * the clock as a side-effect (see X10 Report).
	 */
	public Clock() {
		this( "");
	}
	public Clock( String name) {
		this.name_ = name;
		synchronized (getClass()) {
			id_ = nextId_++;
		}

		Activity a = Runtime.getCurrentActivity();
		a.addClock(this);
		activities_.add(a);
		activityCount_++;
		if (Report.should_report("clock",3)) {
			Report.report(3, PoolRunner.logString() + " " + this + " created by " + a +".");
		}

	}
	
	/**
	 * An activity a is inactive on a clock if it is not registered with it
	 * or it is registered with it, but has already resumed it in the current phase.
	 * @param a
	 * @return
	 */
	private boolean inactive( Activity a) {
		return ( (! activities_.contains(a)) || quiescent(a));
	}
	/**
	 * Register the current activity with this clock.
	 */
	public synchronized void register(Activity a ) {
		/* TODO ... a better authorizer for multi-VM case */
		Activity authorizer = null; 
		if(!ClusterConfig.multi) {
			authorizer = Runtime.getCurrentActivity();
		}
		if (authorizer != null)
			if (Report.should_report("clock", 5)) {
				Report.report(5, PoolRunner.logString() + " " + this + ".register:" + authorizer + " registering " + a);
			}
		if(Report.should_report("cluster", Debug.clock))
			Report.report(Debug.clock, "Clock.register>>>");
		synchronized (this) {
			if (authorizer != null && inactive(authorizer))	
				throw new ClockUseException(authorizer + "is not active on " + this + "; cannot transmit.");
			if (activities_.contains(a)) return;			
			activities_.add(a);
			activityCount_++;
			if(Report.should_report("cluster", Debug.clock))
				Report.report(Debug.clock, "Clock.register>>>"+this);
		}
		if (Report.should_report("clock", 3)) {
			Report.report(3, PoolRunner.logString() + " " + this + "...done.(activityCount_=" + activityCount_+").");
		}
	}
	
	
	private boolean quiescent( Activity a) {
		return resumed_.contains(a) || nextResumed_.contains(a);
		
	}
	/**
	 * Notify the clock that this activity has completed its
	 * work in this phase of the clock.
	 */
	public void resume() {
		if(Report.should_report("cluster", Clock.dbg_level))
			Report.report(Clock.dbg_level, "Clock.Resume>>> before: "+this);
		
		resume(Runtime.getCurrentActivity());
		
		if(Report.should_report("cluster", Clock.dbg_level))
			Report.report(Clock.dbg_level, "Clock.Resume>>> after: "+this);
		//try { throw new Exception(); } catch (Exception e) {e.printStackTrace(); }
	}
	public void resume(Activity a) {
		// do not lock earlier - see comment in doNext
		if (Report.should_report("clock", 5)) {
			Report.report(5, PoolRunner.logString() + " " + this + ".resume(" + a  +")");
		}
		synchronized (this) {
			if (! activities_.contains(a))
				throw new ClockUseException(a + "is not registered with " + this +"; cannot execute 'resume'.");
			
			if (quiescent(a)) {
				if (Report.should_report("clock", 5)) {
					Report.report(5, PoolRunner.logString() + " " + this + "...returned (noop).");
				}
				return; 
			}
			if (splitPhase_) {
				nextResumed_.add(a);
				nextResumedCount_++;
				if (Report.should_report("clock", 5)) {
					Report.report(5, PoolRunner.logString() + " " + this + "...added to nextResumed.");
				}
				return;
			}
			resumed_.add(a);
			resumedCount_++;
			if (Report.should_report("clock", 3)) {
				Report.report(3, PoolRunner.logString() + " " + this + "...added to resumed.");
			}
			tryMoveToSplit_();
		}
	}
	
	
	
	public boolean registered() {
		Activity a = Runtime.getCurrentActivity();	
		// do not lock earlier - see comment in doNow       
		synchronized (this) {
			return activities_.contains(a);
		}
	}
	public boolean dropped() {
		return ! registered();
	}
	
	public boolean quiescent() {
		Activity a = Runtime.getCurrentActivity();
		synchronized (this) {
			return quiescent(a);
		}
		
	}
	/**
	 * Drop this activity from the clock.  Afterwards the
	 * activity may no longer use continue or now on this clock.
	 * Other activities will no longer be blocked waiting for 
	 * the current activity to complete the phase.  
	 * 
	 */
	public void drop() {
		if(Report.should_report("cluster", Clock.dbg_level))
			Report.report(Clock.dbg_level, "Clock.drop>>> before: "+this);
		
		Activity a = Runtime.getCurrentActivity();
		if (drop(a))
			a.dropClock( this );
		
		if(Report.should_report("cluster", Clock.dbg_level))
			Report.report(Clock.dbg_level, "Clock.drop>>> after: "+this);
	}
	
	/**
	 * Drop the given activity from the clock.  Afterwards the
	 * activity may no longer use continue or now on this clock.
	 * Other activities will no longer be blocked waiting for 
	 * the current activity to complete the phase.<p>  
	 * 
	 * Note that this method is used internally (by clock.drop or
	 * by an activity exiting) and should never be called directly
	 * by clients (hence package-scoped). Note that this method
	 * does not unregister the clock with the list of clocks kept
	 * by the activity.
	 * 
	 * @return true if the activity has already dropped this
	 *   clock (or if it never was registered).
	 */
	public synchronized boolean drop(Activity a) {
	    //assert(activities_.contains(a)); //must have a
		boolean ret = activities_.remove(a);
		if (ret) activityCount_--;
		if (resumed_.remove(a)) resumedCount_--;
		if (nextResumed_.remove(a)) nextResumedCount_--;
		
		if (ret) 
			tryChangeSplitState();
		
		if (Report.should_report("clock", 3)) {
			Report.report(3, this + " drops " + a +").");
		 }
		return ret;        
	}
	
	/**
	 * Change the state of this Clock after a 'drop' call.  If this is root, move to
	 * split or whole accordingly;
	 * If this is a shadow node, there are a few scenarios: 
	 * - splitPhase_ = true:  signalNext not sent; some/no others waiting
	 *                        signalNext sent;  some/no others waiting
	 * - splitPhase_ = false: signalNext not sent; some/no others waiting
	 *                        signalNext sent; some/no others waiting  
	 * XXX revist
	 */
	synchronized void tryChangeSplitState() {
		if(isRoot_) {
			if(splitPhase_) 
				tryMoveToWhole_();
			else
				if((activityCount_ == resumedCount_ && childCnt_ == childResumedCnt_))
					moveToSplit_();
		} else { //shadow node 
			/* splitPhase = true: assert(phase_ == root.phase_), if(!dropped) do nothing
			 * splitPhase = false: if(!dropped) { signalResume; signalNext }
			 */			
			if ( (activityCount_ == resumedCount_ && childCnt_ == childResumedCnt_)) {
				//for a shadow node, this only means it's locally quiescent
				final boolean dropped = (activityCount_ + childCnt_) == 0 ? true : false;
				final long clockId = uniqueId_;
				final int curPh = phase_;
				final boolean isSplit = splitPhase_;
				final X10RemoteRef pclock = rref;
				if( (!splitPhase_ && !advancedParent) ||    //everyone else has call doNext before locally quiescent, or 
						dropped) {  						//drop this child node
					advancedParent = true;
					
					RPCHelper.serializeCodeWP(pclock.getPlace().id, new HasResult(MessageType.CLK) {
						public void run() {
							Clock o = ((Clock)pclock.getObject());
							o.signalDrop(clockId, curPh, dropped, isSplit);
						}
						public Object getResult() { return null; }
					}, this);
				 
					moveToSplit_();
				}
				
				//this.notifyAll(); //
			}
		}
	}
	
	synchronized boolean moveToSplit_() {
		if(!splitPhase_) {
			splitPhase_ = true;
			this.phase_++;

			// first notify everyone
			if (this.listener1_ != null) {
				this.listener1_.notifyAdvance();
				if (this.listeners_ != null) {
					int size = listeners_.size();
					for (int i=0;i<size;i++)
						((AdvanceListener)listeners_.get(i)).notifyAdvance();
				}
			}
			this.notifyAll();
			return true;
		} else {
			assert(false);
			//tryMoveToWhole_(); //XXX
			return false;
		}
	}
	
	/**
	 * First check if this clock node is locally quiescent, if no, return false;
	 * Otherwise, if this is a root node, the clock is globally quiescent, split the phase;
	 * if this is a shadow node, send an aggregated 'resume' message to root;
	 * 
	 * @author xinb
	 */
	synchronized boolean tryMoveToSplit_() {
		if (! (activityCount_ == resumedCount_ && childCnt_ == childResumedCnt_)) {
			return false;
		}
		//locally quiescent already
		
	    if(isRoot_) 
	    	return moveToSplit_();
	    else {
	    	//if(advancedParent) {
			//child lock node
			//final int curPhase = phase_;
			final long clockId = uniqueId_;
			final X10RemoteRef pclock = rref;
			//need synchronous call
			RPCHelper.serializeCodeWP(pclock.getPlace().id, new HasResult(MessageType.CLK) {
				public void run() {
					((Clock)pclock.getObject()).signalResume(clockId); //Has to be called on the right Clock object
				}
				public Object getResult() { return null; }
			}, this);
			return false;
			//}
	    }
	}
	
	private synchronized boolean tryMoveToWhole_() {
		if (resumedCount_ != 0 || childNextCnt_ != childCnt_) {
			return false;
		}

		splitPhase_ = false;
		resumed_.addAll(nextResumed_);
		nextResumed_.clear();
		resumedCount_ = nextResumedCount_;
		nextResumedCount_ = 0;
		
		advancedParent = false;
		childResumed_.clear();
		childResumed_.addAll(childNextResumed_);
		childResumedCnt_ = childNextResumedCnt_;
		childNext_.clear();
		childNext_.addAll(childNextNext_);
		childNextCnt_ = childNextNextCnt_;
		childNextResumed_.clear();
		childNextNext_.clear();
		childNextResumedCnt_ = 0;
		childNextNextCnt_ = 0;
		
		return true;
	}
	
	
	private void block_() {
		int start = phase_;
		while (start == phase_) { // signal might be random in Java, check!
			try {
				this.wait(); // wait for signal
			} catch (InterruptedException ie) {
				throw new Error(ie); // that was unexpected...
			}
		}
	}
	
	public void doNext() {
		if(Report.should_report("cluster", Clock.dbg_level))
			Report.report(Clock.dbg_level, "Clock.doNext>>> before: "+this);
		
		doNext(Runtime.getCurrentActivity());
		
		if(Report.should_report("cluster", Clock.dbg_level))
			Report.report(Clock.dbg_level, "Clock.doNext>>> after: "+this);	
	};
	
	
    
	public synchronized void doNext(Activity a) {
		assert activities_.contains(a);
		assert nextResumed_.contains(a) || resumed_.contains(a);
		if (!splitPhase_ || nextResumed_.contains(a)) {				
			if(!isRoot_ && !advancedParent && ((activityCount_ +childCnt_) == (resumedCount_ +childResumedCnt_))) {
				//child clock node, haven't inform parent, all activity resumed locally
				final int curPhase = phase_;
				final long clockId = uniqueId_;
				final X10RemoteRef pclock = rref;
				//blocking call
				advancedParent = true; //remember we have signal the parent
				/* The following call can block, thus it is important to release the lock on the 
				 * current Clock object. 
				 * XXX a better way might be to have RPCHelper spawn another thread to do serialization and wait
				 */
				RPCHelper.serializeCodeWP(pclock.getPlace().id, new HasResult(MessageType.CLK) /*Runnable()*/ {
					public void run() {
						((Clock)pclock.getObject()).signalNext(clockId, curPhase);
					}
					public Object getResult() { return null;}
				}, this);
				
				//advancedParent = true;
				moveToSplit_(); //split postponed until fist doNext, instead of last resume
			} else {
				//root clock node
				//or child node before locally quiescent, or waiting from parent
				block_();
			}
		}
		
		assert resumed_.contains(a);
		resumed_.remove(a);
		resumedCount_ --;
		tryMoveToWhole_();
		return;
	}
	
	/**
	 * Register a callback that is to be called whenever the clock
	 * advances into the next phase.
	 * 
	 * @param al the listener to notify
	 */
	public synchronized void registerAdvanceListener(AdvanceListener al) {
		if (this.listener1_ == null) {
			this.listener1_ = al;
		} else {
			if (this.listeners_ == null)
				this.listeners_ = new ArrayList();
			this.listeners_.add(al);
		}
	}
	
	/**
	 * Callback method used by the Clock to notify all listeners
	 * that the Clock is advancing into the next phase.
	 *
	 * @author Christian Grothoff
	 */
	public interface AdvanceListener {
		public void notifyAdvance();
	}
	public String toString() { 
		return "Clock" + id_ +  name_ 
		+"("  + (splitPhase_ ? "split," : "whole,") + phase_
		+ ":" + activityCount_ 
		+ "," + resumedCount_
		+ "," + nextResumedCount_
		
		+ "," + childCnt_
		+ "," + childResumedCnt_
		+ "," + childNextCnt_

		+ ")";
	}
	
} // end of Clock
