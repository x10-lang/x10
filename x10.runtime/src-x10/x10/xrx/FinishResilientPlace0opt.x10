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
import x10.util.concurrent.SimpleLatch;

import x10.util.*;
import x10.util.concurrent.*;

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
        val remoteCount    = new Rail[Int](MAX_PLACES, 0n);
        val remoteAdopted  = new Rail[Int](MAX_PLACES, 0n);
        // transitCount indicates activity is being created at another place
        val transitCount   = new Rail[Int](MAX_PLACES * MAX_PLACES, 0n);
        val transitAdopted = new Rail[Int](MAX_PLACES * MAX_PLACES, 0n);

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
        val srcId = hereId, dstId = place.id;
        if (verbose>=1) debug(">>>> notifySubActivitySpawn(id="+id+") called, srcId="+srcId + " dstId="+dstId);
        
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
        val vsCreatedCell = new Cell[Boolean](false);
        lowLevelFetch(place0, vsCreatedCell, ()=>{ atomic {
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

            vs.transitCount(srcId*MAX_PLACES + dstId)++;
            if (vs.isAdopted()) {
                val adopterVS = vitalStates.getOrThrow(vs.adopterId); // should exist
                adopterVS.transitAdopted(srcId*MAX_PLACES + dstId)++;
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
        if (vsCreatedCell()) {
            assert isAtHome(id);
            (localState as LocalState.Home).hasVitalState.incrementAndGet(); // use AtomicLong to avoid racing
        }
        if (verbose>=1) debug("<<<< notifySubActivitySpawn(id="+id+") returning");
    }
    
    // activity from srcPlace is being created
    def notifyActivityCreation(srcPlace:Place):Boolean {
        val srcId = srcPlace.id, dstId = hereId;
        if (verbose>=1) debug(">>>> notifyActivityCreation(id="+id+") called, srcId="+srcId + " dstId="+dstId);
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
        val isRegisteredCell = new Cell[Boolean](false);
        lowLevelFetch(place0, isRegisteredCell, ()=>{ atomic {
          if (Place.isDead(srcId)) {
            if (verbose>=2) debug("srcPlace is dead, not register the new activity");
            return false;
          } else {
            val isToHome = (dstId==getHomeId(_id));
            val vs = vitalStates.getOrThrow(_id); // should exist
            if (isFirst && !isToHome) vs.remoteCount(dstId)++;
            vs.transitCount(srcId*MAX_PLACES + dstId)--;
            if (vs.isAdopted()) {
                val adopterVS = vitalStates.getOrThrow(vs.adopterId); // should exist
                assert !isToHome; // vs.isAdopted means that home is dead
                if (isFirst) adopterVS.remoteAdopted(dstId)++;
                adopterVS.transitAdopted(srcId*MAX_PLACES + dstId)--;
            }
            if (verbose>=3) vs.dump("DUMP id="+_id);
            if (isToHome) quiescent_check(_id); // to delete VS if necessary
            return true;
          }
        }});
        if (verbose>=1) debug("<<<< notifyActivityCreation(id="+id+") returning "+isRegisteredCell());
        return isRegisteredCell();
    }
    
    // activity run here is being terminated
    def notifyActivityTermination():void {
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
        lowLevelAt(place0, ()=>{ atomic {
            val vs = vitalStates.getOrThrow(_id); // should exist
            vs.remoteCount(dstId)--; quiescent_check(_id);
            if (vs.isAdopted()) {
                val adopterVS = vitalStates.getOrThrow(vs.adopterId); // should exist
                adopterVS.remoteAdopted(dstId)--;
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

        // wait for the latch release
        val ls = (localState as LocalState.Home);
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
            lowLevelAt(Place(getHomeId(id)), ()=>{ atomic { // just ignored if home place is dead
val lsTmp = localStates.getOrElse(_id, null) as LocalState.Home;		//@@@@
if (lsTmp==null) debug("@@@@@@ pushException: id="+_id+" not found, t=" + t);	//@@@@
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
                    for (i in 0..(MAX_PLACES-1)) {
                        vs.remoteAdopted(i) += (dcVS.remoteCount(i) + dcVS.remoteAdopted(i));
                        dcVS.remoteAdopted(i) = 0n;
                        // don't clear dcVS.remoteCount, which will be checked to remove this dcVS
                        for (j in 0..(MAX_PLACES-1)) {
                            val idx = i*MAX_PLACES + j;
                            vs.transitAdopted(idx) += (dcVS.transitCount(idx) + dcVS.transitAdopted(idx));
                            dcVS.transitAdopted(idx) = 0n;
                            // don't clear dcVS.transitCount, which will be checked to remove this dcVS
                        }
                    }
                    // need not copy the deadPlaces info to the adopter
                }
            } // for (dcIdx)
        }
        
        // 2 delete dead entries
        for (i in 0..(MAX_PLACES-1)) {
            if (!Place.isDead(i)) continue;
            if (vs.remoteCount(i) > 0n) {
                if (verbose>=3) debug("adding DPE("+i+") for remoteCount("+i+")");
                vs.deadPlaces.add(i);
            }
            vs.remoteCount(i) = vs.remoteAdopted(i) = 0n;
            for (j in 0..(MAX_PLACES-1)) {
                val idx = i*MAX_PLACES + j;
                vs.transitCount(idx) = vs.transitAdopted(idx) = 0n;
                val idx2 = j*MAX_PLACES + i;
                if (vs.transitCount(idx2) > 0n) {
                    if (verbose>=3) debug("adding DPE("+i+") for transitCount("+j+","+i+")");
                    vs.deadPlaces.add(i);
                }
                vs.transitCount(idx2) = vs.transitAdopted(idx2) = 0n;
            }
        }
        
        // 3 quiescent check
        if (verbose>=3) vs.dump("DUMP id="+id);
        var quiet:Boolean = true;
        for (i in 0..(MAX_PLACES-1)) {
            if (vs.remoteCount(i) > 0n) { quiet = false; break; }
            if (vs.remoteAdopted(i) > 0n) { quiet = false; break; }
            for (j in 0..(MAX_PLACES-1)) {
                val idx = i*MAX_PLACES + j;
                if (vs.transitCount(idx) > 0n) { quiet = false; break; }
                if (vs.transitAdopted(idx) > 0n) { quiet = false; break; }
            }
            if (!quiet) break;
        }
        if (quiet) {
            if (verbose>=2) debug("removing vitalState(id="+id+")");
            vitalStates.remove(id);
                // better to remove id from ancestor(adopter)'s descendantIds, but we don't know it
                // therefore, the descendantIds entry will be cleared at the ancestor's Step1
            val dpes = vs.deadPlaces;
            lowLevelSend(Place(getHomeId(id)), ()=>{ // just ignored if home place is dead
                var ls:LocalState.Home;
                atomic { // should not cause deadlock because this is executed asynchronously by lowLevelSend
                    ls = localStates.getOrThrow(id) as LocalState.Home; // should exist
                    for (placeId in dpes) ls.dpes.add(placeId);
                }
                val hasVS = ls.hasVitalState.decrementAndGet();
                val c = ls.localLive.get();
                if (hasVS == 0n && c <= 0n) {
                    if (verbose>=2) debug("calling latch.release for id="+id);
                    ls.latch.release();
                }
            });
        }
        
        if (verbose>=2) debug("quiescent_check(id="+id+") returning, quiet="+quiet);
    }

    /*
     * Support for faster runAt
     */
    public def runAt(place:Place, body:()=>void, prof:Runtime.Profile):void {
        if (verbose>=1) debug("FinishResilient.runAt called, place.id=" + place.id);
        Runtime.ensureNotInAtomic();
        if (place.id == Runtime.hereLong()) {
            // local path can be the same as before
            Runtime.runAtNonResilient(place, body, prof);
            if (verbose>=1) debug("FinishResilient.runAt returning (locally executed)");
            return;
        }
        
        val real_finish = this;
        //real_finish.notifySubActivitySpawn(place);
        
        //@@val tmp_finish = make(this/*parent*/, null/*latch*/);
        val tmp_finish = make(this/*parent*/, new SimpleLatch());
        // TODO: clockPhases are now passed but their resiliency is not supported yet
        // TODO: This implementation of runAt does not explicitly dealloc things
        val home = here;
        //@@real_finish.notifySubActivitySpawn(place);//@@@@
        tmp_finish.notifySubActivitySpawnWithRealFinish(place, real_finish);
        
        // XTENLANG-3357: clockPhases must be passed and returned
        val myActivity = Runtime.activity();
        val epoch = myActivity.epoch;
        val clockPhases = myActivity.clockPhases;
        val cpCell = new Cell[Activity.ClockPhases](clockPhases);
        val cpGref = GlobalRef(cpCell);
        
        // [DC] do not use at (place) async since the finish state is handled internally
        // [DC] go to the lower level...
        val cl = () => @RemoteInvocation("fiish_resilient_run_at") {
            if (verbose>=2) debug("FinishResilient.runAt closure received");
            val exec_body = () => {
                if (verbose>=2) debug("FinishResilient.runAt exec_body started");
                val remoteActivity = Runtime.activity();
                remoteActivity.clockPhases = clockPhases; // XTENLANG-3357: set passed clockPhases
                //@@real_finish.notifyActivityCreation(home);//@@@@
                if (tmp_finish.notifyActivityCreationWithRealFinish(home, real_finish)) {
                    try {
                        try {
                            body();
                        } catch (e:Runtime.AtCheckedWrapper) {
                            throw e.getCheckedCause();
                        } 
                    } catch (t:CheckedThrowable) {
                        tmp_finish.pushException(t);
                    }
                    // XTENLANG-3357: return the (maybe modified) clockPhases, similar code as "at (cpGref) { cpGref().set(clockPhases); }"
                    // TODO: better to merge this with notifyActivityTermination to reduce send
                    val cl1 = ()=> @RemoteInvocation("finish_resilient_run_at_1") {
                        if (verbose>=2) debug("FinishResilient.runAt setting new clockPhases");
                        cpGref.getLocalOrCopy().set(clockPhases); // this will be set to myActivity.clockPhases
                    };
                    if (verbose>=2) debug("FinishResilient.runAt exec_body sending closure to set clockPhases");
                    Runtime.x10rtSendMessage(cpGref.home.id, cl1, null); // TODO: should use lowLevelAt
                    Unsafe.dealloc(cl1);
                    
                    tmp_finish.notifyActivityTerminationWithRealFinish(real_finish, home);
                    //@@real_finish.notifyActivityTermination();//@@@@
                }
                remoteActivity.clockPhases = null; // XTENLANG-3357
                if (verbose>=2) debug("FinishResilient.runAt exec_body finished");
            };
            if (verbose>=2) debug("FinishResilient.runAt create a new activity to execute");
            Runtime.dealOrPush(new Activity(epoch, exec_body, home, real_finish, false, false));
            // TODO: Unsafe.dealloc(exec_body); needs to be called somewhere
        };
        if (verbose>=2) debug("FinishResilient.runAt sending closure");
        Runtime.x10rtSendMessage(place.id, cl, prof);
        
        try {
            if (verbose>=2) debug("FinishResilient.runAt calling tmp_finish.waitForFinish");
            tmp_finish.waitForFinish();
            if (verbose>=2) debug("FinishResilient.runAt returned from tmp_finish.waitForFinish");
            myActivity.clockPhases = cpCell(); // XTENLANG-3357: set the (maybe modified) clockPhases
        } catch (e:MultipleExceptions) {
            assert e.exceptions.size == 1 : e.exceptions();
            var e2:CheckedThrowable = e.exceptions(0);
            if (verbose>=1) debug("FinishResilient.runAt received exception="+e2);
            if (e2 instanceof WrappedThrowable) {
                e2 = e2.getCheckedCause();
            }
            Runtime.throwCheckedWithoutThrows(e2);
        }
        if (verbose>=1) debug("FinishResilient.runAt returning (remotely executed)");
    }

    def notifySubActivitySpawnWithRealFinish(place:Place, real_finish:FinishResilientPlace0opt):void {
        val srcId = hereId, dstId = place.id;
        if (verbose>=1) debug(">>>> notifySubActivitySpawnWithRealFinish(id="+id+") called, srcId="+srcId + " dstId="+dstId + " real_finish="+real_finish);
        
        assert srcId != dstId;
        assert dstId != getHomeId(id);
        
        if (verbose>=2) debug("spawning to another remote place, need transit++");
        val _id1 = id;
        val acId1 = isAtHome(id) ? (localState as LocalState.Home).ancestorId : FinishID_NULL;
        val _id0 = real_finish.id;
        val acId0 = isAtHome(real_finish.id) ? (real_finish.localState as LocalState.Home).ancestorId : FinishID_NULL;
        val vs0CreatedCell = new Cell[Boolean](false);
        lowLevelFetch(place0, vs0CreatedCell, ()=>{ atomic {
            var vs0Created:Boolean = false;
            var vs0:VitalState = vitalStates.getOrElse(_id0, null);
            if (vs0 == null) { // create a new VitalState
                if (verbose>=2) debug("creating a new VitalState for id0="+_id0+", ancestorId0="+acId0);
                assert srcId == getHomeId(_id0);
                vs0 = new VitalState(); vitalStates.put(_id0, vs0); vs0Created = true;
                if (acId0 == FinishID_NULL) {
                    // this only happens if the ancestor is the top-level finish
                } else {
                    var acVS0:VitalState = vitalStates.getOrThrow(acId0); // should exist
                    if (acVS0.isAdopted()) acVS0 = vitalStates.getOrThrow(acVS0.adopterId); // should exist
                    assert !acVS0.isAdopted();
                    acVS0.descendantIds.add(_id0);
                }
            }
            
            /*@@@@ (kawatiya 2014/09/29)
             * This "transit[dstId,srcId]+=1000" is necessary to:
             * - keep the VS0 while runAt activity is running
             * - remove the VS0 when the dst place is dead, without throwing DPE
             * We use 1000 here to distinguish this special case easily.
             * The count will be decremented in notifyActivityTerminationWithRealFinish or cleared in quiescent_check
             */
            vs0.transitCount(dstId*MAX_PLACES + srcId) += 1000n; //@@@@ vs0.transit[dstId,srcId] += 1000
            if (vs0.isAdopted()) {
                val adopterVS0 = vitalStates.getOrThrow(vs0.adopterId); // should exist
                adopterVS0.transitAdopted(dstId*MAX_PLACES + srcId) += 1000n;
            }
            if (verbose>=3) vs0.dump("DUMP id0="+_id0);
            if (Place.isDead(dstId)) {
                if (verbose>=2) debug("target place is already dead, try quiescent_check(id0)");
                quiescent_check(_id0);
                if (vs0.isAdopted()) quiescent_check(vs0.adopterId);
            }
            
            var vs1Created:Boolean = false;
            var vs1:VitalState = vitalStates.getOrElse(_id1, null);
            if (vs1 == null) { // create a new VitalState
                if (verbose>=2) debug("creating a new VitalState for id1="+_id1+", ancestorId1="+acId1);
                assert srcId == getHomeId(_id1);
                vs1 = new VitalState(); vitalStates.put(_id1, vs1); vs1Created = true;
                if (acId1 == FinishID_NULL) {
                    // this only happens if the ancestor is the top-level finish
                } else {
                    var acVS1:VitalState = vitalStates.getOrThrow(acId1); // should exist
                    if (acVS1.isAdopted()) acVS1 = vitalStates.getOrThrow(acVS1.adopterId); // should exist
                    assert !acVS1.isAdopted();
                    acVS1.descendantIds.add(_id1);
                }
            }
            
            vs1.transitCount(srcId*MAX_PLACES + dstId)++;
            if (vs1.isAdopted()) {
                val adopterVS1 = vitalStates.getOrThrow(vs1.adopterId); // should exist
                adopterVS1.transitAdopted(srcId*MAX_PLACES + dstId)++;
            }
            if (verbose>=3) vs1.dump("DUMP id1="+_id1);
            if (Place.isDead(dstId)) {
                if (verbose>=2) debug("target place is already dead, try quiescent_check(id1)");
                quiescent_check(_id1);
                if (vs1.isAdopted()) quiescent_check(vs1.adopterId);
            }
            
            assert vs1Created == true;
            return vs0Created;
        }});
        if (true) {
            assert isAtHome(id);
            (localState as LocalState.Home).hasVitalState.incrementAndGet(); // use AtomicLong to avoid racing
        }
        if (vs0CreatedCell()) {
            assert isAtHome(real_finish.id);
            (real_finish.localState as LocalState.Home).hasVitalState.incrementAndGet(); // use AtomicLong to avoid racing
        }
        if (verbose>=1) debug("<<<< notifySubActivitySpawnWithRealFinish(id="+id+") returning");
    }

    def notifyActivityCreationWithRealFinish(srcPlace:Place, real_finish:FinishResilientPlace0opt):Boolean {
        //@@@@ TODO: now this code should be same as original notifyActivityCreation?
        val srcId = srcPlace.id, dstId = hereId;
        if (verbose>=1) debug(">>>> notifyActivityCreationWithRealFinish(id="+id+") called, srcId="+srcId + " dstId="+dstId + " real_finish="+real_finish);
        if (Place.isDead(srcId)) {
            if (verbose>=1) debug("<<<< notifyActivityCreationWithRealFinish(id="+id+") returning false, srcPlace is dead");
            return false;
        }
        
        val c = localState.localLive.getAndIncrement(); // localLive++
        
        assert srcId != dstId;
        assert dstId != getHomeId(id);
        
        val _id1 = id;
        val isFirst1 = (c==0n); // activity is newly created here (remote place)
        val isRegistered1Cell = new Cell[Boolean](false);
        //@@@@ val _id0 = real_finish.id;
        //@@@@ val isFirst0 = true;
        //@@@@ val isRegistered0Cell = new Cell[Boolean](false);
        lowLevelFetch(place0, isRegistered1Cell, ()=>{ atomic {
            //@@@@ keep the vs0 status in transit[dstId,srcId]
            
          if (Place.isDead(srcId)) {
            if (verbose>=2) debug("srcPlace is dead, not register the new activity");
            return false;
          } else {
            //@@@@ val isToHome1 = (dstId==getHomeId(_id1)); // should be false
            val vs1 = vitalStates.getOrThrow(_id1); // should exist
            if (isFirst1) vs1.remoteCount(dstId)++;
            vs1.transitCount(srcId*MAX_PLACES + dstId)--;
            if (vs1.isAdopted()) {
                val adopterVS1 = vitalStates.getOrThrow(vs1.adopterId); // should exist
                if (isFirst1) adopterVS1.remoteAdopted(dstId)++;
                adopterVS1.transitAdopted(srcId*MAX_PLACES + dstId)--;
            }
            if (verbose>=3) vs1.dump("DUMP id1="+_id1);
            //@@@@ if (isToHome1) quiescent_check(_id1); // to delete VS if necessary
            return true;
          }
        }});
        if (verbose>=1) debug("<<<< notifyActivityCreationWithRealFinish(id="+id+") returning "+isRegistered1Cell());
        return isRegistered1Cell();
    }

    def notifyActivityTerminationWithRealFinish(real_finish:FinishResilientPlace0opt, srcPlace:Place):void {
        val dstId = hereId;
        val srcId = srcPlace.id;//@@@@
        if (verbose>=1) debug(">>>> notifyActivityTerminationWithRealFinish(id="+id+") called, dstId="+dstId + " real_finish="+real_finish + " srcId="+srcId);
        
        val c = localState.localLive.decrementAndGet(); // localLive--
        assert c == 0n;
        assert !isAtHome(id);
        
        // localLive==0 && remote -> localStates.remove(id); VS.remote--; quiescent_check
        atomic { localStates.remove(id); }
        val _id1 = id;
        val _id0 = real_finish.id;
        lowLevelAt(place0, ()=>{ atomic {
            val vs1 = vitalStates.getOrThrow(_id1); // should exist
            vs1.remoteCount(dstId)--; quiescent_check(_id1);
            if (vs1.isAdopted()) {
                val adopterVS1 = vitalStates.getOrThrow(vs1.adopterId); // should exist
                adopterVS1.remoteAdopted(dstId)--;
                quiescent_check(vs1.adopterId);
            }
            
            val vs0 = vitalStates.getOrThrow(_id0); // should exist
            //vs0.remoteCount(getHomeId(_id0))--; quiescent_check(_id0);
            //if (vs0.isAdopted()) {
            //    val adopterVS0 = vitalStates.getOrThrow(vs0.adopterId); // should exist
            //    adopterVS0.remoteAdopted(getHomeId(_id0))--;
            //    quiescent_check(vs0.adopterId);
            //}
            vs0.transitCount(dstId*MAX_PLACES + srcId) -= 1000n; //@@@@ vs0.transit[dstId,srcId] -= 1000
            quiescent_check(_id0);
            if (vs0.isAdopted()) {
                val adopterVS0 = vitalStates.getOrThrow(vs0.adopterId); // should exist
                adopterVS0.transitAdopted(dstId*MAX_PLACES + srcId) -= 1000n;
                quiescent_check(vs0.adopterId);
            }
        }});
        if (verbose>=1) debug("<<<< notifyActivityTerminationWithRealFinish(id="+id+") returning");
    }
}

