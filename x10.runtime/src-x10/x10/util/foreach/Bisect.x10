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
 * Implementation where the range is divided into two
 * approximately equal pieces, with each piece constituting an
 * activity. Bisection recurs until each activity is less than or
 * equal to a maximum grain size.
 */
public final class Bisect {

    /**
     * Iterate over a range of indices in parallel using recursive bisection.
     * @param range the range of the indices
     * @param grainSize the maximum grain size for an activity
     * @param body a closure that executes over a contiguous range of indices
     */
    public static @Inline operator for(range:LongRange,
                                       grainSize:Long,
                                       body:(range:LongRange)=>void) {
        if (Runtime.NTHREADS == 1n) {
            Sequential.operator for(range, body);
        } else {
            finish doBisect1D(range.min, range.max+1, grainSize, body);
        }
    }

    /**
     * Iterate over a range of indices in parallel using recursive
     * bisection. Bisection recurs until a minimum grain size of
     * (range.max-range.min+1) / (Runtime.NTHREADS &times; 8) is reached.
     * @param range the range of the indices
     * @param body a closure that executes over a contiguous range of indices
     */
    public static @Inline operator for(range:LongRange,
                                       body:(range:LongRange)=>void) {
        val grainSize = Math.max(1, (range.max-range.min) / (Runtime.NTHREADS*8));
        Bisect.operator for(range, grainSize, body);
    }

    /**
     * Iterate over a range of indices in parallel using recursive bisection.
     * @param range the iteration space
     * @param grainSize the maximum grain size for an activity
     * @param body a closure that executes over a single value of the index
     */
    public static @Inline operator for(range:LongRange,
                                       grainSize:Long,
                                       body:(i:Long)=>void) {
        if (Runtime.NTHREADS == 1n) {
            Sequential.operator for(range, body);
        } else {
            // convert single index closure into execution over range
            val executeRange = (range:LongRange) => {
                for (i in range) body(i);
            };
            finish doBisect1D(range.min, range.max+1, grainSize, executeRange);
        }
    }

    /**
     * Iterate over a range of indices in parallel using recursive
     * bisection. Bisection recurs until a minimum grain size of
     * (max-min+1) / (Runtime.NTHREADS &times; 8) is reached.
     * @param range the iteration space
     * @param body a closure that executes over a contiguous range of indices
     */
    public static @Inline operator for(range: LongRange,
                                       body:(i:Long)=>void) {
        val grainSize = Math.max(1, (range.max-range.min) / (Runtime.NTHREADS*8));
        Bisect.operator for(range, grainSize, body);
    }

    private static def doBisect1D(start:Long, end:Long,
                                  grainSize:Long,
                                  body:(range:LongRange)=>void) {
        if ((end-start) > grainSize) {
            async doBisect1D((start+end)/2L, end, grainSize, body);
            doBisect1D(start, (start+end)/2L, grainSize, body);
        } else {
            body(start..(end-1));
        }
    }

    /**
     * Reduce over a range of indices in parallel using recursive bisection.
     * @param range the iteration space
     * @param grainSize the maximum grain size for an activity
     * @param reduce the reduction operation
     * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
     * @param body a closure that executes over a single value of the index
     */
    public static @Inline operator for[T](range:LongRange,
                                          grainSize:Long,
                                          reduce:(a:T,b:T)=>T, identity:T,
                                          body:(i:Long)=>T):T {
        // convert single index closure into execution over range
        val executeRange = (range:LongRange) => {
            var myRes:T = identity;
            for (i in range) {
                myRes = reduce(myRes, body(i));
            }
            myRes
        };
        return Bisect.operator for(range, grainSize, reduce, executeRange);
    }

    /**
     * Reduce over a range of indices in parallel using recursive bisection.
     * @param range the range of the indices
     * @param grainSize the maximum grain size for an activity
     * @param body a closure that executes over a contiguous range of indices,
     *   returning the reduced value for that range
     * @param reduce the reduction operation
     */
    public static @Inline operator for[T](range:LongRange,
                                          grainSize:Long,
                                          reduce:(a:T,b:T)=>T,
                                          body:(range:LongRange)=>T):T {
        if (Runtime.NTHREADS == 1n) {
            return Sequential.operator for(range, reduce, body);
        } else {
            return doBisectReduce1D(range.min, range.max+1, grainSize, reduce, body);
        }
    }

