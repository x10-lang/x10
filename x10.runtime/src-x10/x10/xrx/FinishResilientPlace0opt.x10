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
import x10.util.concurrent.*;

import x10.array.Array_2;
import x10.array.Array_3;

import x10.io.CustomSerialization;
import x10.io.Deserializer;
import x10.io.Serializer;

import x10.compiler.*; // for @RemoteInvocation

/*
 * Place0-based Resilient Finish + Various Optimizations
 * Note that this version does not use ResilientStorePlace0
 */
class FinishResilientPlace0opt extends FinishResilient implements CustomSerialization {
    private static val verbose = FinishResilient.verbose;
    private static val place0 = Place.FIRST_PLACE;
    private static val hereId = Runtime.hereLong();
    private static val MAX_PLACES = Place.numPlaces(); // TODO: remove the MAX_PLACES dependency to support elastic X10
    
    private static val AT = 0;
    private static val ASYNC = 1;
    private static val AT_AND_ASYNC = AT..ASYNC;
    
    /*
     * Additional data structures
     */
    // FinishID and utility methods
    private static struct FinishID(placeId:Long,localId:Long) { // unique id
        public def toString():String = "[" + placeId + "," + localId + "]";
        // equals need not be overridden 
    }
    private static val FinishID_NULL = FinishID(-1,-1);
    private static val nextId = new AtomicLong();
    private static def getNewFinishId() = FinishID(hereId, nextId.getAndIncrement());
    private static def getHomeId(id:FinishID) = id.placeId;
    private static def isAtHome(id:FinishID) = (getHomeId(id) == hereId);
    
    // LocalState - one (or zero) per place for each FinishState
    private abstract static class LocalState {
        val localLive = new AtomicInteger();
        static class Home(latch:SimpleLatch, ancestorId:FinishID) extends LocalState {
            // additional fields for home, ancestorId is a direct ancestor at remote place
            val excs = new GrowableRail[CheckedThrowable]();
            val dpes = new HashSet[Long](); // places DeadPlaceExceptions should be thrown (merged back from VitalState)
            val hasVitalState = new AtomicInteger();
        }
        static class Remote extends LocalState {
            // no additional fields for remote
        }
    }
    
    // VitalState - stored at Place0, exists only when activities exist remote places
    private static class VitalState {
        // remoteCount indicates that remote FS exists at the place
        val remoteCount    = new Array_2[Int](MAX_PLACES, 2); // keep AT and ASYNC separately
        val remoteAdopted  = new Array_2[Int](MAX_PLACES, 2);
        // transitCount indicates activity is being created at another place
        val transitCount   = new Array_3[Int](MAX_PLACES, MAX_PLACES, 2);
        val transitAdopted = new Array_3[Int](MAX_PLACES, MAX_PLACES, 2);

        var numDead:Long = 0; // number of dead places already handled for this state
        val deadPlaces = new HashSet[Long](); // places DeadPlaceExceptions should be thrown
        val descendantIds = new GrowableRail[FinishID](); // descendants at remote places
        var adopterId:FinishID = FinishID_NULL; // adopter (if adopted), may be overwritten multiple times
        def isAdopted() = (adopterId != FinishID_NULL); // if adopterId is set, this FS should be in the adopter's descendantIds

        def dump(msg:Any) {
            val s = new StringBuilder(); s.add(msg); s.add('\n');
            s.add("     remoteCount:"); for (v in remoteCount   ) s.add(" " + v); s.add('\n');
            s.add("   remoteAdopted:"); for (v in remoteAdopted ) s.add(" " + v); s.add('\n');
            s.add("    transitCount:"); for (v in transitCount  ) s.add(" " + v); s.add('\n');
            s.add("  transitAdopted:"); for (v in transitAdopted) s.add(" " + v); s.add('\n');
            s.add("      deadPlaces:"); for (v in deadPlaces    ) s.add(" " + v); s.add('\n');
            s.add(" descendant.size: " + descendantIds.size()); s.add('\n');
            s.add("       adopterId: " + adopterId); s.add('\n');
            debug(s.toString());
        }
    }
    
