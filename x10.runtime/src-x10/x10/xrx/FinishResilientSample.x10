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

import x10.util.concurrent.SimpleLatch;
import x10.util.*;
import x10.compiler.Immediate;

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
//     def notifyActivityCreation(srcPlace:Place, activity:Activity):Boolean {
//         if (verbose>=1) debug("notifyActivityCreation called, srcPlace.id=" + srcPlace.id);
//         if (srcPlace.isDead()) return false; return true;
//     }
//     def notifyActivityTermination():void {
//         if (verbose>=1) debug("notifyActivityTermination called");
//     }
//     def pushException(t:CheckedThrowable):void {
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
    
    private static val RS = getResilientStore("FinishResilientSample");
    private static def getResilientStore(name:String):ResilientStore[FinishID,State] {
        switch (Runtime.RESILIENT_MODE) { //TODO: this should be controlled by another environment
        case Configuration.RESILIENT_MODE_SAMPLE:
            return ResilientStorePlace0.make[FinishID,State](name);
        case Configuration.RESILIENT_MODE_SAMPLE_HC:
            return ResilientStoreHC.make2[FinishID,State](name, FinishID.NULL); // to avoid XTENLANG-3396
        default:
            throw new UnsupportedOperationException("Unsupported RESILIENT_MODE " + Runtime.RESILIENT_MODE);
        }
    }
    
    private static struct FinishID(placeId:Long,localId:Long) { // unique id used as a key for ResilientStore
        public static val NULL = FinishID(-1,-1);
        public def toString():String = "[" + placeId + "," + localId + "]";
        // equals need not be overridden 
    }
    
    // FIXME: Once we stabilize the implementation, switch to using Array_2
    //        instead of Rail for the logically 2-D arrays.
    private static class State { // data stored into ResilientStore
        val NUM_PLACES = Place.numPlaces();
        val transit = new Rail[Int](NUM_PLACES * NUM_PLACES, 0n);
        val transitAdopted = new Rail[Int](NUM_PLACES * NUM_PLACES, 0n);
        val live = new Rail[Int](NUM_PLACES, 0n);
        val liveAdopted = new Rail[Int](NUM_PLACES, 0n);
        val excs = new GrowableRail[CheckedThrowable](); // exceptions to report
        val children = new GrowableRail[FinishID](); // children
        var adopterId:FinishID = FinishID.NULL; // adopter (if adopted)
        def isAdopted() = (adopterId != FinishID.NULL);
        var numDead:Long = 0;
        def dump(msg:Any) {
            val s = new StringBuilder(); s.add(msg); s.add('\n');
            s.add("           live:"); for (v in live          ) s.add(" " + v); s.add('\n');
            s.add("    liveAdopted:"); for (v in liveAdopted   ) s.add(" " + v); s.add('\n');
            s.add("        transit:"); for (v in transit       ) s.add(" " + v); s.add('\n');
            s.add(" transitAdopted:"); for (v in transitAdopted) s.add(" " + v); s.add('\n');
            s.add("  children.size: " + children.size()); s.add('\n');
            s.add("      adopterId: " + adopterId);
            debug(s.toString());
        }
    }
    
    // all active finishes in this place
    private static val ALL = new GrowableRail[FinishResilientSample](); //TODO: reuse localIds
    
    // fields of this FinishState
    private val id:FinishID; // should be global
    private transient val latch:SimpleLatch; // latch is stored only in the original local finish
    
    public def toString():String = System.identityToString(this) + "(id="+id+")";
    
    private def this(id:FinishID, latch:SimpleLatch) { this.id = id; this.latch = latch; }
    static def make(parent:FinishState, latch:SimpleLatch):FinishResilientSample {
        if (verbose>=1) debug(">>>> FinishResilientSample.make called, parent="+parent + " latch="+latch);
        val parentId = (parent instanceof FinishResilientSample) ? (parent as FinishResilientSample).id : FinishID.NULL; // ok to ignore other cases?
        
        // create FinishState
        var id:FinishID, fs:FinishResilientSample;
       atomic {
        val placeId = here.id, localId = ALL.size();
        id = FinishID(placeId, localId);
        fs = new FinishResilientSample(id, latch);
        ALL.add(fs); // will be used in notifyPlaceDeath, and removed in waitForFinish
       }
        assert ALL(fs.id.localId)==fs;
        
        // create State in ResilientStore
        val state = new State();
        state.live(here.id) = 1n; // for myself, will be decremented in waitForFinish
       RS.lock();
        RS.create(id, state);
        if (parentId != FinishID.NULL) {
            val parentState = RS.getOrElse(parentId, null);
            parentState.children.add(id);
            RS.put(parentId, parentState);
        }
       RS.unlock();
        
        if (verbose>=1) debug("<<<< FinishResilientSample.make returning fs="+fs);
        return fs;
    }
    
    static def notifyPlaceDeath():void {
        if (verbose>=1) debug(">>>> notifyPlaceDeath called");
        if (RS instanceof ResilientStorePlace0[FinishID,State]) { //TODO: clean up this
            (RS as ResilientStorePlace0[FinishID,State]).notifyPlaceDeath();
        }
        
        if (verbose>=2) debug("notifyPlaceDeath acquiring locks");
       RS.lock();
       atomic {
        if (verbose>=2) debug("notifyPlaceDeath acquired locks, processing local fs");
        for (localId in 0..(ALL.size()-1)) {
            val fs = ALL(localId);
            if (verbose>=2) debug("notifyPlaceDeath checking localId=" + localId + " fs=" + fs);
            if (fs == null) continue;
            if (quiescent(fs.id)) releaseLatch(fs.id);
        }
       }
       RS.unlock();
        if (verbose>=2) debug("<<<< notifyPlaceDeath released locks and returning");
    }
    
    private static def releaseLatch(id:FinishID) { // can be called from any place
        if (verbose>=2) debug("releaseLatch(id="+id+") called");
        at (Place(id.placeId)) @Immediate("releaseLatch") async {
            val fs = ALL(id.localId); // get the original local FinishState
            if (verbose>=2) debug("calling latch.release for id="+id);
            fs.latch.release(); // latch.await is in waitForFinish
        }
        if (verbose>=2) debug("releaseLatch(id="+id+") returning");
    }
    
    private def getCurrentAdopterId():FinishID {
        // assert RS.isLocked();
        var currentId:FinishID = id;
        while (true) {
            assert currentId!=FinishID.NULL;
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
            state.transit(srcId*state.NUM_PLACES + dstId)++;
            RS.put(id, state);
        } else {
            val adopterId = getCurrentAdopterId();
            val adopterState = RS.getOrElse(adopterId, null);
            adopterState.transitAdopted(srcId*state.NUM_PLACES + dstId)++;
            RS.put(adopterId, adopterState);
        }
        if (verbose>=3) state.dump("DUMP id="+id);
       RS.unlock();
        if (verbose>=1) debug("<<<< notifySubActivitySpawn(id="+id+") returning");
    }

    def notifyRemoteContinuationCreated():void { 
        // A no-op for this finish
    }
    
    def notifyActivityCreation(srcPlace:Place, activity:Activity):Boolean {
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
            state.transit(srcId*state.NUM_PLACES + dstId)--;
            RS.put(id, state);
        } else {
            val adopterId = getCurrentAdopterId();
            val adopterState = RS.getOrElse(adopterId, null);
            adopterState.liveAdopted(dstId)++;
            adopterState.transitAdopted(srcId*state.NUM_PLACES + dstId)--;
            RS.put(adopterId, adopterState);
        }
        if (verbose>=3) state.dump("DUMP id="+id);
       RS.unlock();
        if (verbose>=1) debug("<<<< notifyActivityCreation(id="+id+") returning true");
        return true;
    }

    def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity):Boolean {
        return notifyActivityCreation(srcPlace, activity);
    }

    def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void { 
        // TODO! A real implementation of this functionality.
        debug("<<<< notifyActivityCreationFailed unimplemented. Program execution is likely dead in the water");
        t.printStackTrace();
    }

    def notifyActivityCreatedAndTerminated(srcPlace:Place) {
        notifyActivityCreation(srcPlace, null);
        notifyActivityTermination();
    }
    
    def notifyActivityTermination():void {
        val dstId = here.id;
        if (verbose>=1) debug(">>>> notifyActivityTermination(id="+id+") called, dstId="+dstId);
       RS.lock();
        val state = RS.getOrElse(id, null);
        if (!state.isAdopted()) {
            state.live(dstId)--;
            RS.put(id, state);
            if (quiescent(id)) releaseLatch(id);
        } else {
            val adopterId = getCurrentAdopterId();
            val adopterState = RS.getOrElse(adopterId, null);
            adopterState.liveAdopted(dstId)--;
            RS.put(adopterId, adopterState);
            if (quiescent(adopterId)) releaseLatch(adopterId);
        }
       RS.unlock();
        if (verbose>=1) debug("<<<< notifyActivityTermination(id="+id+") returning");
    }
    
    def pushException(t:CheckedThrowable):void {
        if (verbose>=1) debug(">>>> pushException(id="+id+") called, t="+t);
       RS.lock();
        val state = RS.getOrElse(id, null);
        state.excs.add(t); // need not consider the adopter
        RS.put(id, state);
       RS.unlock();
        if (verbose>=1) debug("<<<< pushException(id="+id+") returning");
    }
    
    def waitForFinish():void { // can be called only for the original local FinishState returned by make
        assert id.placeId==here.id;
        assert latch!=null; // original local FinishState
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
        atomic { ALL(id.localId) = null; }
       RS.unlock();
        if (verbose>=1) debug("<<<< waitForFinish(id="+id+") returning, exc="+e);
        if (e != null) throw e;
    }
    
    private static def quiescent(id:FinishID):Boolean {
        if (verbose>=2) debug("quiescent(id="+id+") called");
        // assert RS.isLocked();
        val state = RS.getOrElse(id, null);
        if (state==null) { // already finished
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
                if (!Place.isDead(childId.placeId)) continue;
                val childState = RS.getOrElse(childId, null);
                if (childState==null) continue; // already finished
                val lastChildId = children.removeLast();
                if (chIndex < children.size()) children(chIndex) = lastChildId;
                chIndex--; // don't advance this iteration
                // adopt the child
                if (verbose>=3) debug("adopting childId="+childId);
                if (verbose>=3) childState.dump("DUMP childId="+childId);
                assert !childState.isAdopted();
                childState.adopterId = id;
                RS.put(childId, childState);
                state.children.addAll(childState.children); // will be checked in the following iteration
                for (i in 0..(state.NUM_PLACES-1)) {
                    state.liveAdopted(i) += (childState.live(i) + childState.liveAdopted(i));
                    for (j in 0..(state.NUM_PLACES-1)) {
                        val idx = i*state.NUM_PLACES + j;
                        state.transitAdopted(idx) += (childState.transit(idx) + childState.transitAdopted(idx));
                    }
                }
            } // for (chIndex)
        }
        // 2 delete dead entries
        for (i in 0..(state.NUM_PLACES-1)) {
            if (Place.isDead(i)) {
                for (unused in 1..state.live(i)) {
                    if (verbose>=3) debug("adding DPE for live("+i+")");
                    addDeadPlaceException(state, i);
                }
                state.live(i) = 0n; state.liveAdopted(i) = 0n;
                for (j in 0..(state.NUM_PLACES-1)) {
                    val idx = i*state.NUM_PLACES + j;
                    state.transit(idx) = 0n; state.transitAdopted(idx) = 0n;
                    val idx2 = j*state.NUM_PLACES + i;
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
        for (i in 0..(state.NUM_PLACES-1)) {
            if (state.live(i) > 0) { quiet = false; break; }
            if (state.liveAdopted(i) > 0) { quiet = false; break; }
            for (j in 0..(state.NUM_PLACES-1)) {
                val idx = i*state.NUM_PLACES + j;
                if (state.transit(idx) > 0) { quiet = false; break; }
                if (state.transitAdopted(idx) > 0) { quiet = false; break; }
            }
            if (!quiet) break;
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
