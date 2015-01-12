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
package x10.lang;
import x10.compiler.*;
import x10.util.concurrent.SimpleLatch;
import x10.array.Array_2;
import x10.array.Array_3;

/**
 * Place0-based Resilient Finish
 * This version is optimized and does not use ResilientStorePlace0
 */
class FinishResilientPlace0 extends FinishResilient {
    private static val verbose = FinishResilient.verbose;
    private static val place0 = Place.FIRST_PLACE;

    private static val AT = 0;
    private static val ASYNC = 1;
    private static val AT_AND_ASYNC = AT..ASYNC;
    
    private static class State { // data stored at Place0
        val NUM_PLACES = Place.numPlaces();
        val transit = new Array_3[Int](NUM_PLACES, NUM_PLACES, 2);
        val transitAdopted = new Array_3[Int](NUM_PLACES, NUM_PLACES, 2);
        val live = new Array_2[Int](NUM_PLACES, 2);
        val liveAdopted = new Array_2[Int](NUM_PLACES, 2);
        val excs = new x10.util.GrowableRail[CheckedThrowable](); // exceptions to report
        val children = new x10.util.GrowableRail[Long](); // children
        var adopterId:Long = -1; // adopter (if adopted)
        def isAdopted() = (adopterId != -1);
        var numDead:Long = 0;
        
        val parentId:Long; // parent (or -1)
        val gLatch:GlobalRef[SimpleLatch]; // latch to be released
        private def this(parentId:Long, gLatch:GlobalRef[SimpleLatch]) {
            this.parentId = parentId; this.gLatch = gLatch;
        }
        
        def dump(msg:Any) {
            val s = new x10.util.StringBuilder(); s.add(msg); s.add('\n');
            s.add("           live:"); for (v in live          ) s.add(" " + v); s.add('\n');
            s.add("    liveAdopted:"); for (v in liveAdopted   ) s.add(" " + v); s.add('\n');
            s.add("        transit:"); for (v in transit       ) s.add(" " + v); s.add('\n');
            s.add(" transitAdopted:"); for (v in transitAdopted) s.add(" " + v); s.add('\n');
            s.add("  children.size: " + children.size()); s.add('\n');
            s.add("      adopterId: " + adopterId); s.add('\n');
            s.add("       parentId: " + parentId);
            debug(s.toString());
        }
    }
    
    // TODO: freelist to reuse ids (maybe also states)
    private static val states = (here.id==0) ? new x10.util.GrowableRail[State]() : null;
    
    private val id:Long;
    private var hasRemote:Boolean = false;

    public def toString():String = "FinishResilientPlace0(id="+id+")";
    private def this(id:Long) { this.id = id; }
    
    static def make(parent:FinishState, latch:SimpleLatch):FinishResilient {
        if (verbose>=1) debug(">>>> make called, parent="+parent + " latch="+latch);
        val parentId = (parent instanceof FinishResilientPlace0) ? (parent as FinishResilientPlace0).id : -1; // ok to ignore other cases?
        val gLatch = GlobalRef[SimpleLatch](latch);
        val idCell= new Cell[Long](-1);
        lowLevelFetch[Long](place0, idCell, ()=>{ atomic {
            val id = states.size();
            val state = new State(parentId, gLatch);
            states.add(state);
            state.live(gLatch.home.id,ASYNC) = 1n; // for myself, will be decremented in waitForFinish
            if (parentId != -1) states(parentId).children.add(id);
            return id;
        }});
        val id = idCell();
        val fs = new FinishResilientPlace0(id);
        if (verbose>=1) debug("<<<< make returning fs="+fs);
        return fs;
    }

