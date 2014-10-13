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
package x10.lang.managed;
import x10.util.concurrent.SimpleLatch;
import x10.util.GrowableRail;

import com.hazelcast.core.IMap;
//import com.hazelcast.core.EntryEvent;
//import com.hazelcast.core.EntryListener;
//import com.hazelcast.core.MapEvent;
//import com.hazelcast.map.AbstractEntryProcessor;

/*
 * Resilient Finish optimized for Hazelcast
 * This file is compiled only for Managed X10, so we can use Java classes here
 */
public
class FinishResilientHC extends FinishResilientBridge {
    private static val useX10RTforInit = true; // turn off this to use my own initialization code
    private static val imap:IMap = getIMap("FinishResilientHC");
    
    private static def getIMap(mapName:String):IMap {
        if (verbose>=1) debug(">>>> FinishResilientHC.getIMap(mapName="+mapName+") called, useX10RTforInit="+useX10RTforInit);
        var imap:IMap = null;
        
      if (useX10RTforInit) {
        @x10.compiler.Native("java", "imap = x10.x10rt.X10RT.getResilientMap(mapName);"){ /* dummy block */ }
        
      } else { // initialize by myself //@@@@ TODO: NOT COMPLETED
        val config = new com.hazelcast.config.Config();
        config.setProperty("hazelcast.logging.type", "none"); // disables Hazelcast logging
        
        // The following code is copied from x10.runtime/src-java/x10/x10rt/HazelcastDatastore.java
        val leader:String = null; //@@@@ TODO: appropriate value should be specified here
        val launcherProvidedHostname = System.getenv("X10_LAUNCHER_HOST");
        if (verbose>=1) debug("launcherProvidedHostName="+launcherProvidedHostname+" leader="+leader);
        var netconfig:com.hazelcast.config.NetworkConfig = config.getNetworkConfig();
        if (launcherProvidedHostname != null) { // override the network interfaces used to match the hostfile/hostlist
            try {
                val IP = java.net.InetAddress.getByName(launcherProvidedHostname).getHostAddress();
                netconfig = netconfig.setInterfaces(new com.hazelcast.config.InterfacesConfig().addInterface(IP).setEnabled(true));
            } catch (e:CheckedException) { // java.net.UnknownHostException
                // InetAddress.getByName() failed.  address not usable.  Let hazelcast pick one instead
                if (verbose>=1) debug("launcherProvidedHostName not resolved");
            }
        }
        val join = netconfig.setPortAutoIncrement(true).getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().setEnabled(true).setRequiredMember(leader);
        
        val hazelcast = com.hazelcast.core.Hazelcast.newHazelcastInstance(config);
        imap = hazelcast.getMap(mapName);
      }
        
        if (imap==null) {
            Console.OUT.println("FinishResilientHC.getIMap: imap is null, you may need to specify -DX10RT_DATASTORE=Hazelcast");
        }
        if (verbose>=1) debug("<<<< FinishResilientHC.getIMap(mapName="+mapName+") returning, imap="+imap);
        return imap;
    }
    
    private static struct FinishID(placeId:Long,localId:Long) { // unique id
        public static val NULL = FinishID(-1,-1);
        public def toString():String = "[" + placeId + "," + localId + "]";
        // equals need not be overridden 
    }
    
    private static class State { // data stored into ResilientStore
        val transit = new Rail[Int](Place.numPlaces() * Place.numPlaces(), 0n);
        val transitAdopted = new Rail[Int](Place.numPlaces() * Place.numPlaces(), 0n);
        val live = new Rail[Int](Place.numPlaces(), 0n);
        val liveAdopted = new Rail[Int](Place.numPlaces(), 0n);
        val excs = new GrowableRail[CheckedThrowable](); // exceptions to report
        val children = new GrowableRail[FinishID](); // children
        var adopterId:FinishID = FinishID.NULL; // adopter (if adopted)
        def isAdopted() = (adopterId != FinishID.NULL);
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
    
    // all active finishes in this place
    private static val ALL = new GrowableRail[FinishResilientHC](); //TODO: reuse localIds
    
    // fields of this FinishState
    private val id:FinishID; // should be global
    private transient val latch:SimpleLatch; // latch is stored only in the original local finish
    
    public def toString():String = System.identityToString(this) + "(id="+id+")";
    
