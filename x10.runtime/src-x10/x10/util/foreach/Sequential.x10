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
 * Implementation where iterations are executes in sequence. This
 * may be used for debugging purposes or when there is no benefit
 * to be gained from parallelizing an iteration.
 */
public final class Sequential {
    /**
     * Iterate over a range of indices in sequence in a single activity.
     * @param range the iteration space
     * @param body a closure that executes over a single value of the index
     */
    public static @Inline operator for(range: LongRange,
                                       body:(i:Long)=>void) {
        for (i in range) body(i);
    }

    /**
     * Iterate over a dense rectangular block of indices in single sequence in a
     * single activity.
     * @param space the 2D dense space over which to iterate
     * @param body a closure that executes over a single index [i,j]
     */
    public static @Inline operator for(space:DenseIterationSpace_2,
                                       body:(i:Long, j:Long)=>void) {
        for ([i, j] in space) {
                body(i, j);
        }
    }

    /**
     * Execute the closure that iterates over a range of
     * indices. A single activity executes the closure.
     * @param range the range of the indices
     * @param body a closure that executes over a contiguous range of indices
     */
    public static @Inline operator for(range:LongRange,
                                       body:(range:LongRange)=>void) {
        body(range);
    }


    /**
     * Reduce over a range of indices in sequence in a single activity.
     * @param range the iteration space
     * @param reduce the reduction operation
     * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
     * @param body a closure that executes over a single value of the index
     */
    public static @Inline operator for[T](range:LongRange,
                                          reduce:(a:T,b:T)=>T, identity:T,
                                          body:(i:Long)=>T):T {
        var myRes:T = identity;
        for (i in range) {
            myRes = reduce(myRes, body(i));
        }
        return myRes;
    }

    /**
     * Reduce over a range of indices in sequence in a single activity.
     * @param space the 2D dense space over which to reduce
     * @param reduce the reduction operation
     * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
     * @param body a closure that executes over a single index [i,j]
     */
    public static @Inline operator for[T](space:DenseIterationSpace_2,
                                          reduce:(a:T,b:T)=>T, identity:T,
                                          body:(i:Long,j:Long)=>T):T{
        var myRes:T = identity;
        for ([i, j] in space) {
            myRes = reduce(myRes, body(i, j));
        }
        return myRes;
    }


    /**
     * Reduce over a range of indices in sequence in a single activity.
     * @param range the range of the indices
     * @param reduce the reduction operation
     * @param body a closure that executes over a contiguous range of indices,
     *   returning the reduced value for that range
     */
    public static @Inline operator for[T](range:LongRange,
                                          reduce:(a:T,b:T)=>T,
                                          body:(range:LongRange)=>T):T{
        return body(range);
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
         * Reduce over a range of indices in sequence in a single activity.
         * @param range the iteration space
         * @param body a closure that executes over a single value of the index
         */
        public final @Inline operator for(range:LongRange,
                                          body:(i:Long)=>T):T{
            val res = Sequential.operator for(range, this.reduce, this.identity, body);
            result = new Cell[T](res);
            return res;
        }

        /**
         * Reduce over a range of indices in sequence in a single activity.
         * @param space the 2D dense space over which to reduce
         * @param body a closure that executes over a single index [i,j]
         */
        public final @Inline operator for(space:DenseIterationSpace_2,
                                          body:(i:Long, j:Long)=>T):T{
            val res = Sequential.operator for(space, this.reduce, this.identity, body);
            result = new Cell[T](res);
            return res;
        }

    }

}
