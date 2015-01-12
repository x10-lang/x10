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
package x10.lang.managed;

import x10.util.concurrent.SimpleLatch;
import x10.util.GrowableRail;
import x10.util.ArrayList;
import x10.array.Array_3;

import com.hazelcast.core.IMap;
import com.hazelcast.map.AbstractEntryProcessor;
import com.hazelcast.core.EntryListener;
import java.util.Map;

/*
 * Resilient Finish optimized for Hazelcast
 * This file is compiled only for Managed X10, so we can use Java classes here
 */
public
class FinishResilientHC extends FinishResilientBridge {
    private static val verbose = FinishResilientBridge.verbose;
    private static val hereId = Runtime.hereLong();
    private static val MAX_PLACES = Place.numPlaces(); // TODO: remove the MAX_PLACES dependency to support elastic X10

    private static val AT = 0;
    private static val ASYNC = 1;
    private static val AT_AND_ASYNC = AT..ASYNC;
    
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
        
        private static val nextId = new x10.util.concurrent.AtomicLong();
        static def getNewId() = FinishID(hereId, nextId.getAndIncrement()); //TODO: overflow check?
    }
    
    private static class State(parentId:FinishID) { // data stored into Hazelcast IMap
        var nonzero:Long = 0; // number of non-zero entries in counts array
        val counts = new Array_3[Int](MAX_PLACES, MAX_PLACES, 2); //TODO: make this dynamic
        val liveChIds = new ArrayList[FinishID](); // FinishIDs of live children
        val deadChIds = new ArrayList[FinishID](); // FinishIDs of dead children
        val deadPlIds = new ArrayList[Long]();     // Place IDs that have died during the finish
        val exceptions = new GrowableRail[CheckedThrowable](); // exceptions to report
        def dump(msg:Any) {
            var nz:Long = 0;
            val s = new x10.util.StringBuilder(); s.add(msg); s.add('\n');
            s.add("  parentId="+parentId+" #non0="+nonzero+" #excs="+exceptions.size()+"\n");
            s.add("    counts: "); for (v in counts)  { s.add(" " + v); if(v!=0n)nz++; } s.add('\n');
            s.add(" liveChIds: "); for (v in liveChIds) s.add(" " + v); s.add('\n');
            s.add(" deadChIds: "); for (v in deadChIds) s.add(" " + v); s.add('\n');
            s.add(" deadPlIds: "); for (v in deadPlIds) s.add(" " + v);
            debug(s.toString());
            if (nz != nonzero) debug("ERROR: incorrect nonzero count");
        }
    }
    
    /*
     * Processing using executeOnKey
     * The logic here is based on apgas.impl/src/apgas/impl/ResilientFinish.java
     */
    private static def propagate(id:FinishID, entryProcessor:AbstractEntryProcessor/*[FinishID,State]*/):void {
        if (verbose>=2) debug("propagate(id="+id+") called");
        try {
            val parentId = imap.executeOnKey(id, entryProcessor) as FinishID;
            if (verbose>=2) debug("propagate(id="+id+") executed, returned parentId="+parentId);
            if (parentId != FinishID.NULL) { // means that id is quiescent and parentId should be processed
                propagate(parentId, new AbstractEntryProcessor/*[FinishID,State]*/() {
                    public def process(entry:Map.Entry/*[FinishID,State]*/) {
                        val parentState = entry.getValue() as State;
                        if (parentState == null) return FinishID.NULL; // parent has been purged already, stop the propagation
                        if (parentState.liveChIds.contains(id)) {
                            parentState.liveChIds.remove(id);
                        } else {
                            if (!parentState.deadChIds.contains(id)) parentState.deadChIds.add(id);
                        }
                        if (verbose>=3) parentState.dump("DUMP parentId="+parentId);
                        entry.setValue(filter(parentId, parentState));
                        return next(parentState);
                    }
                });
            }
        } catch (e:HereIsDeadError) {
            debug("Caught HereIsDeadError for id="+id+", this place "+hereId+" is dead for the world");
            //@@ System.exit(42); // this place is dead for the world
        }
        if (verbose>=2) debug("propagate(id="+id+") returning");
    }
    // check if the state needs to be still preserved
    private static def filter(id:FinishID, state:State):State {
        if (state.nonzero > 0 || !state.liveChIds.isEmpty() || !state.deadPlIds.contains(id.placeId)) {
           return state; // state is still useful, finish in incomplete or we need to preserve its exceptions
        } else {
           return null;  // finish is complete and place of finish has died, remove entry
        }
    }
    // check if the state is quiescent, and return its parentId for next processing
    private static def next(state:State):FinishID {
        if (state.nonzero > 0 || !state.liveChIds.isEmpty()) {
            return FinishID.NULL;  // not quiescent yet
        } else {
            return state.parentId; // quiescent, return parentId
        }
    }
    private static class HereIsDeadError extends Error { }
    private static class PeerIsDeadException extends Exception { }
    
    // fields of this FinishState
    private val id:FinishID; // should be global
    private transient val latch:SimpleLatch; // latch is stored only in the original local finish
    private var hasRemote:Boolean = false;
    
    public def toString():String = System.identityToString(this) + "(id="+id+")";
    
    private def this(id:FinishID, latch:SimpleLatch) { this.id = id; this.latch = latch; }
    public
    static def make(parent:Any, latch:SimpleLatch):FinishResilientHC { // FinishState is inaccessible from another package ...
        if (verbose>=1) debug(">>>> FinishResilientHC.make called, parent="+parent + " latch="+latch);
        val parentId = (parent instanceof FinishResilientHC) ? (parent as FinishResilientHC).id : FinishID.NULL; // ok to ignore other cases?
        
        // create FinishState
        val id = FinishID.getNewId();
        val fs = new FinishResilientHC(id, latch);
        
        // create State in Hazelcast IMap
        val state = new State(parentId);
        state.counts(hereId, hereId, ASYNC) = 1n; // for myself, will be decremented in waitForFinish
        state.nonzero = 1;
        if (verbose>=3) state.dump("DUMP id="+id);
        val prev = imap.put(id, state); assert prev==null;
        
      if (parentId != FinishID.NULL) {
        // parentState.liveChIds.add(id);
        propagate(parentId, new AbstractEntryProcessor/*[FinishID,State]*/() {
            public def process(entry:Map.Entry/*[FinishID,State]*/) {
                val parentState = entry.getValue() as State;
                if (parentState == null || parentState.deadPlIds.contains(hereId)) throw new HereIsDeadError(); // parent finish thinks this place is dead, exit
                if (!parentState.deadChIds.contains(id)) parentState.liveChIds.add(id);
                if (verbose>=3) parentState.dump("DUMP parentId="+parentId);
                entry.setValue(parentState);
                return FinishID.NULL; // no need to propagate
            }
        });
      }
        
        if (verbose>=1) debug("<<<< FinishResilientHC.make returning fs="+fs);
        return fs;
    }
    
    public
    static def notifyPlaceDeath():void {
        if (verbose>=1) debug(">>>> notifyPlaceDeath called");
        if (hereId != 0) { //TODO: remove the place0 dependency
            if (verbose>=1) debug("<<<< notifyPlaceDeath returning, not at Place 0");
            return;
        }
        
      for (_id in imap.keySet()) { // process all states
        val id = _id as FinishID;
        propagate(id, new AbstractEntryProcessor/*[FinishID,State]*/() {
            public def process(entry:Map.Entry/*[FinishID,State]*/) {
                val state = entry.getValue() as State;
                if (state == null) return FinishID.NULL; // entry has been removed already, ignore
                var newDead:Long = 0;
              for (var p:Long = 0; p < MAX_PLACES; p++) {
                if (!Place.isDead(p)) continue;
                if (state.deadPlIds.contains(p)) continue; // death of this place has already been processed
                newDead++; handleNewPlaceDeath(state, p);
              }
              if (newDead == 0) return FinishID.NULL; // nothing changed for this state
              if (verbose>=3) state.dump("DUMP id="+id);
              entry.setValue(filter(id, state));
              return next(state);
            }
        });
      } // for (id)
        
        if (verbose>=2) debug("<<<< notifyPlaceDeath released locks and returning");
    }
    private static def handleNewPlaceDeath(state:State, placeId:Long) { // should be called from AbstractEntryProcessor.process
        assert !state.deadPlIds.contains(placeId);
        state.deadPlIds.add(placeId); 
        for (var i:Long = 0; i < MAX_PLACES; i++) {
            if (state.counts(i, placeId, ASYNC) != 0n) {
                addDeadPlaceException(state, placeId); //@@ should add n times?
                state.counts(i, placeId, ASYNC) = 0n; --state.nonzero;
            }
            if (state.counts(i, placeId, AT) != 0n) {
                state.counts(i, placeId, AT) = 0n; --state.nonzero;
            }
            if (state.counts(placeId, i, ASYNC) != 0n) {
                state.counts(placeId, i, ASYNC) = 0n; --state.nonzero;
            }
            if (state.counts(placeId, i, AT) != 0n) {
                state.counts(placeId, i, AT) = 0n; --state.nonzero;
            }
        }
    }
    private static def addDeadPlaceException(state:State, placeId:Long) {
        val e = new DeadPlaceException(Place(placeId));
        e.fillInStackTrace(); // meaningless?
        state.exceptions.add(e);
    }
    
    public
    def notifySubActivitySpawn(dstPlace:Place):void {
        notifySubActivitySpawn(dstPlace, ASYNC);
    }
    public
    def notifyShiftedActivitySpawn(dstPlace:Place):void {
        notifySubActivitySpawn(dstPlace, AT);
    }
    public
    def notifySubActivitySpawn(dstPlace:Place, kind:long):void {
        val srcId = hereId, dstId = dstPlace.id;
        if (dstId != srcId) hasRemote = true;

        if (verbose>=1) debug(">>>> notifySubActivitySpawn(id="+id+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);
        
        // counts[srcId,dstId]++;
        propagate(id, new AbstractEntryProcessor/*[FinishID,State]*/() {
            public def process(entry:Map.Entry/*[FinishID,State]*/) {
                val state = entry.getValue() as State;
                if (state == null || state.deadPlIds.contains(hereId)) throw new HereIsDeadError(); // finish thinks this place is dead, exit
                if (Place.isDead(dstId) || state.deadPlIds.contains(dstId)) { // destination place has died
                    // should not do ++counts, because following notifyActivityCreation/Termination will not be executed
                    if (verbose>=2) debug("destination place has died, just adding DPE");
                    if (!state.deadPlIds.contains(dstId)) handleNewPlaceDeath(state, dstId);
                    if (kind == ASYNC) addDeadPlaceException(state, dstId); 
                } else {
                    val c = ++state.counts(srcId, dstId, kind);
                    if (c == 1n) ++state.nonzero; else if (c == 0n) --state.nonzero;
                }
                if (verbose>=3) state.dump("DUMP id="+id);
                entry.setValue(filter(id, state));
                return next(state);
            }
        });
        
        if (verbose>=1) debug("<<<< notifySubActivitySpawn(id="+id+") returning");
    }
    
    public
    def notifyActivityCreation(srcPlace:Place, activity:Activity):Boolean {
        return notifyActivityCreation(srcPlace, activity, ASYNC);
    }

    public
    def notifyActivityCreation(srcPlace:Place, activity:Activity, kind:long):Boolean {
        val srcId = srcPlace.id, dstId = hereId;
        if (verbose>=1) debug(">>>> notifyActivityCreation(id="+id+") called, srcId="+srcId + " dstId="+dstId+" kind="+kind);
        
      try {
        // counts[dstId,dstId]++; counts[srcId,dstId]--;
        propagate(id, new AbstractEntryProcessor/*[FinishID,State]*/() {
            public def process(entry:Map.Entry/*[FinishID,State]*/) {
                val state = entry.getValue() as State;
                if (state == null || state.deadPlIds.contains(hereId)) throw new HereIsDeadError(); // finish thinks this place is dead, exit
                if (Place.isDead(srcId) || state.deadPlIds.contains(srcId)) { // source place has died
                    if (verbose>=2) debug("source place has died");
                    if (!state.deadPlIds.contains(srcId)) { handleNewPlaceDeath(state, srcId); entry.setValue(filter(id, state)); }
                    throw new PeerIsDeadException(); // throw exception to return false (no need to propagate)
                }
                val c1 = ++state.counts(dstId, dstId, kind);
                if (c1 == 1n) ++state.nonzero; else if (c1 == 0n) --state.nonzero;
                val c2 = --state.counts(srcId, dstId, kind);
                if (c2 == 0n) --state.nonzero; else if (c2 ==-1n) ++state.nonzero;
                if (verbose>=3) state.dump("DUMP id="+id);
                entry.setValue(filter(id, state));
                return next(state);
            }
        });
      } catch (e:PeerIsDeadException) {
        if (verbose>=1) debug("<<<< notifyActivityCreation(id="+id+") returning false (src is dead)");
        return false;
      }
        
        if (verbose>=1) debug("<<<< notifyActivityCreation(id="+id+") returning true");
        return true;
    }
    
    public
    def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity):Boolean {
        return notifyActivityCreation(srcPlace, activity, ASYNC);
    }

    public
    def notifyShiftedActivityCreation(srcPlace:Place, activity:Activity):Boolean {
        return notifyActivityCreation(srcPlace, activity, AT);
    }

    public 
    def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void { 
        notifyActivityCreation(srcPlace, null, ASYNC);
        pushException(t);
        notifyActivityTermination(ASYNC);
    }

    public
    def notifyActivityTermination():void {
        notifyActivityTermination(ASYNC);
    }

    public
    def notifyShiftedActivityCompletion():void {
        notifyActivityTermination(AT);
    }

    public
    def notifyActivityTermination(kind:long):void {
        val dstId = hereId;
        if (verbose>=1) debug(">>>> notifyActivityTermination(id="+id+") called, dstId="+dstId+" kind="+kind);
        
        // counts[dstId,dstId]--;
        propagate(id, new AbstractEntryProcessor/*[FinishID,State]*/() {
            public def process(entry:Map.Entry/*[FinishID,State]*/) {
                val state = entry.getValue() as State;
                if (state == null || state.deadPlIds.contains(hereId)) throw new HereIsDeadError(); // finish thinks this place is dead, exit
                val c = --state.counts(dstId, dstId, kind);
                if (c == 0n) --state.nonzero; else if (c ==-1n) ++state.nonzero;
                if (verbose>=3) state.dump("DUMP id="+id);
                entry.setValue(filter(id, state));
                return next(state);
            }
        });
        if (verbose>=1) debug("<<<< notifyActivityTermination(id="+id+") returning");
    }
    
    public
    def pushException(t:CheckedThrowable):void {
        if (verbose>=1) debug(">>>> pushException(id="+id+") called, t="+t);
        
        // exceptions.add(t);
        propagate(id, new AbstractEntryProcessor/*[FinishID,State]*/() {
            public def process(entry:Map.Entry/*[FinishID,State]*/) {
                val state = entry.getValue() as State;
                if (state == null || state.deadPlIds.contains(hereId)) throw new HereIsDeadError(); // finish thinks this place is dead, exit
                state.exceptions.add(t);
                if (verbose>=3) state.dump("DUMP id"+id);
                entry.setValue(state);
                return FinishID.NULL; // no need to propagate
            }
        });
        if (verbose>=1) debug("<<<< pushException(id="+id+") returning");
    }
    
    public
    def waitForFinish():void { // can be called only for the original local FinishState returned by make
        if (verbose>=1) debug(">>>> waitForFinish(id="+id+") called");
        assert id.placeId==hereId;
        assert latch!=null; // original local FinishState
        
        // add listener to this id
        val reg = imap.addEntryListener(new EntryListener/*[FinishID,State]*/() {
            public def entryAdded  (event:com.hazelcast.core.EntryEvent/*[FinishID,State]*/) { }
            public def entryRemoved(event:com.hazelcast.core.EntryEvent/*[FinishID,State]*/) { checkStatus(event.getValue() as State); }
            public def entryUpdated(event:com.hazelcast.core.EntryEvent/*[FinishID,State]*/) { checkStatus(event.getValue() as State); }
            public def entryEvicted(event:com.hazelcast.core.EntryEvent/*[FinishID,State]*/) { }
            public def mapEvicted(event:com.hazelcast.core.MapEvent) { }
            public def mapCleared(event:com.hazelcast.core.MapEvent) { }
        }, id, true/* include value in EntryEvent */);
        
        notifyActivityTermination(ASYNC); // terminate myself, this should trigger entryUpdated listener added above
        
        // If we haven't gone remote with this finish yet, see if this worker
        // can execute other asyncs that are governed by the finish before waiting on the latch.
        if ((!Runtime.STRICT_FINISH) && (Runtime.STATIC_THREADS || !hasRemote)) {
            if (verbose>=2) debug("calling worker.join for id="+id);
            joinFinish(latch);
        }

        if (verbose>=2) debug("calling latch.await for id="+id);
        latch.await(); // wait for the termination (latch may already be released)
        if (verbose>=2) debug("returned from latch.await for id="+id);
        
        imap.removeEntryListener(reg);
        val state = imap.remove(id) as State;
        val e = MultipleExceptions.make(state.exceptions); // may return null        
        
        if (verbose>=1) debug("<<<< waitForFinish(id="+id+") returning, exc="+e);
        if (e != null) throw e;
    }
    // release latch if quiescent
    private def checkStatus(state:State) {
        if (verbose>=2) debug("checkStatus(id="+id+") called");
        assert id.placeId==hereId;
        assert latch!=null; // original local FinishState
        if (verbose>=3) state.dump("DUMP id="+id);
        if (state.nonzero > 0 || !state.liveChIds.isEmpty()) {
            if (verbose>=2) debug("checkStatus(id="+id+") returning, state is still alive");
            return; // state is still alive
        }
        if (verbose>=3) debug("calling latch.release for id="+id);
        latch.release(); // latch.await is in waitForFinish
        if (verbose>=2) debug("checkStatus(id="+id+") returning, latch released");
    }
}
