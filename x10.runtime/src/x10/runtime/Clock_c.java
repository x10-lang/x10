package x10.runtime;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import x10.lang.Activity;
import x10.lang.Clock;

/**
 * Implementation of Clock.  Technically the spec says that this is
 * a value class, but of course the implementation of clocks needs to
 * keep mutable state (somewhere).  Clocks are also integrated with the
 * Runtime and with places since they need to interact with activity
 * creation.  
 * 
 * @author Christian Grothoff
 */
final class Clock_c implements Clock {

    /**
     * The activity information provider that this clock can use.
     */
    private final ActivityInformationProvider aip_;
    
    /**
     * A Set of all activities registered with this clock.
     */
    private final Set activities_ = new HashSet();
   
    /**
     * The Set of all activities that have not yet completed in the
     * current cycle. 
     */
    private final Set pending_ = new HashSet();
    
    /**
     * The first registered advance listener.  null if none is
     * registered.
     */
    private AdvanceListener listener1_;
    
    /**
     * Lazy initialization (since we typically only have at most one
     * listener).
     */
    private Vector listeners_; // Vector<AdvanceListener>

    /**
     * The current phase of the Clock.  Incremented by one in each
     * phase (may wrap around!).
     */
    private int phase_;

    
    /**
     * Create a new Clock.  Registers the current activity with
     * the clock as a side-effect (see X10 Report).
     */
    Clock_c(ActivityInformationProvider aip) {
        this.aip_ = aip;
        register();
    }

    /**
     * Register the current activity with this clock.  While
     * the current spec does not allow this to happen other than
     * by creating the clock, I believe this is a problem with the
     * spec and we need some kind of method like this one.
     */
    public synchronized void register() {
        Activity a = aip_.getCurrentActivity();
        if (activities_.add(a))
            pending_.add(a);
    }
    
    /**
     * Execute the given activity.  The clock will not advance
     * into the next phase until the given activity and all
     * activities transitively started by a have completed.
     * 
     * @param a an activity to run
     */
    public void doNow(Activity a) {
        throw new Error("not implemented");
    }
    
    /**
     * Notify the clock that this activity has completed its
     * clocked activities in this cycle, that is the current
     * activity will not issue any further calls to 'now' until
     * it calls 'next'.
     *
     */
    public synchronized void doContinue() {
        Activity a = aip_.getCurrentActivity();
        pending_.remove(a);
        tryAdvance(); 
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
    public synchronized boolean drop() {
        Activity a = aip_.getCurrentActivity();
        boolean ret = activities_.remove(a);
        pending_.remove(a);
        tryAdvance(); 
        return ret;        
    }
    
    /**
     * Block until all clocks that this activity is registered with
     * have called continue (or next since next implies continue on
     * all registered clocks). 
     * 
     * Note that the semantics of this implementation are different
     * than what is stated in the X10 Report in that the programmer
     * does not specify the clocks but next applies to all clocks
     * that the activity is registered with.
     */
    public synchronized void doNext() {
        Activity a = aip_.getCurrentActivity();
        pending_.remove(a); // this one is done!
        if (tryAdvance())
            return; // we advanced, continue immediately!
        // wait for next phase
        int start = phase_;
        synchronized (a) {
            do {
                try {
                    a.wait(); // wait for signal
                } catch (InterruptedException ie) {
                    throw new Error(ie); // that was unexpected...
                }
            } while (start == phase_); // signal might be random in Java, check!
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
                this.listeners_ = new Vector();
            this.listeners_.add(al);
        }
    }
    
    /**
     * Some event happened that may trigger advancing the clock.
     * Check if this is the case and if so advance the clock.
     */
    private boolean tryAdvance() {
        if (pending_.size() == 0) {
            advance();
            return true;
        } else
            return false;
    }
    
    /**
     * Advance to the next phase.  Increments the phase counter,
     * calls all advance listeners and then signals the activities
     * that are waiting to get them going again.
     */
    private void advance() {
        assert pending_.size() == 0;
        this.phase_++;
        // first notify everyone
        if (this.listener1_ != null) {
            this.listener1_.notifyAdvance();
            if (this.listeners_ != null) {
                int size = listeners_.size();
                for (int i=0;i<size;i++)
                    ((AdvanceListener)listeners_.elementAt(i)).notifyAdvance();
            }
        }
        
        Iterator it = activities_.iterator();
        while (it.hasNext()) {
            Activity a = (Activity) it.next();
            pending_.add(a);
            synchronized (a) {
                a.notifyAll();
            }
        }
    }

 
} // end of Clock_c