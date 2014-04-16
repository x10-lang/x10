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
import x10.util.concurrent.AtomicBoolean;

/*
 * Common abstract class for Resilient Finish
 */
abstract class FinishResilient extends FinishState {
    /*
     * for debug
     */
    protected static val verbose = getEnvLong("X10_RESILIENT_VERBOSE"); // should be copied to subclass
    protected static def getEnvLong(name:String) {
        val env = System.getenv(name);
        val v = (env!=null) ? Long.parseLong(env) : 0;
        if (v>0 && here.id==0) Runtime.println(name + "=" + v);
        return v;
    }
    protected static def debug(msg:String) {
        val nsec = System.nanoTime();
        val output = "[FR nsec=" + nsec + " place=" + here.id + " " + Runtime.activity() + "] ---- " + msg;
        Runtime.println(output); //Runtime.flush();
    }
    protected static def dumpStack(msg:String) {
        try { throw new Exception(msg); } catch (e:Exception) { e.printStackTrace(); }
    }
    
    /*
     * Static methods to be implemented in subclasses
     */
    // static def make(parent:FinishResilient, latch:SimpleLatch):FinishResilient;
    // static def notifyPlaceDeath():void;
    
    /*
     * Other methods to be implemented in subclasses (declared in FinishState class)
     */
    // def notifySubActivitySpawn(place:Place):void;
    // def notifyActivityCreation(srcPlace:Place):Boolean;
    // def notifyActivityTermination():void;
    // def pushException(t:Exception):void;
    // def waitForFinish():void;
    
    /*
     * Dispatcher methods
     */
    private static def getCurrentFS() {
        val a = Runtime.activity();
        return (a!=null) ? a.finishState() : null;
    }
    static def make(parent:FinishState, latch:SimpleLatch):FinishState { // parent/latch may be null
        if (verbose>=1) debug("FinishResilient.make called, parent=" + parent + " latch=" + latch);
        var fs:FinishState;
        switch (Runtime.RESILIENT_MODE) {
        case Configuration.RESILIENT_MODE_SAMPLE:
            val p = (parent!=null) ? parent : getCurrentFS();
            val l = (latch!=null) ? latch : new SimpleLatch();
            fs = FinishResilientSample.make(p, l);
            break;
            
            //TODO: followings will be restrucutured
        case Configuration.RESILIENT_MODE_PLACE_ZERO:
            fs = new FinishState.FinishResilientPlaceZero(latch);
            break;
        case Configuration.RESILIENT_MODE_DISTRIBUTED:
            fs = new FinishState.FinishResilientDistributed(latch==null ? new SimpleLatch() : latch);
            break;
        case Configuration.RESILIENT_MODE_ZOO_KEEPER:
            fs = new FinishState.FinishResilientZooKeeper(latch);
            break;
        default:
            throw new UnsupportedOperationException("Unsupported RESILIENT_MODE " + Runtime.RESILIENT_MODE);
        }
        if (verbose>=1) debug("FinishResilient.make returning, fs=" + fs);
        return fs;
    }
    
    static def notifyPlaceDeath() {
        if (verbose>=1) debug("FinishResilient.notifyPlaceDeath called");
        switch (Runtime.RESILIENT_MODE) {
        case Configuration.RESILIENT_MODE_SAMPLE:
            FinishResilientSample.notifyPlaceDeath();
            break;
            
            //TODO: followings will be restructured
        case Configuration.RESILIENT_MODE_PLACE_ZERO:
            if (here.id == 0) {
                // most finishes are woken up by 'atomic'
                atomic { }
                // the root one also needs to have its latch released
                // also adopt activities of finishes whose homes are dead into closest live parent
                ResilientStorePlaceZero.notifyPlaceDeath((Runtime.rootFinish as FinishResilientPlaceZero).id);
            }
            break;
        case Configuration.RESILIENT_MODE_DISTRIBUTED:
            FinishState.FinishResilientDistributedMaster.notifyAllPlaceDeath();
            // merge backups to parent
            break;
        case Configuration.RESILIENT_MODE_ZOO_KEEPER:
            //
            break;
        default:
            throw new UnsupportedOperationException("Unsupported RESILIENT_MODE " + Runtime.RESILIENT_MODE);
        }
        if (verbose>=1) debug("FinishResilient.notifyPlaceDeath returning");
    }
    
    /*
     * Methods implemented here
     */
    public def simpleLatch():SimpleLatch { // have been used only in Runtime.Worker.loop2()
        throw new UnsupportedOperationException("Obsolete function");
    }
    
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
        
