/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */
package x10.lang;
import x10.compiler.*;
import x10.util.concurrent.SimpleLatch;

// /*
//  * Skeleton
//  * FinishResilient.make and notifyPlaceDeath should also be modified
//  */
// class FinishResilientSample extends FinishResilient {
//     private static val verbose = FinishResilient.verbose;
//     static def make(parent:FinishState, latch:SimpleLatch):FinishResilient {
//         if (verbose>=1) debug("make called, parent=" + parent + " latch=" + latch);
//         return null;
//     }
//     static def notifyPlaceDeath():void {
//         if (verbose>=1) debug("notifyPlaceDeath called");
//     }
//     def notifySubActivitySpawn(place:Place):void {
//         if (verbose>=1) debug("notifySubActivitySpawn called, place.id=" + place.id);
//     }
//     def notifyActivityCreation(srcPlace:Place):Boolean {
//         if (verbose>=1) debug("notifyActivityCreation called, srcPlace.id=" + srcPlace.id);
//         if (srcPlace.isDead()) return false; return true;
//     }
//     def notifyActivityTermination():void {
//         if (verbose>=1) debug("notifyActivityTermination called");
//     }
//     def pushException(t:Exception):void {
//         if (verbose>=1) debug("pushException called, t=" + t);
//     }
//     def waitForFinish():void {
//         if (verbose>=1) debug("waitForFinish called");
//     }
// }

/*
 * Sample (but not so fast) implemenation of Resilient Finish
 */
class FinishResilientSample extends FinishResilient implements Runtime.Mortal {
    private static val verbose = FinishResilient.verbose;
    private static type FinishID = GlobalRef[FinishResilientSample];
    private static val RS = ResilientStore.make[FinishID,State]("FinishResilientSample");
    
    private static class State { // data stored into ResilientStore
        val transit = new Rail[Int](Place.MAX_PLACES * Place.MAX_PLACES, 0n);
        val transitAdopted = new Rail[Int](Place.MAX_PLACES * Place.MAX_PLACES, 0n);
        val live = new Rail[Int](Place.MAX_PLACES, 0n);
        val liveAdopted = new Rail[Int](Place.MAX_PLACES, 0n);
        val excs = new x10.util.GrowableRail[Exception](); // exceptions to report
        val children = new x10.util.GrowableRail[FinishID](); // children
        var adopterId:FinishID = new FinishID(null); // adopter (if adopted)
        def isAdopted() = (!adopterId.isNull());
        var numDead:Long = 0;
        def dump(msg:Any) {
            val s = new x10.util.StringBuilder(); s.add(msg); s.add('\n');
            s.add("           live:"); for (v in live          ) s.add(" " + v); s.add('\n');
            s.add("    liveAdopted:"); for (v in liveAdopted   ) s.add(" " + v); s.add('\n');
            s.add("        transit:"); for (v in transit       ) s.add(" " + v); s.add('\n');
            s.add(" transitAdopted:"); for (v in transitAdopted) s.add(" " + v); s.add('\n');
            s.add("  children.size: " + children.size()); s.add('\n');
            s.add("      adopterId: " + adopterId);
            debug(s.toString());
        }
    }
    
    private static val ALL = new x10.util.HashSet[FinishResilientSample](); // all active finishes in this place
    
    @NonEscaping private val id:FinishID; // should be global
    private transient val latch:SimpleLatch;
    public def toString():String = System.identityToString(this) + "(id="+id+")";
    
