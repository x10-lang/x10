/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2013.
 */

package x10.util;

/**
 * A handle to place-local lazy-initialised worker-local storage.
 * At each place, a worker-local instance of type T is created using the init
 * operation when required for each worker thread.
 */
public class WorkerLocalHandle[T]{T isref, T haszero} implements ()=>T,(T)=>void {
    private static class State[U]{U isref, U haszero} {
        public def this(init:() => U) {
            this.store = new Rail[U](Runtime.MAX_THREADS);
            this.init = init;
        }
        public val store:Rail[U];
        public var init:()=>U;
    }

    private val state:PlaceLocalHandle[State[T]];

    public def this() = this(null);

    public def this(init:() => T) {
        val state = PlaceLocalHandle.make[State[T]](Place.places(), ()=>new State[T](init));
        this.state = state;
    }

    public operator this():T {
        val localState = state();
        var t:T = localState.store(Runtime.workerId());
        if (t == null) {
            t = localState.init();
            localState.store(Runtime.workerId()) = t;
        }
        return t;
    }

    public operator this(t:T):void {
        state().store(Runtime.workerId()) = t;
    }

    /**
     * Set the init operation for this worker local handle, and clear all
     * current values.
     */
    public def initLocal(init:()=>T):void {
        val localState = state();
        val localStore = localState.store;
        localStore.clear(); // in case previously initialized
        localState.init = init;
    }

    /** 
     * Apply the given operation in parallel to each thread local value.
     */
    public def applyLocal(op:(t:T)=>void):void {
        val localStore = state().store;
        finish for (i in 0..(localStore.size-1)) {
            val t = localStore(i);
            if (t != null) {
                async op(t);
            }
        }
        return;
    }

    /** 
     * Reduce partial results from each thread using the init operation to
     * create the initial value, and return combined result.
     */
    public def reduceLocal(op:(a:T,b:T)=>T):T {
        val localState = state();
        val localStore = localState.store;
        var result:T = localState.init();
        for (i in 0..(localStore.size-1)) {
            val t = localStore(i);
            if (t != null) {
                result = op(result, t);
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
            { a.map(result, b, (x:Double,y:Double)=>(x+y) ) as Rail[Double] }
     *   );
     */
    public def reduceLocal(var result:T, op:(a:T,b:T)=>T) {
        val localState = state();
        val localStore = localState.store;
        for (i in 0..(localStore.size-1)) {
            val t = localStore(i);
            if (t != null) {
                result = op(result, t);
            }
        }
        return result;
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
