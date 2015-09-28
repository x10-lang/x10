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
import x10.io.Serializer;
import x10.io.Deserializer;
import x10.util.concurrent.SimpleLatch;

/**
 * Place0-based Resilient Finish
 * This version is optimized and does not use ResilientStorePlace0
 */
final class FinishResilientPlace0 extends FinishResilient implements Runtime.Mortal {
    private static val verbose = FinishResilient.verbose;
    private static val place0 = Place.FIRST_PLACE;

    private static val AT = 0;
    private static val ASYNC = 1;
    private static val AT_AND_ASYNC = AT..ASYNC;

    /**
     * State of a single finish; always stored in Place0
     */
    private static final class State implements x10.io.Unserializable {
        val NUM_PLACES = Place.numPlaces();
        var numActive:Long = 0;
        val transit = new Array_3[Int](2, NUM_PLACES, NUM_PLACES);
        val transitAdopted = new Array_3[Int](2, NUM_PLACES, NUM_PLACES);
        val live = new Array_2[Int](2, NUM_PLACES);
        val liveAdopted = new Array_2[Int](2, NUM_PLACES);
        val excs = new x10.util.GrowableRail[CheckedThrowable](); // exceptions to report
        val children = new x10.util.GrowableRail[Long](); // children
        var adopterId:Long = -1; // adopter (if adopted)
        
        val parentId:Long; // parent (or -1)
        val gfs:GlobalRef[FinishResilientPlace0]; // root finish state

        private def this(parentId:Long, gfs:GlobalRef[FinishResilientPlace0]) {
            this.parentId = parentId; 
            this.gfs = gfs;
        }
        
        def isAdopted() = (adopterId != -1);

        def dump(msg:String) {
            val s = new x10.util.StringBuilder(); s.add(msg); s.add('\n');
            s.add("      numActive:"); s.add(numActive); s.add('\n');
            s.add("           live:"); s.add(live.toString(1024)); s.add('\n');
            s.add("    liveAdopted:"); s.add(liveAdopted.toString(1024)); s.add('\n');
            s.add("        transit:"); s.add(transit.toString(1024)); s.add('\n');
            s.add(" transitAdopted:"); s.add(transitAdopted.toString(1024)); s.add('\n');
            s.add("  children.size: " + children.size()); s.add('\n'); s.add('\n');
            s.add("      adopterId: " + adopterId); s.add('\n');
            s.add("       parentId: " + parentId);
            debug(s.toString());
        }
    }
    
    // TODO: freelist to reuse ids (maybe also states)
    //       or perhaps switch to HashMap[Long,State] instead of GrowableRail
    private static val states = (here.id==0) ? new x10.util.GrowableRail[State]() : null;

    private static val lock = (here.id==0) ? new x10.util.concurrent.Lock() : null;
    
    private var id:Long = -2;
    private transient val latch:SimpleLatch;         // only non-null on root FS
    private transient var excs:MultipleExceptions;   // set by place0 when latch is released
    private transient var hasRemote:Boolean = false; // only used by initiating activity in waitForFinish

    public def toString():String = "FinishResilientPlace0(id="+id+")";
    private def this() { 
        latch = new SimpleLatch();
    }
    
