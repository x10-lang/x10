/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 *  (C) Copyright Australian National University 2013.
 */

package x10.compiler;

import x10.xrx.Runtime;

/**
 * A single-place lazy-initialised worker-local store.
 * On first access by a particular worker thread, a worker-local instance
 * of type T is created using the provided init operation.
 */
public final class WorkerLocal[T]{T isref, T haszero} implements ()=>T,(T)=>void {
    transient val store:Rail[T];
    var init:()=>T;

    public def this(init:() => T) {
        this.store = new Rail[T](Runtime.MAX_THREADS);
        this.init = init;
    }

    public operator this():T {
        var t:T = store(Runtime.workerId());
        if (t == null) {
            t = init();
            store(Runtime.workerId()) = t;
        }
        return t;
    }

    public operator this(t:T):void {
        store(Runtime.workerId()) = t;
    }

    /**
     * Set the init operation for this worker local handle, and clear all
     * current values.
     */
    public def resetAll(init:()=>T):void {
        store.clear(); // in case previously initialized
        this.init = init;
    }

    /** 
     * Apply the given operation in parallel to each thread local value.
     */
    public def applyToAll(op:(t:T)=>void):void {
        finish for (i in 0..(store.size-1)) {
            val t = store(i);
            if (t != null) {
                async op(t);
            }
        }
        return;
    }

    /** 
     * Reduce partial results from each thread and return combined result.
     */
    public def reduce(op:(a:T,b:T)=>T):T {
        var result:T = null;
        for (i in 0..(store.size-1)) {
            val t = store(i);
            if (t != null) {
                if (result == null) result = t;
                else result = op(result, t);
            }
        }
        return result;
    }

    /** 
     * Reduce partial results from each thread using the given 'result' as
     * the initial value.  
     * This can be used e.g. to sum a Rail[Double] 'in place' as follows:
     * result_worker.reduceLocal( result, 
     *   (a:Rail[Double],b:Rail[Double]) => 
     *      { a.map(result, b, (x:Double,y:Double)=>(x+y) ) as Rail[Double] }
     *   );
     */
    public def reduce(var result:T, op:(a:T,b:T)=>T) {
        for (i in 0..(store.size-1)) {
            val t = store(i);
            if (t != null) {
                result = op(result, t);
            }
        }
        return result;
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
