package x10.runtime;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;

import x10.lang.Runtime;
import x10.lang.clock;

/**
 * Implementation of Clock.  Technically the spec says that this is
 * a value class, but of course the implementation of clocks needs to
 * keep mutable state (somewhere).  Clocks are also integrated with the
 * Runtime and with places since they need to interact with activity
 * creation.  
 * 
 * @author Christian Grothoff, Christoph von Praun
 */
public final class Clock extends clock {

    private static int nextId_ = 0;
    private int id_;
    
    /**
     * The activity information provider that this clock can use.
     */
    private final ActivityInformationProvider aip_;
    
    /**
     * A Set of all activities registered with this clock.
     */
    private final Set activities_ = new HashSet(); // <Activity>
   
    /**
     * The Set of all activities that have not yet completed in the
     * current cycle. 
     */
    private final Set pending_ = new HashSet(); // <Activity>
    
    /**
     * Activities spawned via 'now'.  All of these activities
     * must terminate before we can advance to the next phase.
     */
    private final Set nowSet_ = new HashSet(); // <Activity>
    
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
     * Create a new Clock.  Registers the current activity with
     * the clock as a side-effect (see X10 Report).
     */
    protected Clock(ActivityInformationProvider aip) {
        synchronized (getClass()) {
            id_ = nextId_++;
        }
        this.aip_ = aip;
        Runtime.getCurrentActivityInformation().getRegisteredClocks().add(this);
        register();
    }
    
    /**
     * Register the current activity with this clock.
     */
    synchronized void register() {
        Activity a = aip_.getCurrentActivity();
        assert ! activities_.contains(a);
        if (activities_.add(a))
            pending_.add(a);
        aip_.registerActivitySpawnListener(a, dropListener_);
    }
    
    /**
     * Execute the given activity.  The clock will not advance
     * into the next phase until the given activity and all
     * activities transitively started by a have completed.
     * 
     * @param a an activity to run
     */
    public synchronized void doNow(Activity a) {
        assert activities_.contains(aip_.getCurrentActivity());
        nowSet_.add(a);
        aip_.registerActivitySpawnListener(a, nowSpawnListener_);
        ((Place) Runtime.here()).runAsync(a, null);
    }
    
    /**
     * Notify the clock that this activity has completed its
     * clocked activities in this cycle, that is the current
     * activity will not issue any further calls to 'now' until
     * it calls 'next'.
     *
     */
    public void resume() {
        Activity a = aip_.getCurrentActivity();
        // do not lock earlier - see comment in doNext
        synchronized (this) {
            assert activities_.contains(a);
            pending_.remove(a);
        }
    }
     
    public boolean dropped() {
        boolean ret;
        Activity a = aip_.getCurrentActivity();

        // do not lock earlier - see comment in doNow       
        synchronized (this) {
            ret = !activities_.contains(a);
        }
        return ret;
    }
    
    /**
     * Drop this activity from the clock.  Afterwards the
     * activity may no longer use continue or now on this clock.
     * Other activities will no longer be blocked waiting for 
     * the current activity to complete the phase.  
     * 
     * @return true if the activity has already dropped this
     *   clock (or if it never was registered).
     */
    public boolean drop() {
        boolean b = drop(aip_.getCurrentActivity());
        if (!b)
            Runtime.getCurrentActivityInformation().getRegisteredClocks().remove(this);
        return b; 
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
    synchronized boolean drop(Activity a) {
        boolean ret = activities_.remove(a);
        pending_.remove(a);
        tryAdvance_(); 
        return ret;        
    }
    
    /**
     * Block until all clocks that this activity is registered with
     * have called continue (or next since next implies continue on
     * all registered clocks). 
     */
    public void doNext() {
        Activity a = aip_.getCurrentActivity();
        
        // do not acquire lock earlier - otherwise deadlock can happen
        // because the lock used to protected aip_.getCurrentActivity
        // is also held when terminating activities drop locks ...
        synchronized (this) {
            assert activities_.contains(a);
            pending_.remove(a); // this one is done! 
            if (tryAdvance_())
                return; // we advanced, continue immediately!
            // wait for next phase
            int start = phase_;
            while (start == phase_) { // signal might be random in Java, check!
                try {
                    LoadMonitored.blocked(Sampling.CAUSE_CLOCK, id_, null);
                    this.wait(); // wait for signal
                } catch (InterruptedException ie) {
                    throw new Error(ie); // that was unexpected...
                } finally {
                    LoadMonitored.unblocked(Sampling.CAUSE_CLOCK, id_, null);
                }
            }
        }
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
     * Advance to the next phase.  Increments the phase counter,
     * calls all advance listeners and then signals the activities
     * that are waiting to get them going again.
     */
    private synchronized boolean tryAdvance_() {
        boolean ret;
        // double check ...
        if ( (nowSet_.size() == 0) && 
             (pending_.size() == 0)) {
            
            this.phase_++;
            if (Sampling.SINGLETON != null)
                Sampling.SINGLETON.signalEvent(Sampling.EVENT_ID_CLOCK_ADVANCE, id_);
            // first notify everyone
            if (this.listener1_ != null) {
                this.listener1_.notifyAdvance();
                if (this.listeners_ != null) {
                    int size = listeners_.size();
                    for (int i=0;i<size;i++)
                        ((AdvanceListener)listeners_.get(i)).notifyAdvance();
                }
            }
            
            Iterator it = activities_.iterator();
            while (it.hasNext()) {
                Activity a = (Activity) it.next();
                pending_.add(a);
            }
            notifyAll();
            ret = true;
        } else 
            ret = false;
        return ret;
    }

    /**
     * Listener that tracks activity exits to ensure
     * dropping at the end.
     */
    private final ActivitySpawnListener dropListener_ = 
        new ActivitySpawnListener() {
        public void notifyActivitySpawn(Activity a,
                                        Activity i) {
        }
        public void notifyActivityTerminated(Activity a) {
            drop(a);
        }
    };

    /**
     * Listener that (transitively) adds all spawned activities to
     * the 'nowSet_'.    
     */
    private final ActivitySpawnListener nowSpawnListener_ = 
       new ActivitySpawnListener() {
       public void notifyActivitySpawn(Activity a,
                                       Activity i) {
           synchronized (Clock.this) {
               nowSet_.add(a);
           }
           // also register the spawned activity with this spawn listener
           // don't do it in the scope if the above lock due to risk of deadlock
           aip_.registerActivitySpawnListener(a, this);
       }
       public void notifyActivityTerminated(Activity a) {
           // assertion not valid - the activity might already have
           // been removed from the nowSet_ (this method might be 
           // called several times for the same activity -- see also 
           // DefaultRuntime_c::registerActivityStop) 
           //
           // observed that this assertion is violated 'sometimes' in 
           // Jacobi_skewed.
           // assert nowSet_.contains(a);
           synchronized (Clock.this) {
               nowSet_.remove(a);                
               tryAdvance_(); // must occur inside synchronized
           }
           
       }
   };
   
   /**
    * Callback method used by the Clock to notify all listeners
    * that the Clock is advancing into the next phase.
    *
    * @author Christian Grothoff
    */
   public interface AdvanceListener {
       public void notifyAdvance();
   }

} // end of Clock