        val tmp_finish = make(this/*parent*/, null/*latch*/);
        // TODO: clockPhases are now passed but their resiliency is not supported yet
        // TODO: This implementation of runAt does not explicitly dealloc things
        val home = here;
        tmp_finish.notifySubActivitySpawn(place);
        
        // XTENLANG-3357: clockPhases must be passed and returned
        val myActivity = Runtime.activity();
        val clockPhases = myActivity.clockPhases;
        val cpCell = new Cell[Activity.ClockPhases](clockPhases);
        val cpGref = GlobalRef(cpCell);
        
        // [DC] do not use at (place) async since the finish state is handled internally
        // [DC] go to the lower level...
        val cl = () => @RemoteInvocation("fiish_resilient_run_at") {
            val exec_body = () => {
                val remoteActivity = Runtime.activity();
                remoteActivity.clockPhases = clockPhases; // XTENLANG-3357: set passed clockPhases
                if (tmp_finish.notifyActivityCreation(home)) {
                    try {
                        try {
                            body();
                        } catch (e:Runtime.AtCheckedWrapper) {
                            throw e.getCheckedCause();
                        } 
                    } catch (t:CheckedThrowable) {
                        val e = Exception.ensureException(t);
                        tmp_finish.pushException(e);
                    }
                    // XTENLANG-3357: return the (maybe modified) clockPhases, similar code as "at (cpGref) { cpGref().set(clockPhases); }"
                    // TODO: better to merge this with notifyActivityTermination to reduce send
                    val cl1 = ()=> @RemoteInvocation("finish_resilient_run_at_1") {
                        val gref = cpGref as GlobalRef[Cell[Activity.ClockPhases]{self==cpCell,cpCell!=null}]{home==here,cpCell!=null};
                        val cell = gref(); cell.set(clockPhases); // this will be set to myActivity.clockPhases
                    };
                    Runtime.x10rtSendMessage(cpGref.home.id, cl1, null);
                    Unsafe.dealloc(cl1);
                    
                    tmp_finish.notifyActivityTermination();
                }
                remoteActivity.clockPhases = null; // XTENLANG-3357
            };
            Runtime.execute(new Activity(exec_body, home, real_finish, false, false));
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
            val e2 = e.exceptions(0);
            if (verbose>=1) debug("FinishResilient.runAt received exception="+e2);
            if (e2 instanceof WrappedThrowable) Runtime.throwCheckedWithoutThrows(e2.getCause());
            else throw e2;
        }
        if (verbose>=1) debug("FinishResilient.runAt returning (remotely executed)");
    }
    
    public def evalAt(place:Place, body:()=>Any, prof:Runtime.Profile):Any {
        if (verbose>=1) debug("FinishResilient.evalAt called, place.id=" + place.id);
        Runtime.ensureNotInAtomic();
        if (place.id == Runtime.hereLong()) {
            // local path can be the same as before
            val result = Runtime.evalAtNonResilient(place, body, prof);
            if (verbose>=1) debug("FinishResilient.evalAt returning result=" + result +" (locally executed)");
            return result;
        }
        
        val dummy_data = new Empty(); // for XTENLANG-3324
        @StackAllocate val me = @StackAllocate new Cell[Any](dummy_data);
        val me2 = GlobalRef(me);
        @Profile(prof) at (place) {
            val r:Any = body();
            at (me2) { me2()(r); }
        }
        // Fix for XTENLANG-3324
        if (me()==dummy_data) { // no result set
            if (verbose>=1) debug("FinishResilient.evalAt returns no result, target place may be dead");
            if (place.isDead()) throw new DeadPlaceException(place);
            else me(null); // should throw some exception?
        }
        
        val result = me();
        if (verbose>=1) debug("FinishResilient.evalAt returning result=" + result +" (remotely executed)");
        return result;
    }
    
    /*
     * Utility methods used in subclasses
     */
    // returns true if cl is processed at dst
    protected static def lowLevelAt(dst:Place, cl:()=>void):Boolean {
        if (verbose>=4) debug("FinishResilient.lowLevelAt called, dst.id=" + dst.id);
        if (here == dst) {
            if (verbose>=4) debug("FinishResilient.lowLevelAt locally calling cl()");
            cl();
            if (verbose>=4) debug("FinishResilient.lowLevelAt locally executed, returning true");
            return true;
        }
        
        // remote call
        val exc = GlobalRef(new Cell[Exception](null));
        val done = GlobalRef(new AtomicBoolean());
        if (verbose>=4) debug("FinishResilient.lowLevelAt remote execution");
        Runtime.x10rtSendMessage(dst.id, () => @RemoteInvocation("finish_resilient_low_level_at_out") {
            // callee
            try {
                if (verbose>=4) debug("(remote) calling cl()");
                cl();
                if (verbose>=4) debug("(remote) returned from cl()");
                Runtime.x10rtSendMessage(done.home.id, () => @RemoteInvocation("finish_resilient_low_level_at_back") {
                    if (verbose>=4) debug("(home) setting done-flag");
                    done.getLocalOrCopy().getAndSet(true);
                }, null);
            } catch (t:Exception) {
                if (verbose>=4) debug("(remote) caught exception="+t);
                Runtime.x10rtSendMessage(done.home.id, () => @RemoteInvocation("finish_resilient_low_level_at_back_exc") {
                    // [DC] assume that the write barrier on "done" is enough to see update on exc
                    if (verbose>=4) debug("(home) setting exc and done-flag");
                    exc.getLocalOrCopy()(t);
                    done.getLocalOrCopy().getAndSet(true);
                }, null);
            }
            if (verbose>=4) debug("(remote) finished");
        }, null);
        
        // caller
        if (verbose>=4) debug("FinishResilient.lowLevelAt waiting for done-flag");
        if (!done().get()) { // Fix for XTENLANG-3303/3305
            Runtime.increaseParallelism();
            do {
                Runtime.x10rtProbe();
                if (dst.isDead()) {
                    Runtime.decreaseParallelism(1n);
                    if (verbose>=4) debug("FinishResilient.lowLevelAt returning false");
                    return false;
                }
            } while (!done().get());
            Runtime.decreaseParallelism(1n);
        }
        if (verbose>=4) debug("FinishResilient.lowLevelAt returned from waiting loop");
        val t = exc()();
        if (t != null) {
            if (verbose>=4) debug("FinishResilient.lowLevelAt throwing exception " + t);
            throw t;
        }
        if (verbose>=4) debug("FinishResilient.lowLevelAt returning true");
        return true; // success
    }
    
    // returns true if cl is processed at dst
    protected static def lowLevelFetch[T](dst:Place, result:Cell[T], cl:()=>T):Boolean {
        if (verbose>=4) debug("FinishResilient.lowLevelFetch called, dst.id=" + dst.id);
        if (here == dst) {
            if (verbose>=4) debug("FinishResilient.lowLevelFetch locally calling cl()");
            result(cl()); // set the result
            if (verbose>=4) debug("FinishResilient.lowLevelFetch locally executed, returning true");
            return true;
        }
        
        // remote call
        val exc = GlobalRef(new Cell[Exception](null));
        val done = GlobalRef(new AtomicBoolean(false));
        val gresult = GlobalRef(result);
        if (verbose>=4) debug("FinishResilient.lowLevelFetch remote execution");
        Runtime.x10rtSendMessage(dst.id, () => @RemoteInvocation("finish_resilient_low_level_fetch_out") {
            // callee
            try {
                if (verbose>=4) debug("(remote) calling cl()");
                val r = cl();
                if (verbose>=4) debug("(remote) returned from cl()");
                Runtime.x10rtSendMessage(done.home.id, () => @RemoteInvocation("fiish_resilient_low_level_fetch_back") {
                    if (verbose>=4) debug("(home) setting the result and done-flag");
                    gresult.getLocalOrCopy()(r); // set the result
                    done.getLocalOrCopy().getAndSet(true);
                }, null);
            } catch (t:Exception) {
                Runtime.x10rtSendMessage(done.home.id, () => @RemoteInvocation("finish_resilient_low_level_fetch_back_exc") {
                    // [DC] assume that the write barrier on "done" is enough to see update on exc
                    if (verbose>=4) debug("(home) setting exc and done-flag");
                    exc.getLocalOrCopy()(t);
                    done.getLocalOrCopy().getAndSet(true);
                }, null);
            }
            if (verbose>=4) debug("(remote) finished");
        }, null);
        
        // caller
        if (verbose>=4) debug("FinishResilient.lowLevelFetch waiting for done-flag");
        if (!done().get()) { // Fix for XTENLANG-3303/3305
            Runtime.increaseParallelism();
            do {
                Runtime.x10rtProbe();
                if (dst.isDead()) {
                    Runtime.decreaseParallelism(1n);
                    if (verbose>=4) debug("FinishResilient.lowLevelFetch returning false");
                    return false;
                }
            } while (!done().get());
            Runtime.decreaseParallelism(1n);
        }
        if (verbose>=4) debug("FinishResilient.lowLevelFetch returned from waiting loop");
        val t = exc()();
        if (t != null) {
            if (verbose>=4) debug("FinishResilient.lowLevelFetch throwing exception " + t);
            throw t;
        }
        if (verbose>=4) debug("FinishResilient.lowLevelFetch returning true");
        return true; // success
    }
}