    /**
     * Reduce over a range of indices in parallel using recursive
     * bisection.  Bisection recurs until a minimum grain * size of
     * (range.max-range.min+1) / (Runtime.NTHREADS &times; 8) is reached.
     * @param range the range of the indices
     * @param reduce the reduction operation
     * @param body a closure that executes over a contiguous range of indices,
     *   returning the reduced value for that range
     */
    public static @Inline operator for[T](range:LongRange,
                                          reduce:(a:T,b:T)=>T,
                                          body:(range:LongRange)=>T):T {
        val grainSize = Math.max(1, (range.max-range.min) / (Runtime.NTHREADS*8));
        return Bisect.operator for(range, grainSize, reduce, body);
    }

    /**
     * Reduce over a range of indices in parallel using recursive bisection.
     * @param range the iteration space
     * @param body a closure that executes over a contiguous range of indices
     */
    public static @Inline operator for[T](range:LongRange,
                                          reduce:(a:T,b:T)=>T, identity:T,
                                          body:(i:Long)=>T):T {
        val grainSize = Math.max(1, (range.max-range.min) / (Runtime.NTHREADS*8));
        return Bisect.operator for(range, grainSize, reduce, identity, body);
    }

    private static def doBisectReduce1D[T](start:Long, end:Long,
                                        grainSize:Long,
                                        reduce:(a:T,b:T)=>T,
                                        body:(range:LongRange)=>T):T {
        if ((end-start) > grainSize) {
            val asyncResult:T;
            val syncResult:T;
            finish {
                async asyncResult = doBisectReduce1D[T]((start+end)/2L, end, grainSize, reduce, body);
                syncResult = doBisectReduce1D[T](start, (start+end)/2L, grainSize, reduce, body);
            }
            return reduce(syncResult, asyncResult);
        } else {
            return body(start..(end-1));
        }
    }

    /**
     * Iterate over a dense rectangular set of indices in parallel using
     * two-dimensional recursive bisection. The index set is divided along the
     * largest dimension into two approximately equal pieces, with each piece
     * constituting an activity. Bisection recurs on each subblock until each
     * activity is smaller than or equal to a maximum grain size in each
     * dimension.
     * @param space the range of the indices
     * @param grainSize0 the maximum grain size for the first index dimension
     * @param grainSize1 the maximum grain size for the second index dimension
     * @param body a closure that executes over a rectangular block of indices
     */
    public static @Inline operator for(space:DenseIterationSpace_2,
                                       grainSize0:Long, grainSize1:Long,
                                       body:(space:DenseIterationSpace_2)=>void) {
        if (Runtime.NTHREADS == 1n) {
            body(space); // sequential
        } else {
            finish doBisect2D(space.min0, space.max0+1, space.min1, space.max1+1, grainSize0, grainSize1, body);
        }
    }

    /**
     * Iterate over a dense rectangular set of indices in parallel using
     * two-dimensional recursive bisection. The index set is divided along the
     * largest dimension into two approximately equal pieces, with each piece
     * constituting an activity. Bisection recurs on each subblock until each
     * activity is smaller than or equal to a grain size of
     * (max-min+1) / Runtime.NTHREADS in each dimension.
     * <p>TODO divide each dim by N ~= sqrt(Runtime.NTHREADS &times; 8), biased
     *   towards more divisions in longer dim
     * @param space the range of the indices
     * @param min1 the minimum value of the second index dimension
     * @param max1 the maximum value of the second index dimension
     * @param body a closure that executes over a rectangular block of indices
     */
    public static @Inline operator for (space:DenseIterationSpace_2,
                                        body:(space:DenseIterationSpace_2)=>void) {
        val grainSize0 = Math.max(1, (space.max0-space.min0) / Runtime.NTHREADS);
        val grainSize1 = Math.max(1, (space.max1-space.min1) / Runtime.NTHREADS);
        Bisect.operator for(space, grainSize0, grainSize1, body);
    }

