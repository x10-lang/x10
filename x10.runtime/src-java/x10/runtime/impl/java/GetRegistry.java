/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2016-2016.
 */

package x10.runtime.impl.java;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import x10.core.Rail;
import x10.core.fun.VoidFun_0_0;
import x10.lang.DeadPlaceException;
import x10.lang.Place;
import x10.xrx.FinishState;

/**
 * A class to keep track of outstanding Get requests issued by this
 * place to satisfy asyncCopy calls that have a remote source Rail.
 * 
 * In Resilient X10, on notification of Place death, each Place
 * will call notifyPlaceDeath on its local GetRegistry and raise
 * DPE exceptions as needed for any outstanding Get whose data is
 * located in a dead place.
 * 
 * @see Runtime#asyncCopyFrom(x10.rtt.Type, x10.lang.GlobalRail, int, Rail, int, int)
 * @see Runtime#uncountedCopyFrom(x10.rtt.Type, x10.lang.GlobalRail, int, Rail, int, int, VoidFun_0_0)
 * @see Runtime#getReceive(java.io.InputStream)
 * @see Runtime#getCompletedReceive(java.io.InputStream)
 */
public class GetRegistry {
    private static HashMap<Integer,GetHandle> outstandingRequests = new HashMap<Integer,GetHandle>();
    private static int nextId = 1;
    
    static synchronized int registerGet(Place srcPlace, Rail<?> dst, int dstIdx, int numElems, FinishState finishState, VoidFun_0_0 notifier) {
        int id = nextId++;
        GetHandle handle = new GetHandle(srcPlace, dst, dstIdx, numElems, finishState, notifier);
        outstandingRequests.put(id, handle);
        return id;
    }
    
    static synchronized GetHandle completeGet(int id) {
        return outstandingRequests.remove(id);
    }
    
    static synchronized void squashGet(int id) {
        outstandingRequests.remove(id);
    }

    public static synchronized void notifyPlaceDeath() {
        Iterator<Map.Entry<Integer,GetHandle>> iter = outstandingRequests.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, GetHandle> entry = iter.next();
            GetHandle request = entry.getValue();
            if (request.srcPlace.isDead$O()) {
                iter.remove();
                if (request.finishState != null) {
                    request.finishState.pushException(new DeadPlaceException(request.srcPlace, "Place died during asyncCopy operation"));
                    request.finishState.notifyActivityTermination();
                }
            }
        }
    }
    
    static class GetHandle {
        final Place srcPlace;
        final Rail<?> dst;
        final int dstIdx;
        final int numElems;
        final FinishState finishState;
        final VoidFun_0_0 notifier;
        
        private GetHandle(Place srcPlace, Rail<?> dst, int dstIdx, int numElems, FinishState finishState, VoidFun_0_0 notifier) {
            this.srcPlace = srcPlace;
            this.dst = dst;
            this.dstIdx = dstIdx;
            this.numElems = numElems;
            this.finishState = finishState;
            this.notifier = notifier;
        }
    }
}