    private def this(parent:FinishState, latch:SimpleLatch) {
        this.latch = latch;
        this.id = new FinishID(this);
    }    
    static def make(parent:FinishState, latch:SimpleLatch):FinishResilientSample { // parent is null for rootFinish
        if (verbose>=1) debug(">>>> make called, parent="+parent + " latch="+latch);
        val fs = new FinishResilientSample(parent, latch);
        val id = fs.id;
        val state = new State();
        state.live(here.id) = 1n; // for myself, will be decremented in waitForFinish
       RS.lock();
        RS.create(id, state);
        if (parent instanceof FinishResilientSample) { // ok to ignore other cases?
            val parentId = (parent as FinishResilientSample).id;
            val parentState = RS.getOrElse(parentId, null);
            parentState.children.add(id);
            RS.put(parentId, parentState);
        }
        atomic { ALL.add(fs); } // will be used in notifyPlaceDeath, and removed in waitForFinish
       RS.unlock();
        if (verbose>=1) debug("<<<< make returning fs="+fs);
        return fs;
    }
    static def notifyPlaceDeath():void {
        if (verbose>=1) debug(">>>> notifyPlaceDeath called");
        for (fs in ALL) {
           RS.lock();
            if (fs.quiescent()) fs.releaseLatch();
           RS.unlock();
        }
        if (verbose>=1) debug("<<<< notifyPlaceDeath returning");
    }
    private def releaseLatch() { // can be called from any place
        if (verbose>=2) debug("releaseLatch(id="+id+") called");
        lowLevelSend(id.home, ()=>{
            val fs = id.getLocalOrCopy();
            if (verbose>=2) debug("calling latch.release for id="+id);
            fs.latch.release(); // latch.wait is in waitForFinish
        });
        if (verbose>=2) debug("releaseLatch(id="+id+") returning");
    }
    
    private def getCurrentAdopterId():FinishID {
        // assert RS.isLocked();
        var currentId:FinishID = id;
        while (true) {
            val state = RS.getOrElse(currentId, null);
            if (!state.isAdopted()) break;
            currentId = state.adopterId;
        }
        return currentId;
    }
    def notifySubActivitySpawn(place:Place):void {
        val srcId = here.id, dstId = place.id;
        if (verbose>=1) debug(">>>> notifySubActivitySpawn(id="+id+") called, srcId="+srcId + " dstId="+dstId);
       RS.lock();
        val state = RS.getOrElse(id, null);
        if (!state.isAdopted()) {
            state.transit(srcId*Place.MAX_PLACES + dstId)++;
            RS.put(id, state);
        } else {
            val adopterId = getCurrentAdopterId();
            val adopterState = RS.getOrElse(adopterId, null);
            adopterState.transitAdopted(srcId*Place.MAX_PLACES + dstId)++;
            RS.put(adopterId, adopterState);
        }
        if (verbose>=3) state.dump("DUMP id="+id);
       RS.unlock();
        if (verbose>=1) debug("<<<< notifySubActivitySpawn(id="+id+") returning");
    }
    def notifyActivityCreation(srcPlace:Place):Boolean {
        val srcId = srcPlace.id, dstId = here.id;
        if (verbose>=1) debug(">>>> notifyActivityCreation(id="+id+") called, srcId="+srcId + " dstId="+dstId);
        if (srcPlace.isDead()) {
            if (verbose>=1) debug("<<<< notifyActivityCreation(id="+id+") returning false");
            return false;
        }
        RS.lock();
        val state = RS.getOrElse(id, null);
        if (!state.isAdopted()) {
            state.live(dstId)++;
            state.transit(srcId*Place.MAX_PLACES + dstId)--;
            RS.put(id, state);
        } else {
            val adopterId = getCurrentAdopterId();
            val adopterState = RS.getOrElse(adopterId, null);
            adopterState.liveAdopted(dstId)++;
            adopterState.transitAdopted(srcId*Place.MAX_PLACES + dstId)--;
            RS.put(adopterId, adopterState);
        }
        if (verbose>=3) state.dump("DUMP id="+id);
       RS.unlock();
        if (verbose>=1) debug("<<<< notifyActivityCreation(id="+id+") returning true");
        return true;
    }
    def notifyActivityTermination():void {
        val dstId = here.id;
        if (verbose>=1) debug(">>>> notifyActivityTermination(id="+id+") called, dstId="+dstId);
       RS.lock();
        val state = RS.getOrElse(id, null);
        if (!state.isAdopted()) {
            state.live(dstId)--;
            RS.put(id, state);
        } else {
            val adopterId = getCurrentAdopterId();
            val adopterState = RS.getOrElse(adopterId, null);
            adopterState.liveAdopted(dstId)--;
            RS.put(adopterId, adopterState);
        }
        if (quiescent()) releaseLatch();
       RS.unlock();
        if (verbose>=1) debug("<<<< notifyActivityTermination(id="+id+") returning");
    }
    def pushException(t:Exception):void {
        if (verbose>=1) debug(">>>> pushException(id="+id+") called, t="+t);
       RS.lock();
        val state = RS.getOrElse(id, null);
        state.excs.add(t); // need not consider the adopter
        RS.put(id, state);
       RS.unlock();
        if (verbose>=1) debug("<<<< pushException(id="+id+") returning");
    }
    
