/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */
package x10.xrx;

import x10.compiler.*;

import x10.array.Array_2;
import x10.array.Array_3;
import x10.io.CustomSerialization;
import x10.io.Deserializer;
import x10.io.Serializer;
import x10.util.concurrent.AtomicInteger;
import x10.util.concurrent.SimpleLatch;
import x10.util.GrowableRail;
import x10.util.HashMap;
import x10.util.HashSet;

/**
 * Place0-based Resilient Finish
 * This version is optimized and does not use ResilientStorePlace0
 */
final class FinishResilientPlace0 extends FinishResilient implements CustomSerialization {
    private static val verbose = FinishResilient.verbose;
    private static val place0 = Place.FIRST_PLACE;

    private static val AT = 0;
    private static val ASYNC = 1;
    private static val AT_AND_ASYNC = AT..ASYNC;

    private static struct Id(home:int,id:int) {
        public def toString() = "<"+home+","+id+">";
    }
    private static val UNASSIGNED = Id(-1n,-1n);

    /**
     * State of a single finish; always stored in Place0
     */
    private static final class State implements x10.io.Unserializable {
        val NUM_PLACES = Place.numPlaces(); // FIXME: Elastic X10: totally broken if async goes to new place!!!
        val gfs:GlobalRef[FinishResilientPlace0]; // root finish state
        val id:Id;
        var numActive:Long = 0;
        val transit = new Array_3[Int](2, NUM_PLACES, NUM_PLACES);        // TODO: sparse; HashMap?
        val transitAdopted = new Array_3[Int](2, NUM_PLACES, NUM_PLACES); // TODO: sparse; HashMap?
        val live = new Array_2[Int](2, NUM_PLACES);
        val liveAdopted = new Array_2[Int](2, NUM_PLACES);
        val excs = new GrowableRail[CheckedThrowable](); // exceptions to report
        val children = new x10.util.GrowableRail[Id](); // children
        var adopterId:Id = UNASSIGNED; // adopter (if adopted)
        val parentId:Id; // parent (or UNASSIGNED)

        private def this(id:Id, parentId:Id, gfs:GlobalRef[FinishResilientPlace0]) {
            this.id = id;
            this.parentId = parentId; 
            this.gfs = gfs;
        }
        
        def isAdopted() = (adopterId != UNASSIGNED);

        def getCurrentAdopter():State {
            var s:State = this;
            while (s.isAdopted()) {
                s = states.get(s.adopterId);
            }
            return s;
        }

        def releaseLatch() {
	    if (isAdopted()) {
                if (verbose>=1) debug("releaseLatch(id="+id+") called on adopted finish; not releasing latch");
            } else {
                val exceptions = (excs == null || excs.isEmpty()) ?  null : excs.toRail();
                if (verbose>=2) debug("releasing latch id="+id+(exceptions == null ? " no exceptions" : " with exceptions"));

                val mygfs = gfs;
                val tmpId = id;
                try {
                    at (mygfs.home) @Immediate("releaseLatch_gfs_home") async {
                        if (verbose>=2) debug("performing releae for "+tmpId+" at "+here);
                        val fs = mygfs();
                        if (exceptions != null) {
                            fs.latch.lock();
                            if (fs.excs == null) fs.excs = new GrowableRail[CheckedThrowable](exceptions.size);
                            fs.excs.addAll(exceptions);
                            fs.latch.unlock();
                        }
                        fs.latch.release();
                     }
                } catch (dpe:DeadPlaceException) {
                    // can ignore; if the place is dead there is no need to unlatch a waiting activity there
                    if (verbose>=2) debug("caught and suppressed DPE when attempting to release latch for "+id);
                }
            }
            if (verbose>=2) debug("releaseLatch(id="+id+") returning");
        }