    /*
     * Implementation of this class
     */
    // per-place static fields
    private static val localStates = new HashMap[FinishID,LocalState](); // all LocalStates in each place
    private static val vitalStates = (hereId==0) ? new HashMap[FinishID,VitalState]() : null; // all VitalStates at Place0
    
    // instance fields
    private val id:FinishID; // should be global
    private transient val localState:LocalState; // LocalState.Home or LocalState.Remote
    public def toString():String = System.identityToString(this) + "(id="+id+")";
    
    // create a FinishState
    static def make(parent:FinishState, latch:SimpleLatch):FinishResilientPlace0opt {
        if (verbose>=1) debug(">>>> FinishResilientPlace0opt.make called, parent="+parent + " latch="+latch);
        val fs = new FinishResilientPlace0opt(parent, latch);
        fs.localState.localLive.set(1n); // for myself, will be decremented in waitForFinish
        if (verbose>=1) debug("<<<< FinishResilientPlace0opt.make returning fs="+fs);
        return fs;
    }
    private def this(parent:FinishState, latch:SimpleLatch) { // called only from make
        id = getNewFinishId();
        if (verbose>=2) debug("get new FinishID, id="+id);
        // find an ancestor at remote place
        var acId:FinishID;
        if (parent instanceof FinishResilientPlace0opt) {
            val p = parent as FinishResilientPlace0opt;
            if (isAtHome(p.id)) {
                acId = (p.localState as LocalState.Home).ancestorId;
                if (verbose>=2) debug("ancestor is parent's ancestor, ancestorId="+acId);
            } else {
                acId = p.id;
                if (verbose>=2) debug("ancestor is parent, ancestorId="+acId);
            }
        } else {
                acId = FinishID_NULL;
                if (verbose>=2) debug("ancestor is NULL, ancestorId="+acId);
        }
        localState = new LocalState.Home(latch, acId);
        atomic { localStates.put(id, localState); }
    }
    public def serialize(s:Serializer) {
        if (verbose>=2) debug("serializing FinishID, id="+id);
        s.writeAny(id);
    }
    private def this(ds:Deserializer) { // called when FS is sent from another place
        id = ds.readAny() as FinishID;
        if (verbose>=2) debug("deserialized FinishID, id="+id);
        if (isAtHome(id)) { // home place
            atomic { localState = localStates.getOrThrow(id); } // should exist
            assert localState instanceof LocalState.Home;
        } else { // remote place
            var ls:LocalState;
            atomic {
                ls = localStates.getOrElse(id, null);
                if (ls == null) { ls = new LocalState.Remote(); localStates.put(id, ls); }
            }
            localState = ls;
            assert localState instanceof LocalState.Remote;
        }
    }
    
