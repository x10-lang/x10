package x10.runtime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;

import x10.lang.ClockUseException;
import x10.lang.Runtime;
import x10.lang.clock;

/**
 * An object on this VM that represents a real Clock running on some
 * other VM.
 *
 * This implementation is incorrect.  All resumes on this RemoteClock
 * should be collected before sending a single message to the real clock.
 *
 * @author Allan Kielstra
 **/

public class RemoteClock extends Clock {
    RemoteClock(String s, int vm_, long vm_clock_no_) {
        super(s);
        this.vm_ = vm_;
        this.vm_clock_no_ = vm_clock_no_;
    }

    public static RemoteClock getMyVersionOfClock(String s, int vm, long vm_clock_obj) {
        RemoteClock r;
        if ((r = (RemoteClock) RemoteObjectMap.get(vm, vm_clock_obj)) == null) {
            r = new RemoteClock(s, vm, vm_clock_obj);
            RemoteObjectMap.put(vm, vm_clock_obj, r);
        }
        //        RemoteObjectMap.dump(); // ahk
        return r;
    }
    
    private native void doNext_(long activityAsSeenByInvokingVM);
    public void doNext() {
        // we can't let the "next" proceed until all outstanding
        // resumes have been acknowledged
        synchronized (this) {
            Iterator rci = resumed_.iterator();
            while (rci.hasNext()) {
                Activity rca = (Activity) rci.next();
                boolean done = resumed_ack_.contains(rca);
                while (!done) {
                    // will this make it concurrency safe?
                    synchronized (resumed_ack_) {
                        done = resumed_ack_.contains(rca);
                    }
                }
            }
            // now tell the real clock to do a next
            doNext_(Runtime.getCurrentActivity().activityAsSeenByInvokingVM);
            // this will block on this side until the remote side is finished
            resumed_.clear();
            resumed_ack_.clear();
        }
    }
    
    private native void resume_(long activityAsSeenByInvokingVM);
    public void resume() {
        synchronized (this) {
            final Activity a = Runtime.getCurrentActivity();
            final long asSeenByInvokingVM = a.activityAsSeenByInvokingVM;
            final RemoteClock rc = this;
            if (!resumed_.contains(a)) {
                // Have another thread go tell the real clock that this
                // remote user has resumed
                resumed_.add(a);
                new Thread() {
                    public void run() {
                        rc.resume_(asSeenByInvokingVM);
                        // this may take some amount of time
                        synchronized (rc.resumed_ack_) {
                            // i wonder if this really is concurrency safe
                            rc.resumed_ack_.add(a);
                        }
                    }
                }.start();
            }
        }
    }

    public void drop() {
        Activity a = Runtime.getCurrentActivity();
        if (drop(a))
            a.dropClock( this );
    }
    
    synchronized boolean drop(Activity a) {
        boolean ret = activities_.remove(a);
        if (ret) activityCount_--;
        return ret;        
    }

    synchronized void register(Activity a ) {
        if (activities_.contains(a)) return;
        activities_.add(a);
        activityCount_++;
    }

    public static native void completeClockOp(long lapi_target, long lapi_target_addr);
    private final Set resumed_ = new HashSet();     // <Activity>
    private final Set resumed_ack_ = new HashSet(); // <Activity>
    /* The VM hosting the real Clock and the Clock object on
     * that VM.
     */
    private final int vm_;
    private final long vm_clock_no_;
}