        def quiescent():Boolean {
            if (verbose>=2) debug("quiescent(id="+id+") called");

            if (isAdopted()) {
                if (verbose>=2) debug("quiescent(id="+id+") returning false, already adopted by adopterId=="+adopterId);
                return false;
            }
        
	    if (numActive < 0) {
                debug("COUNTING ERROR: quiescent(id="+id+") negative numActive!!!");
                dump();
                return true; // TODO: This really should be converted to a fatal error....
            }
        
            val quiet = numActive == 0;
            if (verbose>=3) dump();
            if (verbose>=2) debug("quiescent(id="+id+") returning " + quiet);
            return quiet;
        }

        def dump() {
            val s = new x10.util.StringBuilder();
            s.add("State dump:\n");
            s.add("             id:" + id); s.add('\n');
            s.add("      numActive:"); s.add(numActive); s.add('\n');
            s.add("           live:"); s.add(live.toString(1024)); s.add('\n');
            s.add("    liveAdopted:"); s.add(liveAdopted.toString(1024)); s.add('\n');
            s.add("        transit:"); s.add(transit.toString(1024)); s.add('\n');
            s.add(" transitAdopted:"); s.add(transitAdopted.toString(1024)); s.add('\n');
            s.add("  children.size: " + children.size()); s.add('\n');
            s.add("      adopterId: " + adopterId); s.add('\n');
            s.add("       parentId: " + parentId);
            debug(s.toString());
        }
    }
    
    private static val states = (here.id==0) ? new HashMap[Id, State]() : null;

    private static val lock = (here.id==0) ? new x10.util.concurrent.Lock() : null;

    private static val nextId = new AtomicInteger(); // per-place portion of unique id
    
    private val id:Id;

    // Initialized by custom deserializer
    private val grlc:GlobalRef[AtomicInteger];
    private transient var isGlobal:Boolean = false;
    private transient val ref:GlobalRef[FinishResilientPlace0] = GlobalRef[FinishResilientPlace0](this);

    // These fields are only valid / used in the root finish instance.
    private transient var latch:SimpleLatch; 
    private transient var parent:FinishState;
    private transient var excs:GrowableRail[CheckedThrowable]; 

    private def localCount():AtomicInteger = (grlc as GlobalRef[AtomicInteger]{self.home == here})();

    public def toString():String = "FinishResilientPlace0(id="+id+", localCount="+localCount().get()+")";

    private def this(p:FinishState) { 
        latch = new SimpleLatch();
        grlc = GlobalRef[AtomicInteger](new AtomicInteger(1n)); // for myself.  Will be decremented in waitForFinish
        parent = p;
        isGlobal = false;
        id = Id(here.id as Int, nextId.getAndIncrement());
    }

    private def this(deser:Deserializer) {
        id = deser.readAny() as Id;
        val lc = deser.readAny() as GlobalRef[AtomicInteger];
        grlc = (lc.home == here) ? lc : GlobalRef[AtomicInteger](new AtomicInteger(1n));
        isGlobal = true;
    }

    public def serialize(ser:Serializer) {
        if (!isGlobal) globalInit(); // Once we have more than 1 copy of the finish state, we must go global
        ser.writeAny(id);
        ser.writeAny(grlc);
    }

    private def globalInit() {
        latch.lock();
        if (!isGlobal) {
            if (verbose>=1) debug(">>>> doing globalInit for id="+id);
            val parentId:Id;
            if (parent instanceof FinishResilientPlace0) {
                val frParent = parent as FinishResilientPlace0;
                if (!frParent.isGlobal) frParent.globalInit();
                parentId = frParent.id;
            } else {
                parentId = UNASSIGNED;
            }
            val gfs = this.ref;
            val myId = this.id;
            Runtime.runImmediateAt(place0, ()=>{ 
                try {
                    lock.lock();
                    val state = new State(myId, parentId, gfs);
                    states.put(myId, state);
                    state.live(ASYNC, gfs.home.id) = 1n; // duplicated from my localCount
                    state.numActive = 1;
                    if (parentId != UNASSIGNED) states.get(parentId).children.add(myId);
                } finally {
                    lock.unlock();
                }
            });
            isGlobal = true;
            if (verbose>=1) debug("<<<< globalInit returning fs="+this);
        }
        latch.unlock();
    }
    