    private def this(id:FinishID, latch:SimpleLatch) { this.id = id; this.latch = latch; }
    public
    static def make(parent:Any, latch:SimpleLatch):FinishResilientHC { // FinishState is inaccessible from another package ...
        if (verbose>=1) debug(">>>> FinishResilientHC.make called, parent="+parent + " latch="+latch);
        val parentId = (parent instanceof FinishResilientHC) ? (parent as FinishResilientHC).id : FinishID.NULL; // ok to ignore other cases?
        
        // create FinishState
        var id:FinishID, fs:FinishResilientHC;
       atomic {
        val placeId = here.id, localId = ALL.size();
        id = FinishID(placeId, localId);
        fs = new FinishResilientHC(id, latch);
        ALL.add(fs); // will be used in notifyPlaceDeath, and removed in waitForFinish
       }
        assert ALL(fs.id.localId)==fs;
        
        // create State in ResilientStore
        val state = new State();
        state.live(here.id) = 1n; // for myself, will be decremented in waitForFinish
       imap.lock(FinishID.NULL);
        imap.put(id, state); // create
        if (parentId != FinishID.NULL) {
            val parentState = imap.get(parentId) as State;
            parentState.children.add(id);
            imap.put(parentId, parentState);
        }
       imap.unlock(FinishID.NULL);
        
        if (verbose>=1) debug("<<<< FinishResilientHC.make returning fs="+fs);
        return fs;
    }
    
    public
    static def notifyPlaceDeath():void {
        if (verbose>=1) debug(">>>> notifyPlaceDeath called");
        if (verbose>=2) debug("notifyPlaceDeath acquiring locks");
       imap.lock(FinishID.NULL);
       atomic {
        if (verbose>=2) debug("notifyPlaceDeath acquired locks, processing local fs");
        for (localId in 0..(ALL.size()-1)) {
            val fs = ALL(localId);
            if (verbose>=2) debug("notifyPlaceDeath checking localId=" + localId + " fs=" + fs);
            if (fs == null) continue;
            if (quiescent(fs.id)) releaseLatch(fs.id);
        }
       }
       imap.unlock(FinishID.NULL);
        if (verbose>=2) debug("<<<< notifyPlaceDeath released locks and returning");
    }
    
    private static def releaseLatch(id:FinishID) { // can be called from any place
        if (verbose>=2) debug("releaseLatch(id="+id+") called");
        lowLevelSend(Place(id.placeId), ()=>{
            val fs = ALL(id.localId); // get the original local FinishState
            if (verbose>=2) debug("calling latch.release for id="+id);
            fs.latch.release(); // latch.await is in waitForFinish
        });
        if (verbose>=2) debug("releaseLatch(id="+id+") returning");
    }
    
    private def getCurrentAdopterId():FinishID {
        assert imap.isLocked(FinishID.NULL);
        var currentId:FinishID = id;
        while (true) {
            assert currentId!=FinishID.NULL;
            val state = imap.get(currentId) as State;
            if (!state.isAdopted()) break;
            currentId = state.adopterId;
        }
        return currentId;
    }
    
    public
    def notifySubActivitySpawn(place:Place):void {
        val srcId = here.id, dstId = place.id;
        if (verbose>=1) debug(">>>> notifySubActivitySpawn(id="+id+") called, srcId="+srcId + " dstId="+dstId);
       imap.lock(FinishID.NULL);
        val state = imap.get(id) as State;
        if (!state.isAdopted()) {
            state.transit(srcId*Place.numPlaces() + dstId)++;
            imap.put(id, state);
        } else {
            val adopterId = getCurrentAdopterId();
            val adopterState = imap.get(adopterId) as State;
            adopterState.transitAdopted(srcId*Place.numPlaces() + dstId)++;
            imap.put(adopterId, adopterState);
        }
        if (verbose>=3) state.dump("DUMP id="+id);
       imap.unlock(FinishID.NULL);
        if (verbose>=1) debug("<<<< notifySubActivitySpawn(id="+id+") returning");
    }
    
    public
    def notifyActivityCreation(srcPlace:Place):Boolean {
        val srcId = srcPlace.id, dstId = here.id;
        if (verbose>=1) debug(">>>> notifyActivityCreation(id="+id+") called, srcId="+srcId + " dstId="+dstId);
        if (srcPlace.isDead()) {
            if (verbose>=1) debug("<<<< notifyActivityCreation(id="+id+") returning false");
            return false;
        }
       imap.lock(FinishID.NULL);
        val state = imap.get(id) as State;
        if (!state.isAdopted()) {
            state.live(dstId)++;
            state.transit(srcId*Place.numPlaces() + dstId)--;
            imap.put(id, state);
        } else {
            val adopterId = getCurrentAdopterId();
            val adopterState = imap.get(adopterId) as State;
            adopterState.liveAdopted(dstId)++;
            adopterState.transitAdopted(srcId*Place.numPlaces() + dstId)--;
            imap.put(adopterId, adopterState);
        }
        if (verbose>=3) state.dump("DUMP id="+id);
       imap.unlock(FinishID.NULL);
        if (verbose>=1) debug("<<<< notifyActivityCreation(id="+id+") returning true");
        return true;
    }
    