    static def notifyPlaceDeath():void {
        if (verbose>=1) debug(">>>> notifyPlaceDeath called");
        if (here != place0) {
            if (verbose>=2) debug("not place0, returning");
            return;
        }
        atomic {
            for (id in 0..(states.size()-1))
                if (quiescent(id)) releaseLatch(id);
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
        val srcId = here.id, dstId = place.id;
        if (dstId != srcId) hasRemote = true;
        if (verbose>=1) debug(">>>> notifySubActivitySpawn(id="+id+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);
        lowLevelAt(place0, ()=>{ atomic {
            val state = states(id);
            if (!state.isAdopted()) {
                state.transit(srcId, dstId, kind)++;
            } else {
                val adopterId = getCurrentAdopterId(id);
                val adopterState = states(adopterId);
                adopterState.transitAdopted(srcId, dstId, kind)++;
            }
            if (verbose>=3) state.dump("DUMP id="+id);
        }});
        if (verbose>=1) debug("<<<< notifySubActivitySpawn(id="+id+") returning");
    }

    /*
     * This method can't block because it may run on an @Immediate worker.  
     * Therefore it can't use lowLevelAt.
     * Instead sequence @Immediate messages to do the nac to place0 and
     * then come back and submit the pending activity.
     * Because place0 can't fail, we know that if the first message gets
     * to place0, the message back to push the activity will evantually
     * be received (unless dstId's place fails, in which case it doesn't matter).
     */
    def notifyActivityCreation(srcPlace:Place, activity:Activity):Boolean {
        return notifyActivityCreation(srcPlace, activity, ASYNC);
    }
    def notifyActivityCreation(srcPlace:Place, activity:Activity, kind:long):Boolean {
        val srcId = srcPlace.id; 
        val dstId = here.id;
        if (verbose>=1) debug(">>>> notifyActivityCreation(id="+id+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);
        if (srcPlace.isDead()) {
            if (verbose>=1) debug("<<<< notifyActivityCreation(id="+id+") returning false");
            return false;
        }

        val pendingActivity = GlobalRef(activity); 
        at (place0) @Immediate("notifyActivityCreation_to_zero") async {
            atomic {
                val state = states(id);
                if (!state.isAdopted()) {
                    state.live(dstId, kind)++;
                    state.transit(srcId, dstId, kind)--;
                } else {
                    val adopterId = getCurrentAdopterId(id);
                    val adopterState = states(adopterId);
                    adopterState.liveAdopted(dstId, kind)++;
                    adopterState.transitAdopted(srcId, dstId, kind)--;
                }
                if (verbose>=3) state.dump("DUMP id="+id);
            };
            at (pendingActivity) @Immediate("notifyActivityCreation_push_activity") async {
                val pa = pendingActivity();
                if (pa != null && pa.epoch == Runtime.epoch()) {
                    if (verbose>=1) debug("<<<< notifyActivityCreation(id="+id+") finally submitting activity");
                    Runtime.worker().push(pa);
                }
                pendingActivity.forget();
            }
        };

        // Return false because we want to defer pushing the activity.
        return false;                
    }

    def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity):Boolean {
        return notifyActivityCreationBlocking(srcPlace, activity, ASYNC);
    }
    def notifyShiftedActivityCreation(srcPlace:Place, activity:Activity):Boolean {
        return notifyActivityCreationBlocking(srcPlace, activity, AT);
    }
    def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity, kind:long):Boolean {
        val srcId = srcPlace.id; 
        val dstId = here.id;
        if (verbose>=1) debug(">>>> notifyActivityCreation(id="+id+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);
        if (srcPlace.isDead()) {
            if (verbose>=1) debug("<<<< notifyActivityCreation(id="+id+") returning false");
            return false;
        }

        lowLevelAt(place0, ()=> {
            atomic {
                val state = states(id);
                if (!state.isAdopted()) {
                    state.live(dstId, kind)++;
                    state.transit(srcId, dstId, kind)--;
                } else {
                    val adopterId = getCurrentAdopterId(id);
                    val adopterState = states(adopterId);
                    adopterState.liveAdopted(dstId, kind)++;
                    adopterState.transitAdopted(srcId, dstId, kind)--;
                }
                if (verbose>=3) state.dump("DUMP id="+id);
            }
        });

        return true;
    }

    def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void { 
        notifyActivityCreationFailed(srcPlace, t, ASYNC);
    }
    def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable, kind:long):void { 
        val srcId = srcPlace.id; 
        val dstId = here.id;
        if (verbose>=1) debug(">>>> notifyActivityCreationFailed(id="+id+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);

        at (place0) @Immediate("notifyActivityCreationFailed_to_zero") async {
            atomic {
                if (verbose>=1) debug(">>>> notifyActivityCreationFailed(id="+id+") message running at place0");
                val state = states(id);
                if (!state.isAdopted()) {
                    state.transit(srcId, dstId, kind)--;
                    state.excs.add(t);
                    if (quiescent(id)) releaseLatch(id);
                } else {
                    val adopterId = getCurrentAdopterId(id);
                    val adopterState = states(adopterId);
                    adopterState.transitAdopted(srcId, dstId, kind)--;
                    adopterState.excs.add(t);
                    if (quiescent(adopterId)) releaseLatch(adopterId);
                }
            };
       };

       if (verbose>=1) debug(">>>> notifyActivityCreationFailed(id="+id+") returning, srcId="+srcId + " dstId="+dstId);
    }

    def notifyActivityCreatedAndTerminated(srcPlace:Place) {
        notifyActivityCreatedAndTerminated(srcPlace, ASYNC);
    }
    def notifyActivityCreatedAndTerminated(srcPlace:Place, kind:long) {
        val srcId = srcPlace.id; 
        val dstId = here.id;
        if (verbose>=1) debug(">>>> notifyActivityCreatedAndTerminated(id="+id+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);

        at (place0) @Immediate("notifyActivityCreatedAndTerminated_to_zero") async {
            atomic {
                if (verbose>=1) debug(">>>> notifyActivityCreatedAndTerminated(id="+id+") message running at place0");
                val state = states(id);
                if (!state.isAdopted()) {
                    state.transit(srcId, dstId, kind)--;
                    if (quiescent(id)) releaseLatch(id);
                } else {
                    val adopterId = getCurrentAdopterId(id);
                    val adopterState = states(adopterId);
                    adopterState.transitAdopted(srcId, dstId, kind)--;
                    if (quiescent(adopterId)) releaseLatch(adopterId);
                }
            };
       };

       if (verbose>=1) debug(">>>> notifyActivityCreatedAndTerminated(id="+id+") returning, srcId="+srcId + " dstId="+dstId);
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
            atomic {
                if (verbose>=1) debug("<<<< notifyActivityTermination(id="+id+") message running at place0");
                val state = states(id);
                if (!state.isAdopted()) {
                    state.live(dstId, kind)--;
                    if (quiescent(id)) releaseLatch(id);
                } else {
                    val adopterId = getCurrentAdopterId(id);
                    val adopterState = states(adopterId);
                    adopterState.liveAdopted(dstId, kind)--;
                    if (quiescent(adopterId)) releaseLatch(adopterId);
                }
            }
        };
    }

    def pushException(t:CheckedThrowable):void {
        if (verbose>=1) debug(">>>> pushException(id="+id+") called, t="+t);
        lowLevelAt(place0, ()=>{ atomic {
            val state = states(id);
            state.excs.add(t); // need not consider the adopter
        }});
        if (verbose>=1) debug("<<<< pushException(id="+id+") returning");
    }

    def waitForFinish():void {
        if (verbose>=1) debug(">>>> waitForFinish(id="+id+") called");
        // terminate myself
        notifyActivityTermination(ASYNC); // TOOD: merge this to the following lowLevelFetch

        // get the latch to wait
        val gLatchCell = new Cell[GlobalRef[SimpleLatch]](GlobalRef(null as SimpleLatch));
        lowLevelFetch(place0, gLatchCell, ()=>{ atomic {
            val state = states(id);
            return state.gLatch;
        }});
        val gLatch = gLatchCell();
        assert gLatch.home==here;
        val lLatch = gLatch.getLocalOrCopy();

        // If we haven't gone remote with this finish yet, see if this worker
        // can execute other asyncs that are governed by the finish before waiting on the latch.
        if ((!Runtime.STRICT_FINISH) && (Runtime.STATIC_THREADS || !hasRemote)) {
            if (verbose>=2) debug("calling worker.join for id="+id);
            Runtime.worker().join(lLatch);
        }

        // wait for the latch release
        if (verbose>=2) debug("calling latch.await for id="+id);
        lLatch.await(); // wait for the termination (latch may already be released)
        if (verbose>=2) debug("returned from latch.await for id="+id);
        
        // get exceptions
        val excCell = new Cell[MultipleExceptions](null);
        lowLevelFetch(place0, excCell, ()=> { atomic {
            val state = states(id);
            if (!state.isAdopted()) {
                states(id) = null;
                return MultipleExceptions.make(state.excs); // may return null
            } else {
                //TODO: need to remove the state in future
                return null as MultipleExceptions;
            }
        }});
        val e = excCell();
        if (verbose>=1) debug("<<<< waitForFinish(id="+id+") returning, exc="+e);
        if (e != null) throw e;
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
        assert here==place0; // should be called inside atomic
        if (verbose>=2) debug("releaseLatch(id="+id+") called");
        val state = states(id);
        val gLatch = state.gLatch;
        lowLevelSend(gLatch.home, ()=>{
            if (verbose>=2) debug("calling latch.release for id="+id);
            gLatch.getLocalOrCopy().release(); // latch.wait is in waitForFinish
        });
        if (verbose>=2) debug("releaseLatch(id="+id+") returning");
    }

    private static def quiescent(id:Long):Boolean {
        assert here==place0; // should be called inside atomic
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
        
        // 1 pull up dead children
        val nd = Place.numDead();
        if (nd != state.numDead) {
            state.numDead = nd;
            val children = state.children;
            for (var chIndex:Long = 0; chIndex < children.size(); ++chIndex) {
                val childId = children(chIndex);
                val childState = states(childId);
                if (childState==null) continue;
                if (!childState.gLatch.home.isDead()) continue;
                val lastChildId = children.removeLast();
                if (chIndex < children.size()) children(chIndex) = lastChildId;
                chIndex--; // don't advance this iteration
                // adopt the child
                if (verbose>=3) debug("adopting childId="+childId);
                assert !childState.isAdopted();
                childState.adopterId = id;
                state.children.addAll(childState.children); // will be checked in the following iteration
		for (k in AT_AND_ASYNC) {
                    for (i in 0..(state.NUM_PLACES-1)) {
                        state.liveAdopted(i,k) += (childState.live(i,k) + childState.liveAdopted(i,k));
                        for (j in 0..(state.NUM_PLACES-1)) {
                            state.transitAdopted(i, j, k) += (childState.transit(i, j, k) + childState.transitAdopted(i, j, k));
                        }
                    }
                }
            } // for (chIndex)
        }

        // 2 delete dead entries
        for (i in 0..(state.NUM_PLACES-1)) {
            if (Place.isDead(i)) {
                for (1..state.live(i, ASYNC)) {
                    if (verbose>=3) debug("adding DPE for live asyncs("+i+")");
                    addDeadPlaceException(state, i);
                }
                state.live(i, AT) = 0n; state.liveAdopted(i, AT) = 0n;
                state.live(i, ASYNC) = 0n; state.liveAdopted(i, ASYNC) = 0n;
                for (j in 0..(state.NUM_PLACES-1)) {
                    state.transit(i,j, AT) = 0n; state.transitAdopted(i, j, AT) = 0n;
                    state.transit(i,j, ASYNC) = 0n; state.transitAdopted(i, j, ASYNC) = 0n;
                    for (1..state.transit(j,i,ASYNC)) {
                        if (verbose>=3) debug("adding DPE for transit asyncs("+j+","+i+")");
                        addDeadPlaceException(state, i);
                    }
                    state.transit(j,i,AT) = 0n; state.transitAdopted(j,i,AT) = 0n;
                    state.transit(j,i,ASYNC) = 0n; state.transitAdopted(j,i,ASYNC) = 0n;
                }
            }
        }
        
        // 3 quiescent check
        if (verbose>=3) state.dump("DUMP id="+id);
        var quiet:Boolean = true;
        outer: for (i in 0..(state.NUM_PLACES-1)) {
            for (k in AT_AND_ASYNC) {
                if (state.live(i,k) > 0) { quiet = false; break outer; }
                if (state.liveAdopted(i,k) > 0) { quiet = false; break outer; }
                for (j in 0..(state.NUM_PLACES-1)) {
                    if (state.transit(i,j,k) > 0) { quiet = false; break outer; }
                    if (state.transitAdopted(i,j,k) > 0) { quiet = false; break outer; }
                }
            }
        }
        if (verbose>=2) debug("quiescent(id="+id+") returning " + quiet);
        return quiet;
    }
    private static def addDeadPlaceException(state:State, placeId:Long) {
        val e = new DeadPlaceException(Place(placeId));
        e.fillInStackTrace(); // meaningless?
        state.excs.add(e);
    }

}
