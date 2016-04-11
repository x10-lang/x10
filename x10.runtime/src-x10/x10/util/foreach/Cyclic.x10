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

package x10.util.foreach;

import x10.array.BlockingUtils;
import x10.array.DenseIterationSpace_2;

import x10.xrx.Runtime;
import x10.compiler.Inline;

/**
 * Implementation where <code>Runtime.NTHREADS</code> activities are
 * created, one for each worker thread.  Given T=Runtime.NTHREADS,
 * activity for thread number <code>t</code> executes iterations
 * (t, t+T, t+2 &times; T, ...).
 */
public final class Cyclic {

    /**
     * Iterate over a range of indices in parallel using a cyclic decomposition.
     * @param range the iteration space
     * @param body a closure that executes over a single value of the index
     */
    public static @Inline operator for(range: LongRange,
                                       body:(i:Long)=>void) {
        if (Runtime.NTHREADS == 1n) {
            Sequential.operator for(range, body);
        } else {
            val min = range.min;
            val max= range.max;
            finish for (t in 0..(Runtime.NTHREADS-1)) async {
                    for (var i:Long = min+t; i <= max; i += Runtime.NTHREADS) {
                        body(i);
                    }
                }
        }
    }

    /**
     * Reduce over a range of indices in parallel using a cyclic decomposition.
     * @param range the iteration space
     * @param body a closure that executes over a single value of the index
     * @param reduce the reduction operation
     * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
     */
    public static @Inline operator for[T](range:LongRange,
                                          reduce:(a:T,b:T)=>T, identity:T,
                                          body:(i:Long)=>T):T {
        if (Runtime.NTHREADS == 1n) {
            return Sequential.operator for(range, reduce, identity, body);
        } else {
            val min = range.min;
            val max= range.max;
            val results = Unsafe.allocRailUninitialized[T](Runtime.NTHREADS);
            finish for (t in 0..(Runtime.NTHREADS-1)) async {
                    var myRes:T = identity;
                    for (var i:Long = min+t; i <= max; i += Runtime.NTHREADS) {
                        myRes = reduce(myRes, body(i));
                    }
                    results(t) = myRes;
                }
            var res:T = results(0);
            for (myT in 1..(Runtime.NTHREADS-1)) {
                res = reduce(res, results(myT));
            }
            return res;
        }
    }


    /**
     * 'for' syntax for reduce methods.
     */
    public static final class Reducer[T] {
        private var result: Cell[T] = null;

        /**
         * The reduction operation.
         */
        public val reduce: (T, T) => T;

        /**
         * The identity value for the reduction operation such that reduce(identity,f)=f.
         */
        public val identity: T;

        /**
         * Access to the result of the last reduction. It may
         * raise <code>ReduceNotReady</code> if no result has been
         * computed yet.
         */
        public final @Inline def value () throws ReduceNotReady {
            if (this.result == null) { throw new ReduceNotReady(); }
            return this.result();
        }

        /**
         * Constructor for collecting loop with reducer.
         * @param reduce the reduction operation
         * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
         */
        public def this(reduce: (T, T) => T, identity: T){
            this.reduce = reduce;
            this.identity = identity;
        }

        /**
         * Constructor for collecting loop with reducer.
         * @param red the reduction operation
         */
        public def this(red: Reducible[T]){
            this.reduce = ((a:T,b:T) => red(a,b));
            this.identity = red.zero();
        }

        /**
         * Reduce over a range of indices in parallel using a cyclic decomposition.
         * @param range the iteration space
         * @param body a closure that executes over a single value of the index
         */
        public final @Inline operator for(range:LongRange,
                                          body:(i:Long)=>T):T{
            val res = Cyclic.operator for(range, this.reduce, this.identity, body);
            result = new Cell[T](res);
            return res;
        }
    }
}
