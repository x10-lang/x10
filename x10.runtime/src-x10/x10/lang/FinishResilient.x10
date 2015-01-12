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
import x10.util.concurrent.AtomicBoolean;
import x10.util.concurrent.Condition;

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
        if (v>0 && here.id==0) Console.OUT.println(name + "=" + v);
        return v;
    }
    protected static def debug(msg:String) {
        val nsec = System.nanoTime();
        val output = "[nsec=" + nsec + " place=" + here.id + " " + Runtime.activity() + "] " + msg;
        Console.OUT.println(output); Console.OUT.flush();
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
    // def notifyActivityCreation(srcPlace:Place, activity:Activity):Boolean;
    // def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity):Boolean;
    // def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void;
    // def notifyActivityCreatedAndTerminated(srcPlace:Place):void;
    // def notifyActivityTermination():void;
    // def pushException(t:CheckedThrowable):void;
    // def waitForFinish():void;

    private static def failJavaOnlyMode() {
        throw new UnsupportedOperationException("Java-only RESILIENT_MODE " + Runtime.RESILIENT_MODE);
    }
    
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
        case Configuration.RESILIENT_MODE_DEFAULT:
        case Configuration.RESILIENT_MODE_PLACE0:
        {
            val p = (parent!=null) ? parent : getCurrentFS();
            val l = (latch!=null) ? latch : new SimpleLatch();
            fs = FinishResilientPlace0.make(p, l);
            break;
        }
        case Configuration.RESILIENT_MODE_HC:
        {
            val p = (parent!=null) ? parent : getCurrentFS();
            val l = (latch!=null) ? latch : new SimpleLatch();
            val o = p as Any;
            var r:FinishState = null;
            @Native("java", "r = x10.lang.managed.FinishResilientHC.make(o, l);")
            { failJavaOnlyMode(); }
            fs = r;
            break;
        }
        case Configuration.RESILIENT_MODE_PLACE0_OPTIMIZED:
        {
            val p = (parent!=null) ? parent : getCurrentFS();
            val l = (latch!=null) ? latch : new SimpleLatch();
            fs = FinishResilientPlace0opt.make(p, l);
            break;
        }
        case Configuration.RESILIENT_MODE_SAMPLE:
        case Configuration.RESILIENT_MODE_SAMPLE_HC:
        {
            val p = (parent!=null) ? parent : getCurrentFS();
            val l = (latch!=null) ? latch : new SimpleLatch();
            fs = FinishResilientSample.make(p, l);
            break;
        }
        default:
            throw new UnsupportedOperationException("Unsupported RESILIENT_MODE " + Runtime.RESILIENT_MODE);
        }
        if (verbose>=1) debug("FinishResilient.make returning, fs=" + fs);
        return fs;
    }
    
    static def notifyPlaceDeath() {
        if (verbose>=1) debug("FinishResilient.notifyPlaceDeath called");
        switch (Runtime.RESILIENT_MODE) {
        case Configuration.RESILIENT_MODE_DEFAULT:
        case Configuration.RESILIENT_MODE_PLACE0:
            FinishResilientPlace0.notifyPlaceDeath();
            break;
        case Configuration.RESILIENT_MODE_HC:
            @Native("java", "x10.lang.managed.FinishResilientHC.notifyPlaceDeath();")
            { failJavaOnlyMode(); }
            break;
        case Configuration.RESILIENT_MODE_PLACE0_OPTIMIZED:
            FinishResilientPlace0opt.notifyPlaceDeath();
            break;
        case Configuration.RESILIENT_MODE_SAMPLE:
        case Configuration.RESILIENT_MODE_SAMPLE_HC:
            FinishResilientSample.notifyPlaceDeath();
            break;
        default:
            throw new UnsupportedOperationException("Unsupported RESILIENT_MODE " + Runtime.RESILIENT_MODE);
        }
        if (verbose>=1) debug("FinishResilient.notifyPlaceDeath returning");
    }
    
    /*
     * Utility methods used in subclasses
     */

    // returns true if dst is not dead, does not wait for the completion
    protected static def lowLevelSend(dst:Place, cl:()=>void):Boolean {
        if (verbose>=4) debug("----lowLevelSend called, dst.id=" + dst.id + " ("+here.id+"->"+dst.id+")");
        if (here == dst) {
            if (verbose>=4) debug("----lowLevelSend locally calling cl()");
            cl();
            if (verbose>=4) debug("----lowLevelSend locally executed, returning true");
            return true;
        }
        if (verbose>=4) debug("----lowLevelSend remote execution");
        if (dst.isDead()) {
            if (verbose>=4) debug("----lowLevelSend returning false");
            return false;
        }
        Runtime.x10rtSendMessage(dst.id, () => @RemoteInvocation("finish_resilient_low_level_send_out") {
            if (verbose>=4) debug("----lowLevelSend(remote) calling cl()");
            cl();
            if (verbose>=4) debug("----lowLevelSend(remote) returned from cl()");
        }, null);
        if (verbose>=4) debug("----lowLevelSend returning true");
        return true;
    }

    // returns true if cl is processed at dst
    protected static def lowLevelAt(dst:Place, cl:()=>void):Boolean {
        if (verbose>=1) {
            if (Runtime.worker().promoted) {
                debug("DANGER: lowlevelAt called on @Immediate worker!");
                new Exception().printStackTrace();
            }
        }
        if (verbose>=4) debug("---- lowLevelAt called, dst.id=" + dst.id + " ("+here.id+"->"+dst.id+")");
        if (here == dst) {
            if (verbose>=4) debug("---- lowLevelAt locally calling cl()");
            cl();
            if (verbose>=4) debug("---- lowLevelAt locally executed, returning true");
            return true;
        }
        
        // remote call
        val cond = new Condition();
        val condGR = GlobalRef[Condition](cond); 
        val exc = GlobalRef(new Cell[CheckedThrowable](null));
        if (verbose>=4) debug("---- lowLevelAt initiating remote execution");
        at (dst) @Immediate("finish_resilient_low_level_at_out") async {
            try {
                if (verbose>=4) debug("---- lowLevelAt(remote) calling cl()");
                cl();
                if (verbose>=4) debug("---- lowLevelAt(remote) returned from cl()");
                at (condGR) @Immediate("finish_resilient_low_level_at_back") async {
                    if (verbose>=4) debug("---- lowLevelAt(home) releasing cond");
                    condGR().release();
                }
            } catch (t:Exception) {
                if (verbose>=4) debug("---- lowLevelAt(remote) caught exception="+t);
                at (condGR) @Immediate("finish_resilient_low_level_at_back_exc") async {
                    if (verbose>=4) debug("---- lowLevelAt(home) setting exc and releasing cond");
                    exc()(t);
                    condGR().release();
                };
            }
            if (verbose>=4) debug("---- lowLevelAt(remote) finished");
        };
        
        if (verbose>=4) debug("---- lowLevelAt waiting for cond");
        cond.await();
        if (verbose>=4) debug("---- lowLevelAt released from cond");
	// Unglobalize objects
	condGR.forget();
        exc.forget();

        val t = exc()();
        if (t != null) {
            if (verbose>=4) debug("---- lowLevelAt throwing exception " + t);
            Runtime.throwCheckedWithoutThrows(t);
        }
        if (verbose>=4) debug("---- lowLevelAt returning true");
        return true; // success
    }
    
    // returns true if cl is processed at dst
    protected static def lowLevelFetch[T](dst:Place, result:Cell[T], cl:()=>T):Boolean {
        if (verbose>=1) {
            if (Runtime.worker().promoted) {
                debug("DANGER: lowlevelAt called on @Immediate worker!");
                new Exception().printStackTrace();
            }
        }
        if (verbose>=4) debug("---- lowLevelFetch called, dst.id=" + dst.id + " ("+here.id+"->"+dst.id+")");
        if (here == dst) {
            if (verbose>=4) debug("---- lowLevelFetch locally calling cl()");
            result(cl()); // set the result
            if (verbose>=4) debug("---- lowLevelFetch locally executed, returning true");
            return true;
        }
        
        // remote call
        val cond = new Condition();
        val condGR = GlobalRef[Condition](cond); 
        val exc = GlobalRef(new Cell[CheckedThrowable](null));
        val gresult = GlobalRef(result);
        if (verbose>=4) debug("---- lowLevelFetch remote execution");
        at (dst) @Immediate("finish_resilient_low_level_fetch_out") async {
            try {
                if (verbose>=4) debug("---- lowLevelFetch(remote) calling cl()");
                val r = cl();
                if (verbose>=4) debug("---- lowLevelFetch(remote) returned from cl()");
                at (condGR) @Immediate("finish_resilient_low_level_fetch_back") async {
                    if (verbose>=4) debug("---- lowLevelFetch(home) setting the result and done-flag");
                    gresult()(r); // set the result
                    condGR().release();
                };
            } catch (t:Exception) {
                at (condGR) @Immediate("finish_resilient_low_level_fetch_back_exc") async {
                    if (verbose>=4) debug("---- lowLevelFetch(home) setting exc and relasing cond");
                    exc()(t);
                    condGR().release();
                };
            }
            if (verbose>=4) debug("---- lowLevelFetch(remote) finished");
        };
        
        if (verbose>=4) debug("---- lowLevelFetch waiting for cond");
        cond.await();
        if (verbose>=4) debug("---- lowLevelFetch released from cond");
        val t = exc()();

	// Unglobalize objects
	condGR.forget();
        exc.forget();
	gresult.forget();

        if (t != null) {
            if (verbose>=4) debug("---- lowLevelFetch throwing exception " + t);
            Runtime.throwCheckedWithoutThrows(t);
        }
        if (verbose>=4) debug("---- lowLevelFetch returning true");
        return true; // success
    }
}