    static def make(parent:FinishState) {
        val fs = new FinishResilientPlace0(parent);
        return fs;
    }

    static def notifyPlaceDeath():void {
        if (verbose>=1) debug(">>>> notifyPlaceDeath called");
        if (here != place0) {
            if (verbose>=2) debug("not place0, returning");
            return;
        }
        try {
            lock.lock();
            for (e in states.entries()) {
                processPlaceDeath(e.getValue());
            }
            val toRemove = new HashSet[Id]();
            for (e in states.entries()) {
                val s = e.getValue();
                if (s.quiescent()) {
                    s.releaseLatch();
                    toRemove.add(s.id);
                }
            }
            for (id in toRemove) {
                states.remove(id);
            }
        } finally {
            lock.unlock();
        }
        if (verbose>=1) debug("<<<< notifyPlaceDeath returning");
    }

    def notifySubActivitySpawn(place:Place):void {
        notifySubActivitySpawn(place, ASYNC);
    }
    def notifyShiftedActivitySpawn(place:Place):void {
        notifySubActivitySpawn(place, AT);
    }
    def notifySubActivitySpawn(place:Place, kind:long):void {
        val srcId = here.id;
        val dstId = place.id;
        val myId = this.id;
        if (dstId == srcId) {
            val lc = localCount().incrementAndGet();
            if (verbose>=1) debug(">>>> notifySubActivitySpawn(id="+myId+") called locally, localCount now "+lc);
        } else {
            if (!isGlobal) globalInit();
            if (verbose>=1) debug(">>>> notifySubActivitySpawn(id="+myId+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);

            Runtime.runImmediateAt(place0, ()=>{ 
                try {
                    lock.lock();
                    val state = states.get(myId);
                    if (Place(srcId).isDead()) {
                        if (verbose>=1) debug("==== notifySubActivitySpawn(id="+myId+") src "+srcId + "is dead; dropping async");
                    } else if (Place(dstId).isDead()) {
                        if (kind == ASYNC) {
                            if (verbose>=1) debug("==== notifySubActivitySpawn(id="+myId+") destination "+dstId + "is dead; pushed DPE for async");
                            addDeadPlaceException(state, dstId);
                        } else {
                            if (verbose>=1) debug("==== notifySubActivitySpawn(id="+myId+") destination "+dstId + "is dead; dropped at");
                        }
                    } else {
                        if (!state.isAdopted()) {
                            state.transit(kind, srcId, dstId)++;
                            state.numActive++;
                        } else {
                            val adopterState = state.getCurrentAdopter();
                            adopterState.transitAdopted(kind, srcId, dstId)++;
                            adopterState.numActive++;
                        }
                        if (verbose>=3) {
                            debug("==== notifySubActivitySpwan(id="+myId+") after update for: "+srcId + " ==> "+dstId+" kind="+kind);
                            state.dump();
                        }
                    }
                } finally {
                    lock.unlock();
                }
            });
        }
        if (verbose>=1) debug("<<<< notifySubActivitySpawn(id="+myId+") returning");
    }

    def notifyRemoteContinuationCreated():void { 
        if (!isGlobal) globalInit();
    }