    /**
     * Iterate over a dense rectangular set of indices in parallel using
     * two-dimensional recursive bisection. The index set is divided along the
     * largest dimension into two approximately equal pieces, with each piece
     * constituting an activity. Bisection recurs on each subblock until each
     * activity is smaller than or equal to a maximum grain size in each
     * dimension.
     * @param space the 2D dense space over which to iterate
     * @param grainSize0 the maximum grain size for the first index dimension
     * @param grainSize1 the maximum grain size for the second index dimension
     * @param body a closure that executes over a single index [i,j]
     */
    public static @Inline operator for (space:DenseIterationSpace_2,
                                        grainSize0:Long, grainSize1:Long,
                                        body:(i:Long, j:Long)=>void) {
        // convert single index closure into execution over range
        val executeRange = (space:DenseIterationSpace_2) => {
            for ([i, j] in space) {
                body(i, j);
            }
        };
        Bisect.operator for(space, grainSize0, grainSize1, executeRange);
    }

    /**
     * Iterate over a dense rectangular set of indices in parallel using
     * two-dimensional recursive bisection. The index set is divided along the
     * largest dimension into two approximately equal pieces, with each piece
     * constituting an activity. Bisection recurs on each subblock until each
     * activity is smaller than or equal to a grain size of
     * (max-min+1) / Runtime.NTHREADS in each dimension.
     * <p>TODO divide each dim by N ~= sqrt(Runtime.NTHREADS &times; 8), biased
     *   towards more divisions in longer dim
     * @param space the 2D dense space over which to iterate
     * @param body a closure that executes over a single index [i,j]
     */
    public static @Inline operator for (space:DenseIterationSpace_2,
                                        body:(i:Long, j:Long)=>void) {
        val grainSize0 = Math.max(1, (space.max0-space.min0) / Runtime.NTHREADS);
        val grainSize1 = Math.max(1, (space.max1-space.min1) / Runtime.NTHREADS);
        Bisect.operator for (space, grainSize0, grainSize1, body);
    }

    /**
     * Perform a parallel iteration using recursive bisection.
     * Do not wait for termination.
     */
    private static def doBisect2D(s0:Long, e0:Long,
                                  s1:Long, e1:Long,
                                  g1:Long, g2:Long,
                                  body:(space:DenseIterationSpace_2)=>void) {
        if ((e0-s0) > g1 && ((e0-s0) >= (e1-s1) || (e1-s1) <= g2)) {
            async doBisect2D((s0+e0)/2L, e0, s1, e1, g1, g2, body);
            doBisect2D(s0, (s0+e0)/2L, s1, e1, g1, g2, body);
        } else if ((e1-s1) > g2) {
            async doBisect2D(s0, e0, (s1+e1)/2L, e1, g1, g2, body);
            doBisect2D(s0, e0, s1, (s1+e1)/2L, g1, g2, body);
        } else {
            body(s0..(e0-1) * s1..(e1-1));
        }
    }

    /**
     * Reduce over a dense rectangular set of indices in parallel using
     * two-dimensional recursive bisection. The index set is divided along the
     * largest dimension into two approximately equal pieces, with each piece
     * constituting an activity. Bisection recurs on each subblock until each
     * activity is smaller than or equal to a maximum grain size in each
     * dimension.
     * @param space the 2D dense space over which to iterate
     * @param grainSize0 the maximum grain size for the first index dimension
     * @param grainSize1 the maximum grain size for the second index dimension
     * @param reduce the reduction operation
     * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
     * @param body a closure that executes over a single index [i,j]
     */
    public static @Inline operator for[T](space:DenseIterationSpace_2,
                                          grainSize0:Long, grainSize1:Long,
                                          reduce:(a:T,b:T)=>T, identity:T,
                                          body:(i:Long, j:Long)=>T):T {
        if (Runtime.NTHREADS == 1n) {
            return Sequential.operator for(space, reduce, identity, body);
        } else {
            // convert single index closure into execution over range
            val reduceRange = (min0:Long, max0:Long, min1:Long, max1:Long) => {
                var myResult:T = identity;
                for (i in min0..max0) {
                    for (j in min1..max1) {
                        myResult = reduce(myResult, body(i, j));
                    }
                }
                myResult
            };
            return doBisectReduce2D(space.min0, space.max0+1, space.min1, space.max1+1, grainSize0, grainSize1, reduce, reduceRange);
        }
    }