    def waitForFinish():void {
        assert id.home==here;
        if (verbose>=1) debug(">>>> waitForFinish(id="+id+") called");
        notifyActivityTermination(); // terminate myself
        if (verbose>=2) debug("calling latch.await for id="+id);
        latch.await(); // wait for the termination (latch may already be released)
        if (verbose>=2) debug("returned from latch.await for id="+id);
        var e:MultipleExceptions = null;
       RS.lock();
        val state = RS.getOrElse(id, null);
        if (!state.isAdopted()) {
            e = MultipleExceptions.make(state.excs); // may return null
            RS.remove(id);
        } else {
            //TODO: need to remove the state in future
        }
        atomic { ALL.remove(this); }
       RS.unlock();
        if (verbose>=1) debug("<<<< waitForFinish(id="+id+") returning, exc="+e);
        if (e != null) throw e;
    }
    
    private def quiescent():Boolean {
        if (verbose>=2) debug("quiescent(id="+id+") called");
        // assert RS.isLocked();
        val state = RS.getOrElse(id, null);
        
        // 1 pull up dead children
        val nd = Place.numDead();
        if (nd != state.numDead) {
            state.numDead = nd;
            val children = state.children;
            for (var chIndex:Long = 0; chIndex < children.size(); ++chIndex) {
                val childId = children(chIndex);
                if (!childId.home.isDead()) continue;
                val lastChildId = children.removeLast();
                if (chIndex < children.size()) children(chIndex) = lastChildId;
                chIndex--; // don't advance this iteration
                // adopt the child
                if (verbose>=3) debug("adopting childId="+childId);
                val childState = RS.getOrElse(childId, null);
                assert !childState.isAdopted();
                childState.adopterId = id;
                RS.put(childId, childState);
                state.children.addAll(childState.children); // will be checked in the following iteration
                for (i in 0..(Place.MAX_PLACES-1)) {
                    state.liveAdopted(i) += (childState.live(i) + childState.liveAdopted(i));
                    for (j in 0..(Place.MAX_PLACES-1)) {
                        val idx = i*Place.MAX_PLACES + j;
                        state.transitAdopted(idx) += (childState.transit(idx) + childState.transitAdopted(idx));
                    }
                }
            } // for (chIndex)
        }
        // 2 delete dead entries
        for (i in 0..(Place.MAX_PLACES-1)) {
            if (Place.isDead(i)) {
                for (unused in 1..state.live(i)) {
                    if (verbose>=3) debug("adding DPE for live("+i+")");
                    addDeadPlaceException(state, i);
                }
                state.live(i) = 0n; state.liveAdopted(i) = 0n;
                for (j in 0..(Place.MAX_PLACES-1)) {
                    val idx = i*Place.MAX_PLACES + j;
                    state.transit(idx) = 0n; state.transitAdopted(idx) = 0n;
                    val idx2 = j*Place.MAX_PLACES + i;
                    for (unused in 1..state.transit(idx2)) {
                        if (verbose>=3) debug("adding DPE for transit("+j+","+i+")");
                        addDeadPlaceException(state, i);
                    }
                    state.transit(idx2) = 0n; state.transitAdopted(idx2) = 0n;
                }
            }
        }
        
        RS.put(id, state);
        
        // 3 quiescent check
        if (verbose>=3) state.dump("DUMP id="+id);
        var quiet:Boolean = true;
        for (i in 0..(Place.MAX_PLACES-1)) {
            if (state.live(i) > 0) { quiet = false; break; }
            if (state.liveAdopted(i) > 0) { quiet = false; break; }
            for (j in 0..(Place.MAX_PLACES-1)) {
                val idx = i*Place.MAX_PLACES + j;
                if (state.transit(idx) > 0) { quiet = false; break; }
                if (state.transitAdopted(idx) > 0) { quiet = false; break; }
            }
            if (!quiet) break;
        }
        if (verbose>=2) debug("quiescent(id="+id+") returning " + quiet);
        return quiet;
    }
    private def addDeadPlaceException(state:State, placeId:Long) {
        val e = new DeadPlaceException(Place(placeId));
        e.fillInStackTrace(); // meaningless?
        state.excs.add(e);
    }
}
