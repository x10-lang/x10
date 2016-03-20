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
 * Implementation where <code>Runtime.NTHREADS</code> activities
 * are created, one for each worker thread. Each activity executes
 * sequentially over all indices in a contiguous block, and the
 * blocks are of approximately equal size.
 */
public final class Block {

    /**
     * Execute the closure that iterates over a range of
     * indices. Several activities execute in parallel the closure
     * on a slice of the indices using a block decomposition.
     * @param range the range of the indices
     * @param body a closure that executes over a contiguous range of indices
     */
    public static @Inline operator for(range:LongRange,
                                       body:(range:LongRange)=>void) {
        val nthreads = Runtime.NTHREADS;
        if (nthreads == 1n) {
            Sequential.operator for(range, body);
        } else {
            val min = range.min;
            val max = range.max;
            val numElems = max - min + 1;
            if (numElems < 1) return;
            val blockSize = numElems/nthreads;
            val leftOver = numElems - nthreads*blockSize;
            finish for (var t:Long = nthreads-1; t >= 0; t--) {
                val myT = t;
                async {
                    val lo = min + blockSize*myT + (myT < leftOver ? myT : leftOver);
                    val hi = lo + blockSize + (myT < leftOver ? 0 : -1);
                    body(lo..hi);
                }
            }
        }
    }

    /**
     * Iterate over a range of indices in parallel using a block decomposition.
     * @param range the iteration space
     * @param body a closure that executes over a single value of the index
     */
    public static @Inline operator for(range: LongRange,
                                       body:(i:Long)=>void) {
        val executeRange = (range:LongRange)=> { Sequential.operator for(range, body); };
        Block.operator for(range, executeRange);
    }


    /**
     * Iterate over a dense rectangular block of indices in parallel using
     * a block decomposition.
     * @param space the 2D dense space over which to iterate
     * @param body a closure that executes over a single index [i,j]
     */
    public static @Inline operator for(space:DenseIterationSpace_2,
                                       body:(i:Long, j:Long)=>void) {
         if (Runtime.NTHREADS == 1n) {
             Sequential.operator for(space, body);
         } else {
             finish for (var t:Long = Runtime.NTHREADS-1; t >= 0; t--) {
                 val myT = t;
                 async {
                     val block = BlockingUtils.partitionBlockBlock(space, Runtime.NTHREADS, myT);
                     Sequential.operator for(block, body);
                 }
             }
         }
     }

    /**
     * Reduce over a range of indices in parallel using a block decomposition.
     * @param range the iteration space
     * @param reduce the reduction operation
     * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
     * @param body a closure that executes over a single value of the index
     */
    public static @Inline operator for[T](range:LongRange,
                                          reduce:(a:T,b:T)=>T, identity:T,
                                          body:(i:Long)=>T):T{
        val executeRange = (range:LongRange) => {
            var myRes:T = identity;
            for (i in range) {
                myRes = reduce(myRes, body(i));
            }
            myRes
        };
        return Block.operator for(range, reduce, executeRange);
    }

    /**
     * Reduce over a range of indices in parallel using a block decomposition.
     * @param space the 2D dense space over which to reduce
     * @param reduce the reduction operation
     * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
     * @param body a closure that executes over a single index [i,j]
     */
    public static @Inline operator for[T](space:DenseIterationSpace_2,
                                          reduce:(a:T,b:T)=>T, identity:T,
                                          body:(i:Long,j:Long)=>T):T {
        if (Runtime.NTHREADS == 1n) {
            return  Sequential.operator for(space, reduce, identity, body);
        } else {
            val results = Unsafe.allocRailUninitialized[T](Runtime.NTHREADS);
            finish for (var t:Long = Runtime.NTHREADS-1; t >= 0; t--) {
                val myT = t;
                val block = BlockingUtils.partitionBlockBlock(space, Runtime.NTHREADS, myT);
                async results(myT) = Sequential.operator for(block, reduce, identity, body);
            }
            var res:T = results(0);
            for (myT in 1..(Runtime.NTHREADS-1)) {
                res = reduce(res, results(myT));
            }
            return res;
        }
    }

    /**
     * Reduce over a range of indices in parallel using a block decomposition.
     * @param range the range of the indices
     * @param reduce the reduction operation
     * @param body a closure that executes over a contiguous range of indices,
     *   returning the reduced value for that range
     */
    public static @Inline operator for[T](range:LongRange,
                                          reduce:(a:T,b:T)=>T,
                                          body:(range:LongRange)=>T):T{
        val nthreads = Runtime.NTHREADS;
        if (nthreads == 1n) {
            return body(range); // sequential
        } else {
            val min = range.min;
            val max = range.max;
            val numElems = max - min + 1;
            if (numElems < 1) return body(range);
            val blockSize = numElems/nthreads;
            val leftOver = numElems - nthreads*blockSize;
            val results = Unsafe.allocRailUninitialized[T](nthreads);
            finish for (var t:Long = nthreads-1; t >= 0; t--) {
                val myT = t;
                async {
                    val lo = min + blockSize*myT + (myT < leftOver ? myT : leftOver);
                    val hi = lo + blockSize + (myT < leftOver ? 0 : -1);
                    results(myT) = body(lo..hi);
                }
            }
            var res:T = results(0);
            for (myT in 1..(nthreads-1)) {
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
         * Reduce over a range of indices in parallel using a block decomposition.
         * @param range the iteration space
         * @param body a closure that executes over a single value of the index
         */
        public final @Inline operator for(range:LongRange,
                                          body:(i:Long)=>T):T{
            val res = Block.operator for(range, this.reduce, this.identity, body);
            result = new Cell[T](res);
            return res;
        }

        /**
         * Reduce over a range of indices in parallel using a block decomposition.
         * @param space the 2D dense space over which to reduce
         * @param body a closure that executes over a single index [i,j]
         */
        public final @Inline operator for(space:DenseIterationSpace_2,
                                          body:(i:Long, j:Long)=>T):T{
            val res = Block.operator for(space, this.reduce, this.identity, body);
            result = new Cell[T](res);
            return res;
        }
    }
}