    public
    def notifyActivityTermination():void {
        val dstId = here.id;
        if (verbose>=1) debug(">>>> notifyActivityTermination(id="+id+") called, dstId="+dstId);
       imap.lock(FinishID.NULL);
        val state = imap.get(id) as State;
        if (!state.isAdopted()) {
            state.live(dstId)--;
            imap.put(id, state);
            if (quiescent(id)) releaseLatch(id);
        } else {
            val adopterId = getCurrentAdopterId();
            val adopterState = imap.get(adopterId) as State;
            adopterState.liveAdopted(dstId)--;
            imap.put(adopterId, adopterState);
            if (quiescent(adopterId)) releaseLatch(adopterId);
        }
       imap.unlock(FinishID.NULL);
        if (verbose>=1) debug("<<<< notifyActivityTermination(id="+id+") returning");
    }
    
    public
    def pushException(t:CheckedThrowable):void {
        if (verbose>=1) debug(">>>> pushException(id="+id+") called, t="+t);
       imap.lock(FinishID.NULL);
        val state = imap.get(id) as State;
        state.excs.add(t); // need not consider the adopter
        imap.put(id, state);
       imap.unlock(FinishID.NULL);
        if (verbose>=1) debug("<<<< pushException(id="+id+") returning");
    }
    
    public
    def waitForFinish():void { // can be called only for the original local FinishState returned by make
        assert id.placeId==here.id;
        assert latch!=null; // original local FinishState
        if (verbose>=1) debug(">>>> waitForFinish(id="+id+") called");
        
        notifyActivityTermination(); // terminate myself
        if (verbose>=2) debug("calling latch.await for id="+id);
        latch.await(); // wait for the termination (latch may already be released)
        if (verbose>=2) debug("returned from latch.await for id="+id);
        
        var e:MultipleExceptions = null;
       imap.lock(FinishID.NULL);
        val state = imap.get(id) as State;
        if (!state.isAdopted()) {
            e = MultipleExceptions.make(state.excs); // may return null
            imap.remove(id);
        } else {
            //TODO: need to remove the state in future
        }
        atomic { ALL(id.localId) = null; }
       imap.unlock(FinishID.NULL);
        if (verbose>=1) debug("<<<< waitForFinish(id="+id+") returning, exc="+e);
        if (e != null) throw e;
    }
    
    private static def quiescent(id:FinishID):Boolean {
        if (verbose>=2) debug("quiescent(id="+id+") called");
        assert imap.isLocked(FinishID.NULL);
        val state = imap.get(id) as State;
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
                val childState = imap.get(childId) as State;
                if (childState==null) continue; // already finished
                val lastChildId = children.removeLast();
                if (chIndex < children.size()) children(chIndex) = lastChildId;
                chIndex--; // don't advance this iteration
                // adopt the child
                if (verbose>=3) debug("adopting childId="+childId);
                if (verbose>=3) childState.dump("DUMP childId="+childId);
                assert !childState.isAdopted();
                childState.adopterId = id;
                imap.put(childId, childState);
                state.children.addAll(childState.children); // will be checked in the following iteration
                for (i in 0..(Place.numPlaces()-1)) {
                    state.liveAdopted(i) += (childState.live(i) + childState.liveAdopted(i));
                    for (j in 0..(Place.numPlaces()-1)) {
                        val idx = i*Place.numPlaces() + j;
                        state.transitAdopted(idx) += (childState.transit(idx) + childState.transitAdopted(idx));
                    }
                }
            } // for (chIndex)
        }
        // 2 delete dead entries
        for (i in 0..(Place.numPlaces()-1)) {
            if (Place.isDead(i)) {
                for (unused in 1..state.live(i)) {
                    if (verbose>=3) debug("adding DPE for live("+i+")");
                    addDeadPlaceException(state, i);
                }
                state.live(i) = 0n; state.liveAdopted(i) = 0n;
                for (j in 0..(Place.numPlaces()-1)) {
                    val idx = i*Place.numPlaces() + j;
                    state.transit(idx) = 0n; state.transitAdopted(idx) = 0n;
                    val idx2 = j*Place.numPlaces() + i;
                    for (unused in 1..state.transit(idx2)) {
                        if (verbose>=3) debug("adding DPE for transit("+j+","+i+")");
                        addDeadPlaceException(state, i);
                    }
                    state.transit(idx2) = 0n; state.transitAdopted(idx2) = 0n;
                }
            }
        }
        
        imap.put(id, state);
        
        // 3 quiescent check
        if (verbose>=3) state.dump("DUMP id="+id);
        var quiet:Boolean = true;
        for (i in 0..(Place.numPlaces()-1)) {
            if (state.live(i) > 0) { quiet = false; break; }
            if (state.liveAdopted(i) > 0) { quiet = false; break; }
            for (j in 0..(Place.numPlaces()-1)) {
                val idx = i*Place.numPlaces() + j;
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