    /*
     * This method can't block because it may run on an @Immediate worker.  
     * Therefore it can't use Runtime.runImmediateAt.
     * Instead sequence @Immediate messages to do the nac to place0 and
     * then come back and submit the pending activity.
     * Because place0 can't fail, we know that if the first message gets
     * to place0, the message back to push the activity will eventually
     * be received (unless dstId's place fails, in which case it doesn't matter).
     */
    def notifyActivityCreation(srcPlace:Place, activity:Activity):Boolean {
        return notifyActivityCreation(srcPlace, activity, ASYNC);
    }
    def notifyActivityCreation(srcPlace:Place, activity:Activity, kind:long):Boolean {
        val srcId = srcPlace.id; 
        val dstId = here.id;
        val myId = this.id;
        if (srcId == dstId) {
            if (verbose>=1) debug(">>>> notifyActivityCreation(id="+myId+") called locally. no action requred");
            return true;
        }

        if (verbose>=1) debug(">>>> notifyActivityCreation(id="+myId+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);
        val pendingActivity = GlobalRef(activity); 
        at (place0) @Immediate("notifyActivityCreation_to_zero") async {
            var shouldSubmit:Boolean = true;
            try {
                lock.lock();
                if (Place(srcId).isDead() || Place(dstId).isDead()) {
                    // NOTE: no state updates or DPE processing here.
		    //       Must happen exactly once and is done
                    //       when Place0 is notified of a dead place.
                    if (verbose>=1) debug("==== notifyActivityCreation(id="+myId+") suppressed: "+srcId + " ==> "+dstId+" kind="+kind);
                    shouldSubmit = false;
                } else {
                    val state = states.get(myId);
                    if (!state.isAdopted()) {
                        state.live(kind, dstId)++;
                        state.transit(kind, srcId, dstId)--;
                    } else {
                        val adopterState = state.getCurrentAdopter();
                        adopterState.liveAdopted(kind, dstId)++;
                        adopterState.transitAdopted(kind, srcId, dstId)--;
                    }
                    if (verbose>=3) {
                        debug("==== notifyActivityCreation(id="+myId+") after update for: "+srcId + " ==> "+dstId+" kind="+kind);
                        state.dump();
                    }
                }
            } finally {
                lock.unlock();
            }
            if (shouldSubmit) {
                at (pendingActivity) @Immediate("notifyActivityCreation_push_activity") async {
                    val pa = pendingActivity();
                    if (pa != null && pa.epoch == Runtime.epoch()) {
                        if (verbose>=1) debug("<<<< notifyActivityCreation(id="+myId+") finally submitting activity");
                        Runtime.worker().push(pa);
                    }
                    pendingActivity.forget();
                }
            } else {
                at (pendingActivity) @Immediate("notifyActivityCreation_forget_activity") async {
                    pendingActivity.forget();
                }
            }
        };

        // Return false because we want to defer pushing the activity.
        return false;                
    }

    def notifyShiftedActivityCreation(srcPlace:Place):Boolean {
        val kind = AT;
        val srcId = srcPlace.id; 
        val dstId = here.id;
        val myId = this.id;
        if (verbose>=1) debug(">>>> notifyShiftedActivityCreation(id="+myId+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);

        return Runtime.evalImmediateAt[Boolean](place0, ()=> {
            try {
                lock.lock();
                if (Place(srcId).isDead() || Place(dstId).isDead()) {
                    // NOTE: no state updates or DPE processing here.
		    //       Must happen exactly once and is done
                    //       when Place0 is notified of a dead place.
                    if (verbose>=1) debug("==== notifyShiftedActivityCreation(id="+myId+") suppressed: "+srcId + " ==> "+dstId+" kind="+kind);
                    return false;
                }
                val state = states.get(myId);
                if (!state.isAdopted()) {
                    state.live(kind, dstId)++;
                    state.transit(kind, srcId, dstId)--;
                } else {
                    val adopterState = state.getCurrentAdopter();
                    adopterState.liveAdopted(kind, dstId)++;
                    adopterState.transitAdopted(kind, srcId, dstId)--;
                }
            } finally {
                lock.unlock();
            }
            return true;
        });
    }