    // new activity being spawned to a place
    def notifySubActivitySpawn(place:Place):void {
        notifySubActivitySpawn(place, ASYNC);
    }
    def notifyShiftedActivitySpawn(place:Place):void {
        notifySubActivitySpawn(place, AT);
    }
    private def notifySubActivitySpawn(place:Place, kind:Long):void {
        val srcId = hereId, dstId = place.id;
        if (verbose>=1) debug(">>>> notifySubActivitySpawn(id="+id+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);
        
        //   home to home        -> localLive++ here, to avoid racing
        // remote to sameRemote  -> localLive++ here, to avoid racing
        if (srcId == dstId) { // local creation, localLive++ here
            val c = localState.localLive.getAndIncrement(); // localLive++
            if (verbose>=2) debug("spawning to the same place, localLive++ here");
            if (verbose>=1) debug("<<<< notifySubActivitySpawn(id="+id+") returning, src==dst");
            return;
        }
        
        //   home to remote      -> create VS (if not); hasVS++ (if created); VS.transit(src,dst)++;
        // remote to otherRemote -> VS.transit(src,dst)++
        // remote to home        -> VS.transit(src,dst)++
        if (verbose>=2) debug("spawning to another place, need transit++");
        val _id = id;
        val acId = isAtHome(id) ? (localState as LocalState.Home).ancestorId : FinishID_NULL;
        val vsCreated = Runtime.evalImmediateAt[Boolean](place0, ()=>{ atomic {
            var vsCreated:Boolean = false;
            var vs:VitalState = vitalStates.getOrElse(_id, null);
            if (vs == null) { // create a new VitalState
                if (verbose>=2) debug("creating a new VitalState for id="+_id+", ancestorId="+acId);
                assert srcId == getHomeId(_id);
                vs = new VitalState(); vitalStates.put(_id, vs); vsCreated = true;
                if (acId == FinishID_NULL) {
                    // this only happens if the ancestor is the top-level finish
                } else {
                    var acVS:VitalState = vitalStates.getOrThrow(acId); // should exist
                    if (acVS.isAdopted()) acVS = vitalStates.getOrThrow(acVS.adopterId); // should exist
                    assert !acVS.isAdopted();
                    acVS.descendantIds.add(_id);
                }
            }

            vs.transitCount(srcId, dstId, kind)++;
            if (vs.isAdopted()) {
                val adopterVS = vitalStates.getOrThrow(vs.adopterId); // should exist
                adopterVS.transitAdopted(srcId, dstId, kind)++;
            }
            if (verbose>=3) vs.dump("DUMP id="+_id);
            if (Place.isDead(dstId)) {
                if (verbose>=2) debug("target place is already dead, try quiescent_check");
                quiescent_check(_id); // the case the dst is (already) dead, try to remove the VS and set DPE
                                      // TODO: hasVS may tentatatively become -1, is this ok?
                if (vs.isAdopted()) quiescent_check(vs.adopterId);
            }
            
            return vsCreated;
        }});
        if (vsCreated) {
            assert isAtHome(id);
            (localState as LocalState.Home).hasVitalState.incrementAndGet(); // use AtomicLong to avoid racing
        }
        if (verbose>=1) debug("<<<< notifySubActivitySpawn(id="+id+") returning");
    }