    static def make(parent:FinishState):FinishResilient {
        if (verbose>=1) debug(">>>> make called, parent="+parent);
        val parentId = (parent instanceof FinishResilientPlace0) ? (parent as FinishResilientPlace0).id : -1; // ok to ignore other cases?
        val fs = new FinishResilientPlace0();
        val gfs = GlobalRef[FinishResilientPlace0](fs);
        fs.id = Runtime.evalImmediateAt[Long](place0, ()=>{ 
            try {
                lock.lock();
                val id = states.size();
                val state = new State(parentId, gfs);
                states.add(state);
                state.live(ASYNC,gfs.home.id) = 1n; // for myself, will be decremented in waitForFinish
                state.numActive = 1;
                if (parentId != -1) states(parentId).children.add(id);
                return id;
            } finally {
                lock.unlock();
            }
        });
        if (verbose>=1) debug("<<<< make returning fs="+fs);
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
            for (id in 0..(states.size()-1)) {
                processPlaceDeath(id);
            }
            for (id in 0..(states.size()-1)) {
                if (quiescent(id)) releaseLatch(id);
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
        if (dstId != srcId) hasRemote = true;
        if (verbose>=1) debug(">>>> notifySubActivitySpawn(id="+id+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);
        Runtime.runImmediateAt(place0, ()=>{
            try {
                lock.lock();
                val state = states(id);
                if (Place(srcId).isDead()) {
                    if (verbose>=1) debug("==== notifySubActivitySpawn(id="+id+") src "+srcId + "is dead; dropping async");
                    return;
                }
                if (Place(dstId).isDead()) {
                    if (kind == ASYNC) {
                        if (verbose>=1) debug("==== notifySubActivitySpawn(id="+id+") destination "+dstId + "is dead; pushed DPE for async");
                        addDeadPlaceException(state, dstId);
                    } else {
                        if (verbose>=1) debug("==== notifySubActivitySpawn(id="+id+") destination "+dstId + "is dead; dropped at");
                    }
                    return;
                }
                if (!state.isAdopted()) {
                    state.transit(kind, srcId, dstId)++;
                    state.numActive++;
                } else {
                    val adopterId = getCurrentAdopterId(id);
                    val adopterState = states(adopterId);
                    adopterState.transitAdopted(kind, srcId, dstId)++;
                    adopterState.numActive++;
                }
                if (verbose>=3) state.dump("DUMP id="+id);
            } finally {
                lock.unlock();
            }
        });
        if (verbose>=1) debug("<<<< notifySubActivitySpawn(id="+id+") returning");
    }

    def notifyRemoteContinuationCreated():void { 
        hasRemote = true;
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
        if (verbose>=1) debug(">>>> notifyActivityCreation(id="+id+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);

        val pendingActivity = GlobalRef(activity); 
        at (place0) @Immediate("notifyActivityCreation_to_zero") async {
            var shouldSubmit:Boolean = true;
            try {
                lock.lock();
                if (Place(srcId).isDead() || Place(dstId).isDead()) {
                    // NOTE: no state updates or DPE processing here.
		    //       Must happen exactly once and is done
                    //       when Place0 is notified of a dead place.
                    if (verbose>=1) debug("==== notifyActivityCreation(id="+id+") suppressed: "+srcId + " ==> "+dstId+" kind="+kind);
                    shouldSubmit = false;
                } else {
                    val state = states(id);
                    if (!state.isAdopted()) {
                        state.live(kind, dstId)++;
                        state.transit(kind, srcId, dstId)--;
                    } else {
                        val adopterId = getCurrentAdopterId(id);
                        val adopterState = states(adopterId);
                        adopterState.liveAdopted(kind, dstId)++;
                        adopterState.transitAdopted(kind, srcId, dstId)--;
                    }
                    if (verbose>=3) state.dump("DUMP id="+id);
                }
            } finally {
                lock.unlock();
            }
            if (shouldSubmit) {
                at (pendingActivity) @Immediate("notifyActivityCreation_push_activity") async {
                    val pa = pendingActivity();
                    if (pa != null && pa.epoch == Runtime.epoch()) {
                        if (verbose>=1) debug("<<<< notifyActivityCreation(id="+id+") finally submitting activity");
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
        if (verbose>=1) debug(">>>> notifyShiftedActivityCreation(id="+id+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);

        return Runtime.evalImmediateAt[Boolean](place0, ()=> {
            try {
                lock.lock();
                if (Place(srcId).isDead() || Place(dstId).isDead()) {
                    // NOTE: no state updates or DPE processing here.
		    //       Must happen exactly once and is done
                    //       when Place0 is notified of a dead place.
                    if (verbose>=1) debug("==== notifyShiftedActivityCreation(id="+id+") suppressed: "+srcId + " ==> "+dstId+" kind="+kind);
                    return false;
                }
                val state = states(id);
                if (!state.isAdopted()) {
                    state.live(kind, dstId)++;
                    state.transit(kind, srcId, dstId)--;
                } else {
                    val adopterId = getCurrentAdopterId(id);
                    val adopterState = states(adopterId);
                    adopterState.liveAdopted(kind, dstId)++;
                    adopterState.transitAdopted(kind, srcId, dstId)--;
                }
                if (verbose>=3) state.dump("DUMP id="+id);
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
        if (verbose>=1) debug(">>>> notifyActivityCreationFailed(id="+id+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);

        at (place0) @Immediate("notifyActivityCreationFailed_to_zero") async {
            try {
                lock.lock();
                if (Place(srcId).isDead() || Place(dstId).isDead()) {
                    // NOTE: no state updates or DPE processing here.
		    //       Must happen exactly once and is done
                    //       when Place0 is notified of a dead place.
                    if (verbose>=1) debug("==== notifyActivityCreationFailed(id="+id+") suppressed: "+srcId + " ==> "+dstId+" kind="+kind);
                } else {
                    if (verbose>=1) debug(">>>> notifyActivityCreatedFailed(id="+id+") message running at place0");
                    val state = states(id);
                    if (!state.isAdopted()) {
                        state.transit(kind, srcId, dstId)--;
                        state.numActive--;
                        state.excs.add(t);
                        if (quiescent(id)) releaseLatch(id);
                    } else {
                        val adopterId = getCurrentAdopterId(id);
                        val adopterState = states(adopterId);
                        adopterState.transitAdopted(kind, srcId, dstId)--;
                        adopterState.numActive--;
                        adopterState.excs.add(t);
                        if (quiescent(adopterId)) releaseLatch(adopterId);
                    }
                }
            } finally {
                lock.unlock();
            }
       };

       if (verbose>=1) debug("<<<< notifyActivityCreationFailed(id="+id+") returning, srcId="+srcId + " dstId="+dstId);
    }

    def notifyActivityCreatedAndTerminated(srcPlace:Place) {
        notifyActivityCreatedAndTerminated(srcPlace, ASYNC);
    }
    def notifyActivityCreatedAndTerminated(srcPlace:Place, kind:long) {
        val srcId = srcPlace.id; 
        val dstId = here.id;
        if (verbose>=1) debug(">>>> notifyActivityCreatedAndTerminated(id="+id+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);

        at (place0) @Immediate("notifyActivityCreatedAndTerminated_to_zero") async {
            try {
                lock.lock();
                if (Place(srcId).isDead() || Place(dstId).isDead()) {
                    // NOTE: no state updates or DPE processing here.
		    //       Must happen exactly once and is done
                    //       when Place0 is notified of a dead place.
                    if (verbose>=1) debug("==== notifyActivityCreatedAndTerminated(id="+id+") suppressed: "+srcId + " ==> "+dstId+" kind="+kind);
                } else {
                    if (verbose>=1) debug(">>>> notifyActivityCreatedAndTerminated(id="+id+") message running at place0");
                    val state = states(id);
                    if (!state.isAdopted()) {
                        state.transit(kind, srcId, dstId)--;
                        state.numActive--;
                        if (quiescent(id)) releaseLatch(id);
                    } else {
                        val adopterId = getCurrentAdopterId(id);
                        val adopterState = states(adopterId);
                        adopterState.transitAdopted(kind, srcId, dstId)--;
                        adopterState.numActive--;
                        if (quiescent(adopterId)) releaseLatch(adopterId);
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
        val dstId = here.id;
        if (verbose>=1) debug(">>>> notifyActivityTermination(id="+id+") called, dstId="+dstId+" kind="+kind);
        at (place0) @Immediate("notifyActivityTermination_to_zero") async {
            try {
                lock.lock();
                if (Place(dstId).isDead()) {
                    // NOTE: no state updates or DPE processing here.
		    //       Must happen exactly once and is done
                    //       when Place0 is notified of a dead place.
                    if (verbose>=1) debug("==== notifyActivityTermination(id="+id+") suppressed: "+dstId+" kind="+kind);
                } else {
                    if (verbose>=1) debug("<<<< notifyActivityTermination(id="+id+") message running at place0");
                    val state = states(id);
                    if (!state.isAdopted()) {
                        state.live(kind, dstId)--;
                        state.numActive--;
                        if (quiescent(id)) releaseLatch(id);
                    } else {
                        val adopterId = getCurrentAdopterId(id);
                        val adopterState = states(adopterId);
                        adopterState.liveAdopted(kind, dstId)--;
                        adopterState.numActive--;
                        if (quiescent(adopterId)) releaseLatch(adopterId);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    def pushException(t:CheckedThrowable):void {
        if (verbose>=1) debug(">>>> pushException(id="+id+") called, t="+t);
        Runtime.runImmediateAt(place0, ()=>{ 
            try {
                lock.lock();
                val state = states(id);
                state.excs.add(t); // NB: if adopted, semantics say to suppress exception.  So don't both checking for adopterId.
            } finally {
                lock.unlock();
            }
        });
        if (verbose>=1) debug("<<<< pushException(id="+id+") returning");
    }

    def waitForFinish():void {
        if (verbose>=1) debug(">>>> waitForFinish(id="+id+") called");

        // terminate myself
        notifyActivityTermination();

        // If we haven't gone remote with this finish yet, see if this worker
        // can execute other asyncs that are governed by the finish before waiting on the latch.
        if ((!Runtime.STRICT_FINISH) && (Runtime.STATIC_THREADS || !hasRemote)) {
            if (verbose>=2) debug("calling worker.join for id="+id);
            Runtime.worker().join(this.latch);
        }

        // wait for the latch release
        if (verbose>=2) debug("calling latch.await for id="+id);
        latch.await(); // wait for the termination (latch may already be released)
        if (verbose>=2) debug("returned from latch.await for id="+id);
        
        // get exceptions and propagate if there are any
        if (excs != null) throw excs;
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

        hasRemote = true;
        val srcId = here.id;
        val dstId = place.id;
        if (bytes.size >= ASYNC_SIZE_THRESHOLD) {
            if (verbose >= 1) debug("==== spawnRemoteActivity(id="+id+") selecting indirect (size="+
                                    bytes.size+") srcId="+srcId + " dstId="+dstId);
            val preSendAction = ()=>{ this.notifySubActivitySpawn(place); };
            val wrappedBody = ()=> @x10.compiler.AsyncClosure {
                val deser = new x10.io.Deserializer(bytes);
                val bodyPrime = deser.readAny() as ()=>void;
                bodyPrime();
            };
            x10.xrx.Runtime.x10rtSendAsync(place.id, wrappedBody, this, prof, preSendAction);
        } else {
            if (verbose >= 1) debug(">>>>  spawnRemoteActivity(id="+id+") selecting direct (size="+
                                    bytes.size+") srcId="+srcId + " dstId="+dstId);
            Runtime.runImmediateAt(place0, ()=>{
                try {
                    lock.lock();
                    val state = states(id);
                    if (Place(srcId).isDead()) {
                        if (verbose>=1) debug("==== spwanRemoteActivity(id="+id+") src "+srcId + "is dead; dropping async");
                        return;
                    }
                    if (Place(dstId).isDead()) {
                        if (verbose>=1) debug("==== spawnRemoteActivitySpawn(id="+id+") destination "+dstId + "is dead; pushed DPE");
                        addDeadPlaceException(state, dstId);
                        return;
                    }
                    if (!state.isAdopted()) {
                        state.live(ASYNC, dstId)++;
                        state.numActive++;
                    } else {
                        val adopterId = getCurrentAdopterId(id);
                        val adopterState = states(adopterId);
                        adopterState.liveAdopted(ASYNC, dstId)++;
                        adopterState.numActive++;
                    }                                        
                    if (verbose>=3) state.dump("DUMP id="+id);
                } finally {
                    lock.unlock();
                }
                at (Place(dstId)) @Immediate("spawnRemoteActivity_dstPlace") async {
                    if (verbose >= 1) debug("==== spawnRemoteActivity(id="+id+") submitting activity from "+srcId+" at "+dstId);
                    val wrappedBody = ()=> {
                        // defer deserialization to reduce work on immediate thread
                        val deser = new x10.io.Deserializer(bytes);
                        val bodyPrime = deser.readAny() as ()=>void;
                        bodyPrime();
                    };
                    Runtime.worker().push(new Activity(42, wrappedBody, this));
               }
            });
            if (verbose>=1) debug("<<<< spawnRemoteActivity(id="+id+") returning");
        }
    }
    
    /*
     * Private methods
     */
    private static def getCurrentAdopterId(id:Long):Long {
        assert here==place0;
        var currentId:Long= id;
        while (true) {
            assert currentId!=-1;
            val state = states(currentId);
            if (!state.isAdopted()) break;
            currentId = state.adopterId;
        }
        return currentId;
    }

    private static def releaseLatch(id:Long) { // release the latch for this state
        if (verbose>=2) debug("releaseLatch(id="+id+") called");
        val state = states(id);
        val gfs = state.gfs;
        val excs = state.isAdopted() ? null : MultipleExceptions.make(state.excs);
        at (gfs.home) @Immediate("releaseLatch_gfs_home") async {
            if (verbose>=2) debug("releasing latch id="+id+(excs == null ? " no exceptions" : " with exceptions"));
            gfs().excs = excs;
            gfs().latch.release();
        }
        if (verbose>=2) debug("releaseLatch(id="+id+") returning");
    }

    private static def quiescent(id:Long):Boolean {
        if (verbose>=2) debug("quiescent(id="+id+") called");
        val state = states(id);
        if (state==null) {
            if (verbose>=2) debug("quiescent(id="+id+") returning false, state==null");
            return false;
        }
        if (state.isAdopted()) {
            if (verbose>=2) debug("quiescent(id="+id+") returning false, already adopted by adopterId=="+state.adopterId);
            return false;
        }
        
	if (state.numActive < 0) {
            debug("COUNTING ERROR: quiescent(id="+id+") negative numActive!!!");
            state.dump("DUMP id="+id);
            return true; // TODO: This really should be converted to a fatal error....
        }
        
        val quiet = state.numActive == 0;
        if (verbose>=3) state.dump("DUMP id="+id);
        if (verbose>=2) debug("quiescent(id="+id+") returning " + quiet);
        return quiet;
    }

    private static def processPlaceDeath(id:Long) {
        val state = states(id);

        if (state==null || state.isAdopted()) return; // nothing to do.

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
            childState.adopterId = id;
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
