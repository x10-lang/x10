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
            @Native("java", "r = x10.xrx.managed.FinishResilientHC.make(o, l);")
            { failJavaOnlyMode(); }
            fs = r;
            break;
        }
        case Configuration.RESILIENT_MODE_HC_OPTIMIZED:
        {
           val p = (parent!=null) ? parent : getCurrentFS();
           val l = (latch!=null) ? latch : new SimpleLatch();
           val o = p as Any;
           var r:FinishState = null;
           @Native("java", "r = x10.xrx.managed.FinishResilientHCopt.make(o, l);")
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
            @Native("java", "x10.xrx.managed.FinishResilientHC.notifyPlaceDeath();")
            { failJavaOnlyMode(); }
            break;
        case Configuration.RESILIENT_MODE_HC_OPTIMIZED:
        @Native("java", "x10.xrx.managed.FinishResilientHC.notifyPlaceDeath();")
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
}