    def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void { 
        notifyActivityCreationFailed(srcPlace, t, ASYNC);
    }
    def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable, kind:long):void { 
        val srcId = srcPlace.id;
        val dstId = here.id;
        val myId = this.id;

        if (verbose>=1) debug(">>>> notifyActivityCreationFailed(id="+myId+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);

        if (!isGlobal) globalInit();

        at (place0) @Immediate("notifyActivityCreationFailed_to_zero") async {
            try {
                lock.lock();
                if (Place(srcId).isDead() || Place(dstId).isDead()) {
                    // NOTE: no state updates or DPE processing here.
		    //       Must happen exactly once and is done
                    //       when Place0 is notified of a dead place.
                    if (verbose>=1) debug("==== notifyActivityCreationFailed(id="+myId+") suppressed: "+srcId + " ==> "+dstId+" kind="+kind);
                } else {
                    if (verbose>=1) debug(">>>> notifyActivityCreatedFailed(id="+myId+") message running at place0");
                    val state = states.get(myId);
                    if (!state.isAdopted()) {
                        state.transit(kind, srcId, dstId)--;
                        state.numActive--;
                        state.excs.add(t);
                        if (state.quiescent()) {
                            state.releaseLatch();
                            states.remove(myId);
                        }
                    } else {
                        val adopterState = state.getCurrentAdopter();
                        adopterState.transitAdopted(kind, srcId, dstId)--;
                        adopterState.numActive--;
                        adopterState.excs.add(t);
                        if (adopterState.quiescent()) {
                            adopterState.releaseLatch();
                            states.remove(myId);
                            states.remove(adopterState.id);
                        }
                    }
                }
            } finally {
                lock.unlock();
            }
       };

       if (verbose>=1) debug("<<<< notifyActivityCreationFailed(id="+myId+") returning, srcId="+srcId + " dstId="+dstId);
    }

    def notifyActivityCreatedAndTerminated(srcPlace:Place) {
        notifyActivityCreatedAndTerminated(srcPlace, ASYNC);
    }
    def notifyActivityCreatedAndTerminated(srcPlace:Place, kind:long) {
        val srcId = srcPlace.id; 
        val dstId = here.id;
        val myId = this.id;

        if (dstId == srcId) {
            val lc = localCount().decrementAndGet();
            if (verbose>=1) debug(">>>> notifyActivityCreatedAndTerminated(id="+myId+") called locally, localCount now "+lc);
            if (lc > 0) return;
            if (isGlobal) {
                // if srcId == dstId, then notifySubActivitySpawn transitions to live (not transit)
                // so we need to decrement accordingly here and then check for quiescence.
                at (place0) @Immediate("notifyActivityCreatedAndTerminated_quiescence_check_to_zero") async {
                    try {
                        lock.lock();
                        val state = states.get(myId);
                        if (!state.isAdopted()) {
                            state.live(kind, srcId)--;
                            state.numActive--;
                            if (state.quiescent()) {
                                state.releaseLatch();
                                states.remove(myId);
                            }
                        } else {
                            val adopterState = state.getCurrentAdopter();
                            adopterState.live(kind, srcId)--;
                            adopterState.numActive--;
                            if (adopterState.quiescent()) {
                                adopterState.releaseLatch();
                                states.remove(myId);
                                states.remove(adopterState.id);
                            }
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            } else {
                if (verbose>=1) debug(">>>> notifyActivityCreatedAndTerminated(id="+myId+") quiescent local finish; releasing latch");
                latch.release();
                return;
            }
            return; 
        }

        if (verbose>=1) debug(">>>> notifyActivityCreatedAndTerminated(id="+myId+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);
        at (place0) @Immediate("notifyActivityCreatedAndTerminated_to_zero") async {
            try {
                lock.lock();
                if (Place(srcId).isDead() || Place(dstId).isDead()) {
                    // NOTE: no state updates or DPE processing here.
		    //       Must happen exactly once and is done
                    //       when Place0 is notified of a dead place.
                    if (verbose>=1) debug("==== notifyActivityCreatedAndTerminated(id="+myId+") suppressed: "+srcId + " ==> "+dstId+" kind="+kind);
                } else {
                    if (verbose>=1) debug(">>>> notifyActivityCreatedAndTerminated(id="+myId+") message running at place0");
                    val state = states.get(myId);
                    if (!state.isAdopted()) {
                        state.transit(kind, srcId, dstId)--;
                        state.numActive--;
                        if (state.quiescent()) {
                            state.releaseLatch();
                            states.remove(myId);
                        }
                    } else {
                        val adopterState = state.getCurrentAdopter();
                        adopterState.transitAdopted(kind, srcId, dstId)--;
                        adopterState.numActive--;
                        if (adopterState.quiescent()) {
                            adopterState.releaseLatch();
                            states.remove(myId);
                            states.remove(adopterState.id);
                        }
                    }
                }
            } finally {
                lock.unlock();
            }
       };
    }

    def notifyActivityTermination():void {
        notifyActivityTermination(ASYNC);
    }
    def notifyShiftedActivityCompletion():void {
        notifyActivityTermination(AT);
    }
    def notifyActivityTermination(kind:long):void {
        val lc = localCount().decrementAndGet();
        val myId = this.id;

        if (lc > 0) {
            if (verbose>=1) debug(">>>> notifyActivityTermination(id="+myId+") called, decremented localCount to "+lc);
            return;
        }

        // If this is not the root finish, we are done with the finish state.
        // If this is the root finish, it will be kept alive because waitForFinish
        // is an instance method and it is on the stack of some activity.
        (ref as GlobalRef[FinishResilientPlace0]{self.home == here}).forget();

        if (!isGlobal) {
            if (verbose>=1) debug(">>>> notifyActivityTermination(id="+myId+") zero localCount on local finish; releasing latch");
            latch.release();
            return;
        } 

        val dstId = here.id;
        if (verbose>=1) debug(">>>> notifyActivityTermination(id="+myId+") called, dstId="+dstId+" kind="+kind);
        at (place0) @Immediate("notifyActivityTermination_to_zero") async {
            try {
                lock.lock();
                if (Place(dstId).isDead()) {
                    // NOTE: no state updates or DPE processing here.
		    //       Must happen exactly once and is done
                    //       when Place0 is notified of a dead place.
                    if (verbose>=1) debug("==== notifyActivityTermination(id="+myId+") suppressed: "+dstId+" kind="+kind);
                } else {
                    if (verbose>=1) debug("<<<< notifyActivityTermination(id="+myId+") message running at place0");
                    val state = states(myId);
                    if (!state.isAdopted()) {
                        state.live(kind, dstId)--;
                        state.numActive--;
                        if (state.quiescent()) {
                            state.releaseLatch();
                            states.remove(myId);
                        }
                    } else {
                        val adopterState = state.getCurrentAdopter();
                        adopterState.liveAdopted(kind, dstId)--;
                        adopterState.numActive--;
                        if (adopterState.quiescent()) {
                            adopterState.releaseLatch();
                            states.remove(myId);
                            states.remove(adopterState.id);
                        }
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    def pushException(t:CheckedThrowable):void {
        val myId = this.id;
        if (!isGlobal) {
            latch.lock();
            if (verbose>=1) debug(">>>> pushException(id="+myId+") locally pushing exception "+t);
            if (excs == null) excs = new GrowableRail[CheckedThrowable]();
            excs.add(t);
            latch.unlock();
        } else {
            if (verbose>=1) debug(">>>> pushException(id="+myId+") called, t="+t);
            Runtime.runImmediateAt(place0, ()=>{ 
                try {
                    lock.lock();
                    val state = states(myId);
                    state.excs.add(t); // NB: if adopted, semantics say to suppress exception.  So don't both checking for adopterId.
                } finally {
                    lock.unlock();
                }
            });
            if (verbose>=1) debug("<<<< pushException(id="+myId+") returning");
        }
    }

    def waitForFinish():void {
        if (verbose>=1) debug(">>>> waitForFinish(id="+id+") called");

        // terminate myself
        notifyActivityTermination();

        // If we haven't gone remote with this finish yet, see if this worker
        // can execute other asyncs that are governed by the finish before waiting on the latch.
        if ((!Runtime.STRICT_FINISH) && (Runtime.STATIC_THREADS || !isGlobal)) {
            if (verbose>=2) debug("calling worker.join for id="+id);
            Runtime.worker().join(this.latch);
        }

        // wait for the latch release
        if (verbose>=2) debug("calling latch.await for id="+id);
        latch.await(); // wait for the termination (latch may already be released)
        if (verbose>=2) debug("returned from latch.await for id="+id);

        // no more messages will come back to this finish state 
        (ref as GlobalRef[FinishResilientPlace0]{self.home == here}).forget();
        
        // get exceptions and throw wrapped in a ME if there are any
        if (excs != null) throw new MultipleExceptions(excs);
    }

    /*
     * We have two options for spawning a remote async.
     *
     * The first (indirect) is most appropriate for "fat" asyncs whose
     * serialized form is a very large message.
     *   - notifySubActivitySpawn: src ===> Place0 (increment transit(src,dst)
     *   - x10rtSendAsync:         src ===> dst (send "fat" async body to dst)
     *   - notifyActivityCreation: dst ===> Place0 (decrement transit, increment live)
     *
     * The second (direct) is best for "small" asyncs, since it
     * reduces latency and only interacts with Place0 once, but
     * requires the async body to be bundled with the finish state
     * control messages, and thus sent on the network twice instead of once.
     *
     * We dynamically select the protocol on a per-async basis by comparing
     * the serialized size of body to a size threshold.
     */
    def spawnRemoteActivity(place:Place, body:()=>void, prof:x10.xrx.Runtime.Profile):void {
        val start = prof != null ? System.nanoTime() : 0;
        val ser = new Serializer();
        ser.writeAny(body);
        if (prof != null) {
            val end = System.nanoTime();
            prof.serializationNanos += (end-start);
            prof.bytes += ser.dataBytesWritten();
        }
        val bytes = ser.toRail();

        if (!isGlobal) globalInit();
        val srcId = here.id;
        val dstId = place.id;
        val myId = this.id;
        if (bytes.size >= ASYNC_SIZE_THRESHOLD) {
            if (verbose >= 1) debug("==== spawnRemoteActivity(id="+myId+") selecting indirect (size="+
                                    bytes.size+") srcId="+srcId + " dstId="+dstId);
            val preSendAction = ()=>{ this.notifySubActivitySpawn(place); };
            val wrappedBody = ()=> @x10.compiler.AsyncClosure {
                val deser = new x10.io.Deserializer(bytes);
                val bodyPrime = deser.readAny() as ()=>void;
                bodyPrime();
            };
            x10.xrx.Runtime.x10rtSendAsync(place.id, wrappedBody, this, prof, preSendAction);
        } else {
            if (verbose >= 1) debug(">>>>  spawnRemoteActivity(id="+myId+") selecting direct (size="+
                                    bytes.size+") srcId="+srcId + " dstId="+dstId);

            localCount().incrementAndGet();  // synthetic activity to keep finish locally live during async to Place0
            val fsgr = this.ref;
            at (place0) @Immediate("spawnRemoteActivity_to_zero") async {
                try {
                    lock.lock();
                    val state = states(myId);
                    if (Place(srcId).isDead()) {
                        if (verbose>=1) debug("==== spwanRemoteActivity(id="+myId+") src "+srcId + "is dead; dropping async");
                    } else if (Place(dstId).isDead()) {
                        if (verbose>=1) debug("==== spawnRemoteActivity(id="+myId+") destination "+dstId + "is dead; pushed DPE");
                        addDeadPlaceException(state, dstId);
                    } else {
                        if (!state.isAdopted()) {
                            state.live(ASYNC, dstId)++;
                            state.numActive++;
                        } else {
                            val adopterState = state.getCurrentAdopter();
                            adopterState.liveAdopted(ASYNC, dstId)++;
                            adopterState.numActive++;
                        }
                        if (verbose>=3) {
                            debug("==== spawnRemoteActivity(id="+myId+") after update for: "+srcId + " ==> "+dstId);
                            state.dump();
                        }
                    }
                } finally {
                    lock.unlock();
                }
                at (fsgr) @Immediate("spawnRemoteActivity_dec_local_count") async {
                    fsgr().notifyActivityTermination(); // end of synthetic local activity
                }
                at (Place(dstId)) @Immediate("spawnRemoteActivity_dstPlace") async {
                    if (verbose >= 1) debug("==== spawnRemoteActivity(id="+myId+") submitting activity from "+srcId+" at "+dstId);
                    val wrappedBody = ()=> {
                        // defer deserialization to reduce work on immediate thread
                        val deser = new x10.io.Deserializer(bytes);
                        val bodyPrime = deser.readAny() as ()=>void;
                        bodyPrime();
                    };
                    Runtime.worker().push(new Activity(42, wrappedBody, this));
               }
            }
            if (verbose>=1) debug("<<<< spawnRemoteActivity(id="+myId+") returning");
        }
    }

    // TODO: this should be an instance method of State.
    private static def processPlaceDeath(state:State) {

        if (state.isAdopted()) return; // nothing to do.

        // 1 pull up dead children
        val children = state.children;
        for (var chIndex:Long = 0; chIndex < children.size(); ++chIndex) {
            val childId = children(chIndex);
            val childState = states(childId);
            if (childState==null) continue;
            if (!childState.gfs.home.isDead()) continue;
            val lastChildId = children.removeLast();
            if (chIndex < children.size()) children(chIndex) = lastChildId;
            chIndex--; // don't advance this iteration
            // adopt the child
            if (verbose>=3) debug("adopting childId="+childId);
            assert !childState.isAdopted();
            childState.adopterId = state.id;
            state.children.addAll(childState.children); // will be checked in later iteration since addAll appends
            for (k in AT_AND_ASYNC) {
                for (i in 0..(state.NUM_PLACES-1)) {
                    state.liveAdopted(k,i) += (childState.live(k,i) + childState.liveAdopted(k,i));
                    for (j in 0..(state.NUM_PLACES-1)) {
                        state.transitAdopted(k, i, j) += (childState.transit(k, i, j) + childState.transitAdopted(k, i, j));
                    }
                }
            }
            state.numActive += childState.numActive;
        } // for (chIndex)

        // 2 clear dead entries and create DPEs
        for (i in 0..(state.NUM_PLACES-1)) {
            if (Place.isDead(i)) {
                for (1..state.live(ASYNC, i)) {
                    if (verbose>=3) debug("adding DPE for live asyncs("+i+")");
                    addDeadPlaceException(state, i);
                }
                state.numActive -= ( state.live(AT, i) + state.liveAdopted(AT, i) + state.live(ASYNC, i) + state.liveAdopted(ASYNC, i));
                state.live(AT, i) = 0n; state.liveAdopted(AT, i) = 0n;
                state.live(ASYNC, i) = 0n; state.liveAdopted(ASYNC, i) = 0n;
                for (j in 0..(state.NUM_PLACES-1)) {
                    state.numActive -= (state.transit(AT,i,j) + state.transitAdopted(AT,i,j) + state.transit(ASYNC,i,j) + state.transitAdopted(ASYNC,i,j));
                    state.transit(AT,i,j) = 0n; state.transitAdopted(AT,i,j) = 0n;
                    state.transit(ASYNC,i,j) = 0n; state.transitAdopted(ASYNC,i,j) = 0n;
                    for (1..state.transit(ASYNC, j,i)) {
                        if (verbose>=3) debug("adding DPE for transit asyncs("+j+","+i+")");
                        addDeadPlaceException(state, i);
                    }
                    state.numActive -= (state.transit(AT,j,i) + state.transitAdopted(AT,j,i) + state.transit(ASYNC,j,i) + state.transitAdopted(ASYNC,j,i));
                    state.transit(AT,j,i) = 0n; state.transitAdopted(AT,j,i) = 0n;
                    state.transit(ASYNC,j,i) = 0n; state.transitAdopted(ASYNC,j,i) = 0n;
                }
            }
        }
    }

    private static def addDeadPlaceException(state:State, placeId:Long) {
        val e = new DeadPlaceException(Place(placeId));
        e.fillInStackTrace(); // meaningless?
        state.excs.add(e);
    }
}
