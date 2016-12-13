/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */
package x10.xrx;

import x10.compiler.AsyncClosure;
import x10.compiler.Immediate;
import x10.compiler.Inline;

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

    private static val AT = 0n;
    private static val ASYNC = 1n;

    private static struct Id(home:int,id:int) {
        public def toString() = "<"+home+","+id+">";
    }
    private static val UNASSIGNED = Id(-1n,-1n);

    private static struct Task(place:Int, kind:Int) {
        public def toString() = "<"+(kind == AT ? "at" : "async")+" live @ "+place+">";
        def this(place:Long, kind:Int) {
            property(place as Int, kind);
        }
    }

    private static struct Edge(src:Int, dst:Int, kind:Int) {
        public def toString() = "<"+(kind == AT ? "at" : "async")+" from "+src+" to "+dst+">";
        def this(srcId:Long, dstId:Long, kind:Int) {
            property(srcId as Int, dstId as Int, kind);
        }
    }

    /**
     * State of a single finish; always stored in Place0
     */
    private static final class State implements x10.io.Unserializable {
        val gfs:GlobalRef[FinishResilientPlace0]; // root finish state
        val id:Id;
        val parentId:Id; // id of parent (UNASSIGNED means no parent / parent is UNCOUNTED)
        var numActive:Long = 0;
        val live = new HashMap[Task,Int]();
        var _transit:HashMap[Edge,Int] = null; // lazily allocated by transit()
        var excs:GrowableRail[CheckedThrowable] = null;  // lazily allocated in addException
        var adopterId:Id = UNASSIGNED; // current adopter (if adopted)
        var adoptees:GrowableRail[Id] = null; // adopted descendents, lazily allocated in seekAdoption
        var _liveAdopted:HashMap[Task,Int] = null; // lazily allocated by liveAdopted()
        var _transitAdopted:HashMap[Edge,Int] = null; // lazily allocated by transitAdopted()

        private def this(id:Id, parentId:Id, gfs:GlobalRef[FinishResilientPlace0]) {
            this.id = id;
            this.parentId = parentId; 
            this.gfs = gfs;
        }
        
        @Inline def isAdopted() = (adopterId != UNASSIGNED);

        def liveAdopted() {
            if (_liveAdopted == null) _liveAdopted = new HashMap[Task,Int]();
            return _liveAdopted;
        }

        def transit() {
            if (_transit == null) _transit = new HashMap[Edge,Int]();
            return _transit;
        }

        def transitAdopted() {
            if (_transitAdopted == null) _transitAdopted = new HashMap[Edge,Int]();
            return _transitAdopted;
        }

        static @Inline def increment[K](map:HashMap[K,Int], k:K) {
            map.put(k, map.getOrElse(k, 0n)+1n);
        }

        static @Inline def decrement[K](map:HashMap[K,Int], k:K) {
            val oldCount = map(k);
            if (oldCount == 1n) {
                 map.remove(k);
            } else {
                 map(k) = oldCount-1n;
            }
        }

        def addException(t:CheckedThrowable) {
            if (excs == null) excs = new GrowableRail[CheckedThrowable]();
            excs.add(t);
        }

        def inTransit(srcId:Long, dstId:Long, kind:Int, tag:String) {
            val e = Edge(srcId, dstId, kind);
            if (!isAdopted()) {
                increment(transit(), e);
                numActive++;
            } else {
                val adopterState = states(adopterId);
                increment(adopterState.transitAdopted(), e);
                adopterState.numActive++;
            }
            if (verbose>=3) {
                debug("==== "+tag+"(id="+id+") after update for: "+srcId + " ==> "+dstId+" kind="+kind);
                if (!isAdopted()) dump(); else states(adopterId).dump();
            }
        }

        def transitToLive(srcId:Long, dstId:Long, kind:Int, tag:String) {
            val e = Edge(srcId, dstId, kind);
            val t = Task(dstId, kind);
            if (!isAdopted()) {
                increment(live,t);
                decrement(transit(), e);
            } else {
                val adopterState = states(adopterId);
                increment(adopterState.liveAdopted(), t);
                decrement(adopterState.transitAdopted(), e);
            }
            if (verbose>=3) {
                debug("==== "+tag+"(id="+id+") after update for: "+srcId + " ==> "+dstId+" kind="+kind);
                if (!isAdopted()) dump(); else states(adopterId).dump();
            }
        }

        def addLive(srcId:Long, dstId:Long, kind:Int, tag:String) {
            val t = Task(dstId, kind);
            if (!isAdopted()) {
                increment(live, t);
                numActive++;
            } else {
                val adopterState = states(adopterId);
                increment(adopterState.liveAdopted(), t);
                adopterState.numActive++;
            }
            if (verbose>=3) {
                debug("==== "+tag+"(id="+id+") after update for: "+srcId + " ==> "+dstId+" kind="+kind);
                if (!isAdopted()) dump(); else states(adopterId).dump();
            }
        }

        def transitToCompleted(srcId:Long, dstId:Long, kind:Int, t:CheckedThrowable) {
            val e = Edge(srcId, dstId, kind);
            if (!isAdopted()) {
                decrement(transit(), e);
                numActive--;
                if (t != null) addException(t);
                if (quiescent()) {
                    releaseLatch();
                    removeFromStates();
                }
            } else {
                val adopterState = states(adopterId);
                decrement(adopterState.transitAdopted(), e);
                adopterState.numActive--;
                if (t != null) adopterState.addException(t);
                if (adopterState.quiescent()) {
                    adopterState.releaseLatch();
                    adopterState.removeFromStates();
                }
            }
        }

        def liveToCompleted(placeId:Long, kind:Int) {
            val t = Task(placeId, kind);
            if (!isAdopted()) {
                decrement(live, t);
                numActive--;
                if (quiescent()) {
                    releaseLatch();
                    removeFromStates();
                }
            } else {
                val adopterState = states(adopterId);
                decrement(adopterState.live, t);
                adopterState.numActive--;
                if (adopterState.quiescent()) {
                    adopterState.releaseLatch();
                    adopterState.removeFromStates();
                }
            }
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
            if (verbose>=2 || (verbose>=1 && quiet)) debug("quiescent(id="+id+") returning " + quiet);
            return quiet;
        }

        def removeFromStates() {
            if (adoptees != null && !adoptees.isEmpty()) {
                for (childId in adoptees.toRail()) {
                    states.remove(childId);
                }
            }
            states.remove(id);
        }

        def addDeadPlaceException(placeId:Long) {
            val e = new DeadPlaceException(Place(placeId));
            e.fillInStackTrace(); // meaningless?
            addException(e);
        }

        def seekAdoption() {
            if (isAdopted() || !gfs.home.isDead()) return;

            var adopterState:State = states(parentId);
            while (adopterState != null) {
                if (!adopterState.gfs.home.isDead()) break; // found first live ancestor
                adopterState = states(adopterState.parentId);
            }

            if (adopterState == null) {
                if (verbose>=1) debug ("==== seekAdoption "+id+" is becoming an orphan; no live ancestor");
                return;
            }

            if (verbose>=1) debug ("==== seekAdoption "+id+" will be adopted by "+adopterState.id);
            if (verbose>=3) {
                debug("==== seekAdoption: dumping states before adoption");
                dump();
                adopterState.dump();
            }

            val asla = adopterState.liveAdopted();
            for (entry in live.entries()) {
                val task = entry.getKey();
                asla.put(task, asla.getOrElse(task,0n) + entry.getValue());
            }
            if (_liveAdopted != null) {
                for (entry in _liveAdopted.entries()) {
                    val task = entry.getKey();
                    asla.put(task, asla.getOrElse(task,0n) + entry.getValue());
                }
            }

            if (_transit != null || _transitAdopted != null) {
                val asta = adopterState.transitAdopted();
                if (_transit != null) {
                    for (entry in _transit.entries()) {
                        val edge = entry.getKey();
                        asta.put(edge, asta.getOrElse(edge,0n) + entry.getValue());
                    }
                    _transit = null;
                }
                if (_transitAdopted != null) {
                    for (entry in _transitAdopted.entries()) {
                        val edge = entry.getKey();
                        asta.put(edge, asta.getOrElse(edge,0n) + entry.getValue());
                    }
                }
                _transitAdopted = null;
            }

            adopterState.numActive += numActive;

            if (adopterState.adoptees == null) adopterState.adoptees = new GrowableRail[Id]();
            adopterState.adoptees.add(id);
            adopterId = adopterState.id;
            if (adoptees != null) {
                for (w in adoptees.toRail()) {
                    adopterState.adoptees.add(w);
                    states(w).adopterId = adopterState.id;
                    if (verbose>=2) debug ("==== seekAdoption "+id+" transfered ward "+w+" to "+adopterState.id);
                }
                adoptees = null;
            }
    
            if (verbose>=3) {
                debug("==== seekAdoption: dumping adopter state after adoption");
                adopterState.dump();
            }
        }

        def convertDeadActivities() {
            if (isAdopted()) return;

            // NOTE: can't say for (p in Place.places()) because we need to see the dead places
            for (i in 0n..((Place.numPlaces() as Int) - 1n)) {
                if (!Place.isDead(i)) continue;

                val deadTasks = new HashSet[Task]();
                for (k in live.keySet()) {
                    if (k.place == i) deadTasks.add(k);
                }
                for (dt in deadTasks) {
                    val count = live.remove(dt);
                    numActive -= count;
                    if (dt.kind == ASYNC) {
                        for (1..count) {
                            if (verbose>=3) debug("adding DPE to "+id+" for live async at "+i);
                            addDeadPlaceException(i);
                        }
                    }
                }

                if (_liveAdopted != null) {
                    val deadWards = new HashSet[Task]();
                    for (k in _liveAdopted.keySet()) {
                        if (k.place == i) deadWards.add(k);
                    }
                    for (dw in deadWards) {
                        val count = _liveAdopted.remove(dw);
                        numActive -= count;
                    }
                }
                  
                if (_transit != null) {
                    val deadEdges = new HashSet[Edge]();
                    for (k in _transit.keySet()) {
                        if (k.src == i || k.dst == i) deadEdges.add(k);
                    }
                    for (de in deadEdges) {
                        val count = _transit.remove(de);
                        numActive -= count;
                        if (de.kind == ASYNC && de.dst == i) {
                            for (1..count) {
                                if (verbose>=3) debug("adding DPE to "+id+" for transit asyncs("+de.src+","+i+")");
                                addDeadPlaceException(i);
                            }
                        }
                    }
                }

                if (_transitAdopted != null) {
                    val deadEdges = new HashSet[Edge]();
                    for (k in _transitAdopted.keySet()) {
                        if (k.src == i || k.dst == i) deadEdges.add(k);
                    }
                    for (de in deadEdges) {
                        val count = _transitAdopted.remove(de);
                        numActive -= count;
                    }
                }
            }
        }

        def dump() {
            val s = new x10.util.StringBuilder();
            s.add("State dump:\n");
            s.add("             id:" + id); s.add('\n');
            s.add("      numActive:"); s.add(numActive); s.add('\n');
            s.add("       parentId: " + parentId); s.add('\n');
            s.add("      adopterId: " + adopterId); s.add('\n');
            if (live.size() > 0) {
                s.add("           live:\n");
                for (e in live.entries()) {
                    s.add("\t\t"+e.getKey()+" = "+e.getValue()+"\n");
                }
            }
            if (_liveAdopted != null && _liveAdopted.size() > 0) {
                s.add("    liveAdopted:\n"); 
                for (e in _liveAdopted.entries()) {
                    s.add("\t\t"+e.getKey()+" = "+e.getValue()+"\n");
                }
            }
            if (_transit != null && _transit.size() > 0) {
                s.add("        transit:\n"); 
                for (e in _transit.entries()) {
                    s.add("\t\t"+e.getKey()+" = "+e.getValue()+"\n");
                }
            }
            if (_transitAdopted != null && _transitAdopted.size() > 0) {
                s.add(" transitAdopted:\n"); 
                for (e in _transitAdopted.entries()) {
                    s.add("\t\t"+e.getKey()+" = "+e.getValue()+"\n");
                }
            }
            debug(s.toString());
        }
    }
    
    private static val states = (here.id==0) ? new HashMap[Id, State]() : null;

    private static val lock = (here.id==0) ? new x10.util.concurrent.Lock() : null;

    private static val nextId = new AtomicInteger(); // per-place portion of unique id
    
    private val id:Id;

    // Initialized by custom deserializer
    private val grlc:GlobalRef[AtomicInteger];
    private transient val ref:GlobalRef[FinishResilientPlace0] = GlobalRef[FinishResilientPlace0](this);
    private transient var isGlobal:Boolean = false;
    private transient var strictFinish:Boolean = false;

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
        strictFinish = false;
        id = Id(here.id as Int, nextId.getAndIncrement());
    }

    private def this(deser:Deserializer) {
        id = deser.readAny() as Id;
        val lc = deser.readAny() as GlobalRef[AtomicInteger];
        grlc = (lc.home == here) ? lc : GlobalRef[AtomicInteger](new AtomicInteger(1n));
        isGlobal = true;
        strictFinish = true;
    }

    public def serialize(ser:Serializer) {
        strictFinish = true;
        if (!isGlobal) globalInit(); // Once we have more than 1 copy of the finish state, we must go global
        ser.writeAny(id);
        ser.writeAny(grlc);
    }

    private def globalInit() {
        latch.lock();
        strictFinish = true;
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
                    State.increment(state.live, Task(gfs.home.id, ASYNC)); // duplicated from my localCount
                    state.numActive = 1;
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
                e.getValue().seekAdoption();
            }
            for (e in states.entries()) {
                e.getValue().convertDeadActivities();
            }
            val toRemove = new HashSet[State]();
            for (e in states.entries()) {
                val s = e.getValue();
                if (s.quiescent()) {
                    s.releaseLatch();
                    toRemove.add(s);
                }
            }
            for (s in toRemove) {
                s.removeFromStates();
            }
        } finally {
            lock.unlock();
        }
        if (verbose>=1) debug("<<<< notifyPlaceDeath returning");
    }

    private def forgetGlobalRefs():void {
        (ref as GlobalRef[FinishResilientPlace0]{self.home == here}).forget();
        (grlc as GlobalRef[AtomicInteger]{self.home==here}).forget();
    }

    def notifySubActivitySpawn(place:Place):void {
        notifySubActivitySpawn(place, ASYNC);
    }
    def notifyShiftedActivitySpawn(place:Place):void {
        notifySubActivitySpawn(place, AT);
    }
    def notifySubActivitySpawn(place:Place, kind:Int):void {
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
                    val state = states(myId);
                    if (Place(srcId).isDead()) {
                        if (verbose>=1) debug("==== notifySubActivitySpawn(id="+myId+") src "+srcId + "is dead; dropping async");
                    } else if (Place(dstId).isDead()) {
                        if (kind == ASYNC) {
                            if (verbose>=1) debug("==== notifySubActivitySpawn(id="+myId+") destination "+dstId + "is dead; pushed DPE for async");
                            state.addDeadPlaceException(dstId);
                        } else {
                            if (verbose>=1) debug("==== notifySubActivitySpawn(id="+myId+") destination "+dstId + "is dead; dropped at");
                        }
                    } else {
                        state.inTransit(srcId, dstId, kind, "notifySubActivitySpawn");
                    }
                } finally {
                    lock.unlock();
                }
            });
        }
        if (verbose>=1) debug("<<<< notifySubActivitySpawn(id="+myId+") returning");
    }

    def notifyRemoteContinuationCreated():void { 
        strictFinish = true;
        if (verbose>=1) debug("<<<< notifyRemoteContinuationCreated(id="+id+") isGlobal = "+isGlobal);
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
    def notifyActivityCreation(srcPlace:Place, activity:Activity, kind:Int):Boolean {
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
                    val state = states(myId);
                    state.transitToLive(srcId, dstId, kind, "notifyActivityCreation");
                }
            } finally {
                lock.unlock();
            }
            try {
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
            } catch (dpe:DeadPlaceException) {
                // can ignore; if the place is dead there is no need to worry about the pending activity
                if (verbose>=2) debug("caught and suppressed DPE when attempting submit pending activity for "+id);
            }
        }

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
                val state = states(myId);
                state.transitToLive(srcId, dstId, kind, "notifyShiftedActivityCreation");
            } finally {
                lock.unlock();
            }
            return true;
        });
    }

    def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void { 
        notifyActivityCreationFailed(srcPlace, t, ASYNC);
    }
    def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable, kind:Int):void { 
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
                    val state = states(myId);
                    state.transitToCompleted(srcId, dstId, kind, t);
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
    def notifyActivityCreatedAndTerminated(srcPlace:Place, kind:Int) {
        val srcId = srcPlace.id; 
        val dstId = here.id;
        val myId = this.id;

        if (dstId == srcId) {
            val lc = localCount().decrementAndGet();
            if (verbose>=1) debug(">>>> notifyActivityCreatedAndTerminated(id="+myId+") called locally, localCount now "+lc);
            if (lc > 0) return;
            if (isGlobal) {
                // if srcId == dstId, then notifySubActivitySpawn goes directly to live (not transit)
                // so we need to decrement accordingly here and then check for quiescence.
                at (place0) @Immediate("notifyActivityCreatedAndTerminated_quiescence_check_to_zero") async {
                    try {
                        lock.lock();
                        if (Place(srcId).isDead()) {
                            // NOTE: no state updates or DPE processing here.
		            //       Must happen exactly once and is done
                            //       when Place0 is notified of a dead place.
                            if (verbose>=1) debug("==== notifyActivityCreatedAndTerminated(id="+myId+") suppressed: "+srcId + " ==> "+srcId+" kind="+kind);
                        } else {
                            val state = states(myId);
                            state.liveToCompleted(srcId, kind);
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
                    val state = states(myId);
                    state.transitToCompleted(srcId, dstId, kind, null);
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
    def notifyActivityTermination(kind:Int):void {
        val lc = localCount().decrementAndGet();
        val myId = this.id;

        if (lc > 0) {
            if (verbose>=1) debug(">>>> notifyActivityTermination(id="+myId+") called, decremented localCount to "+lc);
            return;
        }

        // If this is not the root finish, we are done with the finish state.
        // If this is the root finish, it will be kept alive because waitForFinish
        // is an instance method and it is on the stack of some activity.
        forgetGlobalRefs();

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
                    state.liveToCompleted(dstId, kind);
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
                    if (!state.isAdopted()) { // If adopted, the language semantics are to suppress exception.
                        state.addException(t);
                    }
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
        if ((!Runtime.STRICT_FINISH) && (Runtime.STATIC_THREADS || !strictFinish)) {
            if (verbose>=2) debug("calling worker.join for id="+id);
            Runtime.worker().join(this.latch);
        }

        // wait for the latch release
        if (verbose>=2) debug("calling latch.await for id="+id);
        latch.await(); // wait for the termination (latch may already be released)
        if (verbose>=2) debug("returned from latch.await for id="+id);

        // no more messages will come back to this finish state 
        forgetGlobalRefs();
        
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
     *   - notifyActivityCreation: dst ===> Place0 (decrement transit(src,dst), increment live)
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

        localCount().incrementAndGet();  // synthetic activity to keep finish locally live during async to Place0
        val fsgr = this.ref;

        if (bytes.size >= ASYNC_SIZE_THRESHOLD) {
            if (verbose >= 1) debug("==== spawnRemoteActivity(id="+myId+") selecting indirect (size="+
                                    bytes.size+") srcId="+srcId + " dstId="+dstId);
            val wrappedBody = ()=> @AsyncClosure {
                val deser = new Deserializer(bytes);
                val bodyPrime = deser.readAny() as ()=>void;
                bodyPrime();
            };
            val wbgr = GlobalRef(wrappedBody);          

            at (place0) @Immediate("spawnRemoteActivity_big_async_to_zero") async {
                var markedInTransit:Boolean = false;
                try {
                    lock.lock();
                    val state = states(myId);
                    if (Place(srcId).isDead()) {
                        if (verbose>=1) debug("==== spwanRemoteActivity(id="+myId+") src "+srcId + "is dead; dropping async");
                    } else if (Place(dstId).isDead()) {
                        if (verbose>=1) debug("==== spawnRemoteActivity(id="+myId+") destination "+dstId + "is dead; pushed DPE");
                        state.addDeadPlaceException(dstId);
                    } else {
                        state.inTransit(srcId, dstId, ASYNC, "spawnRemoteActivity(large async)");
                        markedInTransit = true;
                    }
                } finally {
                    lock.unlock();
                }

                try {
                    val mt = markedInTransit;
                    at (wbgr) @Immediate("spawnRemoteActivity_big_back_to_spawner") async {
                        val fs = (fsgr as GlobalRef[FinishResilientPlace0]{self.home == here})();
                        try {
                            if (mt) x10.xrx.Runtime.x10rtSendAsync(dstId, wbgr(), fs, null, null);
                        } catch (dpe:DeadPlaceException) {
                            // not relevant to immediate thread; DPE raised in convertDeadActivities
                            if (verbose>=2) debug("caught and suppressed DPE from x10rtSendAsync from spawnRemoteActivity_big_back_to_spawner for "+id);
                        }
                        wbgr.forget();
                        fs.notifyActivityTermination();
                    }
                } catch (dpe:DeadPlaceException) {
                    // can ignore; if the src place just died there is nothing left to do.
                    if (verbose>=2) debug("caught and suppressed DPE when attempting spawnRemoteActivity_big_back_to_spawner for "+id);
                }
            }
        } else {
            if (verbose >= 1) debug(">>>>  spawnRemoteActivity(id="+myId+") selecting direct (size="+
                                    bytes.size+") srcId="+srcId + " dstId="+dstId);

            at (place0) @Immediate("spawnRemoteActivity_to_zero") async {
                try {
                    lock.lock();
                    val state = states(myId);
                    if (Place(srcId).isDead()) {
                        if (verbose>=1) debug("==== spwanRemoteActivity(id="+myId+") src "+srcId + "is dead; dropping async");
                    } else if (Place(dstId).isDead()) {
                        if (verbose>=1) debug("==== spawnRemoteActivity(id="+myId+") destination "+dstId + "is dead; pushed DPE");
                        state.addDeadPlaceException(dstId);
                    } else {
                        state.addLive(srcId, dstId, ASYNC, "spawnRemoteActivity(small async)");
                    }
                } finally {
                    lock.unlock();
                }
                try {
                    at (fsgr) @Immediate("spawnRemoteActivity_dec_local_count") async {
                        fsgr().notifyActivityTermination(); // end of synthetic local activity
                    }
                } catch (dpe:DeadPlaceException) {
                    // can ignore; if the place just died here is no need to worry about updating local count
                    if (verbose>=2) debug("caught and suppressed DPE when attempting spawnRemoteActivity_dec_local_count for "+id);
                }
                try {
                    at (Place(dstId)) @Immediate("spawnRemoteActivity_dstPlace") async {
                        if (verbose >= 1) debug("==== spawnRemoteActivity(id="+myId+") submitting activity from "+srcId+" at "+dstId);
                        val wrappedBody = ()=> {
                            // defer deserialization to reduce work on immediate thread
                            val deser = new Deserializer(bytes);
                            val bodyPrime = deser.readAny() as ()=>void;
                            bodyPrime();
                        };
                        Runtime.worker().push(new Activity(42, wrappedBody, this));
                    }
                } catch (dpe:DeadPlaceException) {
                    // can ignore; if the place just died there is no need to worry about submitting the activity
                    if (verbose>=2) debug("caught and suppressed DPE when attempting spawnRemoteActivity_dstPlace for "+id);
                }
            }
            if (verbose>=1) debug("<<<< spawnRemoteActivity(id="+myId+") returning");
        }
    }
}