    /**
     * Reduce over a dense rectangular set of indices in parallel using
     * two-dimensional recursive bisection. The index set is divided along the
     * largest dimension into two approximately equal pieces, with each piece
     * constituting an activity. Bisection recurs on each subblock until each
     * activity is smaller than or equal to a maximum grain size in each
     * dimension.
     * @param space the 2D dense space over which to reduce
     * @param reduce the reduction operation
     * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
     * @param body a closure that executes over a single index [i,j]
     */
    public static @Inline operator for[T](space:DenseIterationSpace_2,
                                          reduce:(a:T,b:T)=>T, identity:T,
                                          body:(i:Long, j:Long)=>T):T {
        val grainSize0 = Math.max(1, (space.max0-space.min0) / Runtime.NTHREADS);
        val grainSize1 = Math.max(1, (space.max1-space.min1) / Runtime.NTHREADS);
        return Bisect.operator for(space, grainSize0, grainSize1, reduce, identity, body);
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
         * Reduce over a range of indices in parallel using recursive bisection.
         * @param range the iteration space
         * @param body a closure that executes over a single value of the index
         */
        public final @Inline operator for(range:LongRange,
                                          body:(i:Long)=>T):T{
            val res = Bisect.operator for(range, this.reduce, this.identity, body);
            result = new Cell[T](res);
            return res;
        }

        /**
         * Reduce over a dense rectangular set of indices in parallel using
         * two-dimensional recursive bisection.
         * @param space the 2D dense space over which to reduce
         * @param body a closure that executes over a single index [i,j]
         */
        public final @Inline operator for(space:DenseIterationSpace_2,
                                          body:(i:Long, j:Long)=>T):T{
            val res = Bisect.operator for(space, this.reduce, this.identity, body);
            result = new Cell[T](res);
            return res;
        }

    }

    /**
     * Perform a parallel reduction using recursive bisection.
     * Results of each branch are reduced together at each level of the tree.
     * Wait for termination of all nested asyncs.
     */
    private static def doBisectReduce2D[T](s0:Long, e0:Long,
                                           s1:Long, e1:Long,
                                           g1:Long, g2:Long,
                                           reduce:(a:T,b:T)=>T,
                                           body:(min_i1:Long, max_i1:Long, min_i2:Long, max_i2:Long)=>T):T {
        if ((e0-s0) > g1 && ((e0-s0) >= (e1-s1) || (e1-s1) <= g2)) {
            val asyncResult:T;
            val syncResult:T;
            finish {
                async asyncResult = doBisectReduce2D[T]((s0+e0)/2L, e0, s1, e1, g1, g2, reduce, body);
                syncResult = doBisectReduce2D[T](s0, (s0+e0)/2L, s1, e1, g1, g2, reduce, body);
            }
            return reduce(asyncResult, syncResult);
        } else if ((e1-s1) > g2) {
            val asyncResult:T;
            val syncResult:T;
            finish {
                async asyncResult = doBisectReduce2D[T](s0, e0, (s1+e1)/2L, e1, g1, g2, reduce, body);
                syncResult = doBisectReduce2D[T](s0, e0, s1, (s1+e1)/2L, g1, g2, reduce, body);
            }
            return reduce(asyncResult, syncResult);
        } else {
            return body(s0, e0-1, s1, e1-1);
        }
    }

    public static final class GrainSize {

        val grainSize: Long;
        public def this(grainSize:Long) {
            this.grainSize = grainSize;
        }

        public final @Inline operator for (range: LongRange, body:(Long)=>void) {
            Bisect.operator for (range,this.grainSize,body);
        }

    }

}