    def notifyRemoteContinuationCreated():void { 
        Console.OUT.println("FIXME.  Need to create 'vital state'!");
    }

    
    // activity from srcPlace is being created
    def notifyActivityCreation(srcPlace:Place, activity:Activity):Boolean {
        return notifyActivityCreation(srcPlace, activity, false/*non-blocking*/, ASYNC);
    }
    def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity):Boolean {
        return notifyActivityCreation(srcPlace, activity, true/*blocking*/, ASYNC);
    }
    def notifyShiftedActivityCreation(srcPlace:Place, activity:Activity):Boolean {
        return notifyActivityCreation(srcPlace, activity, true/*blocking*/, AT);
    }
    private def notifyActivityCreation(srcPlace:Place, activity:Activity, blocking:Boolean, kind:Long):Boolean {
        val srcId = srcPlace.id, dstId = hereId;
        if (verbose>=1) debug(">>>> notifyActivityCreation(id="+id+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind+" activity="+activity+" blocking="+blocking);
        if (Place.isDead(srcId)) {
            if (verbose>=1) debug("<<<< notifyActivityCreation(id="+id+") returning false, srcPlace is dead");
            return false;
        }
        
        //   home to home        -> do nothing (localLive already incremented)
        // remote to sameRemote  -> do nothing (localLive already incremented)
        if (srcId == dstId) { // local creation, no need to transit--
            if (verbose>=1) debug("<<<< notifyActivityCreation(id="+id+") returning true, src==dst");
            return true;
        }

        //   home to remote      -> localLive++; if (first) VS.remote(dst)++; VS.transit(src,dst)--
        // remote to otherRemote -> localLive++; if (first) VS.remote(dst)++; VS.transit(src,dst)--
        // remote to home        -> localLive++; VS.transit(src,dst)--; quiescent_check (to delete VS)
        val c = localState.localLive.getAndIncrement(); // localLive++
        val _id = id;
        val isFirst = (c==0n); // activity is newly created here
        
      if (blocking) { // blocking case, normal code
        assert activity==null;
        val isRegistered = Runtime.evalImmediateAt[Boolean](place0, ()=>{ atomic {
          if (Place.isDead(srcId)) {
            if (verbose>=2) debug("srcPlace is dead, not register the new activity");
            return false;
          } else {
            val isToHome = (dstId==getHomeId(_id));
            val vs = vitalStates.getOrThrow(_id); // should exist
            if (isFirst && !isToHome) vs.remoteCount(dstId, kind)++;
            vs.transitCount(srcId, dstId, kind)--;
            if (vs.isAdopted()) {
                val adopterVS = vitalStates.getOrThrow(vs.adopterId); // should exist
                assert !isToHome; // vs.isAdopted means that home is dead
                if (isFirst) adopterVS.remoteAdopted(dstId, kind)++;
                adopterVS.transitAdopted(srcId, dstId, kind)--;
            }
            if (verbose>=3) vs.dump("DUMP id="+_id);
            if (isToHome) quiescent_check(_id); // to delete VS if necessary
            return true;
          }
        }});
        if (verbose>=1) debug("<<<< notifyActivityCreation(id="+id+") returning "+isRegistered);
        return isRegistered;

      } else { // non-blocking case, cannot use runImmediateAsync
        
        val pendingActivity = GlobalRef(activity); 
        at (place0) @Immediate("Place0opt_notifyActivityCreation_to_zero") async {
         atomic {

          // this part is same as above except returning nothing
          if (Place.isDead(srcId)) {
            if (verbose>=2) debug("srcPlace is dead, not register the new activity");
            //return false;
          } else {
            val isToHome = (dstId==getHomeId(_id));
            val vs = vitalStates.getOrThrow(_id); // should exist
            if (isFirst && !isToHome) vs.remoteCount(dstId, kind)++;
            vs.transitCount(srcId, dstId, kind)--;
            if (vs.isAdopted()) {
                val adopterVS = vitalStates.getOrThrow(vs.adopterId); // should exist
                assert !isToHome; // vs.isAdopted means that home is dead
                if (isFirst) adopterVS.remoteAdopted(dstId, kind)++;
                adopterVS.transitAdopted(srcId, dstId, kind)--;
            }
            if (verbose>=3) vs.dump("DUMP id="+_id);
            if (isToHome) quiescent_check(_id); // to delete VS if necessary
            //return true;
          }
         } // atomic

          // push the pending activity
            at (pendingActivity) @Immediate("notifyActivityCreation_push_activity") async {
                val pa = pendingActivity();
                if (pa != null && pa.epoch == Runtime.epoch()) {
                    if (verbose>=1) debug("<<<< notifyActivityCreation(id="+id+") finally submitting activity");
                    Runtime.worker().push(pa);
                }
                pendingActivity.forget();
            }
        }
        // Return false because we want to defer pushing the activity.
        return false;                
      }
    }
    
    def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void {
        notifyActivityCreationFailedOrTerminated(srcPlace, t, ASYNC);
    }
    def notifyActivityCreatedAndTerminated(srcPlace:Place) {
        notifyActivityCreationFailedOrTerminated(srcPlace, null, ASYNC);
    }
    private def notifyActivityCreationFailedOrTerminated(srcPlace:Place, t:CheckedThrowable, kind:Long):void { 
        val srcId = srcPlace.id, dstId = hereId;
        if (verbose>=1) debug(">>>> notifyActivityCreationFailedOrTerminated(id="+id+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind+" t="+t);
        
        //   home to home        -> do nothing (localLive already incremented)
        // remote to sameRemote  -> do nothing (localLive already incremented)
        if (srcId == dstId) { // local creation, no need to transit--
            //@@@@ Do we need to do localLive--, or this case should not happen??
            if (verbose>=1) debug("<<<< notifyActivityCreationFailedOrTerminated(id="+id+") returning, src==dst");
            return;
        }
        
        //   home to remote      -> VS.transit(src,dst)--; quiescent_check
        // remote to otherRemote -> VS.transit(src,dst)--; quiescent_check
        // remote to home        -> VS.transit(src,dst)--; quiescent_check
        val _id = id;
        at (place0) @Immediate("Place0opt_notifyActivityCreationFailedOrTerminated_to_zero") async {
          atomic { //@@@@ need to verify this code
            if (verbose>=2) debug("notifyActivityCreationFailedOrTerminated running at place0");
            val vs = vitalStates.getOrThrow(_id); // should exist
            vs.transitCount(srcId, dstId, kind)--;
            if (t != null) {
                val ls = localStates.getOrThrow(_id) as LocalState.Home; // should exist
                ls.excs.add(t); // already in atomic region
            }
            quiescent_check(_id);
            if (vs.isAdopted()) {
                val adopterVS = vitalStates.getOrThrow(vs.adopterId); // should exist
                adopterVS.transitAdopted(srcId, dstId, kind)--;
                //@@@@ do we need to do excs.add(t) here?
                quiescent_check(vs.adopterId);
            }
            if (verbose>=3) vs.dump("DUMP id="+_id);
          }
       }
       if (verbose>=1) debug("<<<< notifyActivityCreationFailedOrTerminated(id="+id+") returning");
    }
    
    // activity run here is being terminated
    def notifyActivityTermination():void {
        notifyActivityTermination(ASYNC);
    }
    def notifyShiftedActivityCompletion():void {
        notifyActivityTermination(AT);
    }
    def notifyActivityTermination(kind:Long):void {
        val dstId = hereId;
        if (verbose>=1) debug(">>>> notifyActivityTermination(id="+id+") called, dstId="+dstId);
        
        val c = localState.localLive.decrementAndGet(); // localLive--
        if (c > 0n) {
            if (verbose>=1) debug("<<<< notifyActivityTermination(id="+id+") returning, localLive="+c);
            return;
        }
        
        // localLive==0 && home && !hasVS  -> latch.release
        // localLive==0 && home &&  hasVS  -> do_nothing @@OK?@@
        if (isAtHome(id)) {
            val ls = localState as LocalState.Home;
            val hasVS = ls.hasVitalState.get();
            if (hasVS == 0n) {
                if (verbose>=2) debug("calling latch.release for id="+id);
                ls.latch.release();
            }
            if (verbose>=1) debug("<<<< notifyActivityTermination(id="+id+") returning, localLive="+c+", hasVS="+hasVS);
            return;
        }
        
        // localLive==0 && remote -> localStates.remove(id); VS.remote--; quiescent_check
        atomic { localStates.remove(id); }
        val _id = id;
        Runtime.runImmediateAt(place0, ()=>{ atomic {
            val vs = vitalStates.getOrThrow(_id); // should exist
            vs.remoteCount(dstId, kind)--; quiescent_check(_id);
            if (vs.isAdopted()) {
                val adopterVS = vitalStates.getOrThrow(vs.adopterId); // should exist
                adopterVS.remoteAdopted(dstId, kind)--;
                quiescent_check(vs.adopterId);
            }
        }});
        if (verbose>=1) debug("<<<< notifyActivityTermination(id="+id+") returning");
    }
    
    // wait for the quiescence
    def waitForFinish():void { // can be called only for the original local FinishState returned by make
        assert isAtHome(id);
        if (verbose>=1) debug(">>>> waitForFinish(id="+id+") called");
        
        notifyActivityTermination(); // terminate myself
        val ls = (localState as LocalState.Home);
        
        // If there is no remote activities, see if this worker
        // can execute other asyncs that are governed by the finish before waiting on the latch.
        val hasVS = ls.hasVitalState.get(); // hasVS==0 means no remote activities
        if ((!Runtime.STRICT_FINISH) && (Runtime.STATIC_THREADS || hasVS==0n)) {
            if (verbose>=2) debug("calling worker.join for id="+id);
            Runtime.worker().join(ls.latch);
        }
        
        // wait for the latch release
        if (verbose>=2) debug("calling latch.await for id="+id);
        ls.latch.await(); // wait for the termination (latch may already be released)
        if (verbose>=2) debug("returned from latch.await for id="+id);
        
        // remove this FS and return (or throw MultipleExceptions)
        atomic { localStates.remove(id); }
        for (placeId in ls.dpes) // merge DPEs
            ls.excs.add(new DeadPlaceException(Place(placeId))); // TODO: stackTrace is meaningless
        val exc = MultipleExceptions.make(ls.excs); // may return null
        
        if (verbose>=1) debug("<<<< waitForFinish(id="+id+") returning, exc="+exc);
        if (exc != null) throw exc;
    }
    
    // push an exception
    def pushException(t:CheckedThrowable):void {
        if (verbose>=1) debug(">>>> pushException(id="+id+") called, t="+t);
        if (isAtHome(id)) {
            val ls = localState as LocalState.Home;
            atomic { ls.excs.add(t); }
        } else {
            val _id = id;
            Runtime.runImmediateAt(Place(getHomeId(id)), ()=>{ atomic { // just ignored if home place is dead
                val ls = localStates.getOrThrow(_id) as LocalState.Home; // should exist
                atomic { ls.excs.add(t); }
            }});
        }
        if (verbose>=1) debug("<<<< pushException(id="+id+") returning");
    }
    
    // called when a place death is detected
    static def notifyPlaceDeath():void {
        if (verbose>=1) debug(">>>> notifyPlaceDeath called");
        if (hereId != 0) {
            if (verbose>=2) debug("not Place0, returning");
            return;
        }
        atomic {
            val ids = new GrowableRail[FinishID](vitalStates.size());
            for (id in vitalStates.keySet()) ids.add(id); // copy ids first to avoid concurrency bug
            for (i in 0..(ids.size()-1)) {
                val id = ids(i);
                quiescent_check(id); // this may trigger vitalStates.remove(id)
            }
        }
        if (verbose>=1) debug("<<<< notifyPlaceDeath returning");
    }
    
    // check the quiescence of a VitalState, and release the latch at home if necessary
    private static def quiescent_check(id:FinishID):void {
        // should be called inside atomic
        assert hereId == 0;
        if (verbose>=2) debug("quiescent_check(id="+id+") called");
        val vs = vitalStates.getOrThrow(id); // should exist
        
        // 1 pull up dead descendants
        val nd = Place.numDead();
        if (nd != vs.numDead && !vs.isAdopted()) {
            vs.numDead = nd;
            val dcIds = vs.descendantIds;
            // check all descendants, including newly added ones
            for (var dcIdx:Long = 0; dcIdx < dcIds.size(); ++dcIdx) {
                val dcId = dcIds(dcIdx);
                if (dcId == FinishID_NULL) continue; // the entry has been removed
                if (!Place.isDead(getHomeId(dcId))) continue; // the descendant FS is alive
                val dcVS = vitalStates.getOrElse(dcId, null);
                if (dcVS == null) { dcIds(dcIdx) = FinishID_NULL; continue; } // the dcVS has been removed

                // adopt the descendant
                if (verbose>=3) dcVS.dump("adopting descendantId="+dcId);
                if (dcVS.isAdopted()) { // already adopted, no need to copy info
                    dcVS.adopterId = id; // may be overwriting the old value
                    assert dcVS.descendantIds.size() == 0;
                } else { // newly adopt the descendant
                    dcVS.adopterId = id;
                    dcIds.addAll(dcVS.descendantIds); // these will be checked in the following iteration
                    dcVS.descendantIds.clear();
                    for (k in AT_AND_ASYNC) {
                      for (i in 0..(MAX_PLACES-1)) {
                        vs.remoteAdopted(i,k) += (dcVS.remoteCount(i,k) + dcVS.remoteAdopted(i,k));
                        dcVS.remoteAdopted(i,k) = 0n;
                        // don't clear dcVS.remoteCount, which will be checked to remove this dcVS
                        for (j in 0..(MAX_PLACES-1)) {
                            vs.transitAdopted(i,j,k) += (dcVS.transitCount(i,j,k) + dcVS.transitAdopted(i,j,k));
                            dcVS.transitAdopted(i,j,k) = 0n;
                            // don't clear dcVS.transitCount, which will be checked to remove this dcVS
                        }
                      }
                    }
                    // need not copy the deadPlaces info to the adopter
                }
            } // for (dcIdx)
        }
        
        // 2 delete dead entries
        for (i in 0..(MAX_PLACES-1)) {
            if (!Place.isDead(i)) continue;
            if (vs.remoteCount(i, ASYNC) > 0n) { // only the ASYNC counter is converted to DPE
                if (verbose>=3) debug("adding DPE("+i+") for remoteCount("+i+")");
                vs.deadPlaces.add(i);
            }
            vs.remoteCount(i, AT) = vs.remoteAdopted(i, AT) = 0n;
            vs.remoteCount(i, ASYNC) = vs.remoteAdopted(i, ASYNC) = 0n;
            for (j in 0..(MAX_PLACES-1)) {
                vs.transitCount(i,j, AT) = vs.transitAdopted(i,j, AT) = 0n;
                vs.transitCount(i,j, ASYNC) = vs.transitAdopted(i,j, ASYNC) = 0n;
                if (vs.transitCount(j,i, ASYNC) > 0n) { // only the ASYNC counter is converted to DPE
                    if (verbose>=3) debug("adding DPE("+i+") for transitCount("+j+","+i+")");
                    vs.deadPlaces.add(i);
                }
                vs.transitCount(j,i, AT) = vs.transitAdopted(j,i, AT) = 0n;
                vs.transitCount(j,i, ASYNC) = vs.transitAdopted(j,i, ASYNC) = 0n;
            }
        }
        
        // 3 quiescent check
        if (verbose>=3) vs.dump("DUMP id="+id);
        var quiet:Boolean = true;
        outer: for (k in AT_AND_ASYNC) {
          for (i in 0..(MAX_PLACES-1)) {
            if (vs.remoteCount(i,k) > 0n) { quiet = false; break outer; }
            if (vs.remoteAdopted(i,k) > 0n) { quiet = false; break outer; }
            for (j in 0..(MAX_PLACES-1)) {
                if (vs.transitCount(i,j,k) > 0n) { quiet = false; break outer; }
                if (vs.transitAdopted(i,j,k) > 0n) { quiet = false; break outer; }
            }
          }
        }
        if (quiet) {
            if (verbose>=2) debug("removing vitalState(id="+id+")");
            vitalStates.remove(id);
                // better to remove id from ancestor(adopter)'s descendantIds, but we don't know it
                // therefore, the descendantIds entry will be cleared at the ancestor's Step1
            val dpes = vs.deadPlaces;
            at (Place(getHomeId(id))) @Immediate("quiescent_check_vsRemoved") async { // just ignored if home place is dead @@@@TOCHECK
                var ls:LocalState.Home;
                atomic { // should not cause deadlock because this is executed asynchronously @@@@TOCHECK
                    ls = localStates.getOrThrow(id) as LocalState.Home; // should exist
                    for (placeId in dpes) ls.dpes.add(placeId);
                }
                val hasVS = ls.hasVitalState.decrementAndGet();
                val c = ls.localLive.get();
                if (hasVS == 0n && c <= 0n) {
                    if (verbose>=2) debug("calling latch.release for id="+id);
                    ls.latch.release();
                }
            }
        }
        
        if (verbose>=2) debug("quiescent_check(id="+id+") returning, quiet="+quiet);
    }
}

