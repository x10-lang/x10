/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2016-2017.
 */

package x10.xrx;

import x10.util.ArrayList;
import x10.util.HashMap;
import x10.util.concurrent.AtomicInteger;
import x10.util.concurrent.Lock;

/**
 * A class to keep track of outstanding Get requests issued by this
 * place to satisfy asyncCopy calls that have a remote source Rail.
 * 
 * In Resilient X10, on notification of Place death, each Place
 * will call notifyPlaceDeath on its local GetRegistry and raise
 * DPE exceptions as needed for any outstanding Get whose data is
 * located in a dead place.
 * 
 */
final class GetRegistry {

    private static val outstandingRequests = new HashMap[Int,GetHandle]();
    private static val nextId = new AtomicInteger(1n);
    private static val lock = new Lock();
    
    static class GetHandle {
        val srcPlace:Place;
        val dst:Any; /* Rail[T] */
        val dstIdx:Int;
        val numElems:Int;
        val finishState:FinishState;
        val notifier:()=>void;
        
        private def this(srcPlace:Place, dst:Any, dstIdx:Int, numElems:Int, finishState:FinishState, notifier:()=>void) {
            this.srcPlace = srcPlace;
            this.dst = dst;
            this.dstIdx = dstIdx;
            this.numElems = numElems;
            this.finishState = finishState;
            this.notifier = notifier;
        }
    }

    // Used only by NativeX10.
    // The memory operation is done at the x10rt layer, so the Rail, dstIdx, and numElems values are not needed
    static def registerGet(srcPlace:Place, finishState:FinishState, notifier:()=>void):Int {
        return registerGet(srcPlace, null, -1n, -1n, finishState, notifier);
    }

    static def registerGet(srcPlace:Place, dst:Any /*Rail[T]*/, dstIdx:Int, numElems:Int, finishState:FinishState, notifier:()=>void):Int {
        lock.lock();
        val id = nextId.getAndIncrement();
        val handle = new GetHandle(srcPlace, dst, dstIdx, numElems, finishState, notifier);
        outstandingRequests.put(id, handle);
        lock.unlock();
        return id;
    }
    
    static def completeGet(id:Int):GetHandle {
        lock.lock();
        val ans = outstandingRequests.remove(id);
        lock.unlock();
        return ans;
    }
    
    static def squashGet(id:Int):void {
        lock.lock();
        outstandingRequests.remove(id);
        lock.unlock();
    }

    static def notifyPlaceDeath():void {
        lock.lock();
        val toRemove = new ArrayList[Int]();
        for (e in outstandingRequests.entries()) {
            val request = e.getValue();
            if (request.srcPlace.isDead()) {
                toRemove.add(e.getKey());
                if (request.finishState != null) {
                    request.finishState.pushException(new DeadPlaceException(request.srcPlace, "Source place died during asyncCopy operation"));
                    request.finishState.notifyActivityCreatedAndTerminated(here);
                } else {
                    if (!Configuration.silenceInternalWarnings()) {
                        Console.ERR.println("WARNING: uncounted get from "+request.srcPlace+" will never complete due to place death.");
                    }
                    // FIXME: need a way to associate a failure callback with the uncounted copy!
                }
            }
        }

        for (id in toRemove) {
            outstandingRequests.remove(id);
        }
        lock.unlock();
    }
}
