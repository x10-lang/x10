/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 */

package x10.compiler;

import x10.array.BlockingUtils;
import x10.array.DenseIterationSpace_2;

import x10.xrx.Runtime;

/**
 * Parallel iteration over a set of indices using different patterns of
 * local activity creation; this is intended to be used by the compiler to
 * implement the <code>foreach</code> construct.
 * <p>
 * The <code>body</code> closure is executed for each value of the index, 
 * making use of available local parallelism. The iteration must be both 
 * <em>serializable</em> and <em>parallelizable</em>; in other words, it is 
 * correct to execute <code>body</code> for each index in sequence, and it is 
 * also correct to execute <code>body</code> in parallel for any subset of 
 * indices.</p>
 * <p>There is an implied <code>finish</code> i.e. all iterations must complete
 * before <code>foreach</code> is complete.</p>
 * <p>Restrictions:</p>
 * <ul>
 * <li>A conditional atomic statement (<code>when</code>) may not be included 
 * as it could introduce ordering dependencies. Unconditional <code>atomic</code>
 * may be included as it cannot create an ordering dependency.</li>
 * <li>Changing place with <code>at</code> is not recommended as it may introduce
 * arbitrary delays.</p>
 * </ul>
 */
public final class Foreach {
    /**
     * Iterate over a range of indices in sequence in a single activity.
     * This may be used for debugging purposes or as a compiler target where
     * there is no benefit to be gained from parallelizing an iteration.
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param body a closure that executes over a single value of the index
     */
    public static @Inline def sequential(min:Long, max:Long,
                                    body:(i:Long)=>void) {
        for (i in min..max) body(i);
    }

    /**
     * Iterate over a range of indices in sequence in a single activity.
     * @param min0 the minimum value of the first index dimension
     * @param max0 the maximum value of the first index dimension
     * @param min1 the minimum value of the second index dimension
     * @param max1 the maximum value of the second index dimension
     * @param body a closure that executes over a single value of the index [i,j]
     */
    public static @Inline def sequential(min0:Long, max0:Long,
                                    min1:Long, max1:Long,
                                    body:(i:Long, j:Long)=>void) {
        for (i in min0..max0) {
            for (j in min1..max1) {
                body(i, j);
            }
        }
    }

    /**
     * Iterate over a range of indices in sequence in a single activity.
     * This may be used for debugging purposes or as a compiler target where
     * there is no benefit to be gained from parallelizing an iteration.
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param body a closure that executes over a contiguous range of indices
     */
    public static @Inline def sequential(min:Long, max:Long,
                                    body:(min:Long, max:Long)=>void) {
        body(min, max);
    }

    /**
     * Iterate over a dense rectangular block of indices in single sequence in a
     * single activity.
     * @param space the 2D dense space over which to iterate
     * @param body a closure that executes over a single index [i,j]
     */
    public static @Inline def sequential(space:DenseIterationSpace_2,
                                    body:(i:Long, j:Long)=>void) {
        Foreach.sequential(space.min0, space.max0, space.min1, space.max1, body);
    }

    /**
     * Reduce over a range of indices in sequence in a single activity.
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param body a closure that executes over a single value of the index
     * @param reduce the reduction operation
     * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
     */
    public static @Inline def sequentialReduce[T](min:Long, max:Long,
                                    body:(i:Long)=>T,
                                    reduce:(a:T,b:T)=>T, identity:T):T{
        var myRes:T = identity;
        for (i in min..max) {
            myRes = reduce(myRes, body(i));
        }
        return myRes;
    }

    /**
     * Reduce over a range of indices in sequence in a single activity.
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param body a closure that executes over a contiguous range of indices, 
     *   returning the reduced value for that range
     * @param reduce the reduction operation
     */
    public static @Inline def sequentialReduce[T](min:Long, max:Long,
                                    body:(min:Long, max:Long)=>T,
                                    reduce:(a:T,b:T)=>T):T{
        return body(min, max);
    }

    /**
     * Reduce over a range of indices in sequence in a single activity.
     * @param space the 2D dense space over which to reduce
     * @param body a closure that executes over a single index [i,j]
     * @param reduce the reduction operation
     * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
     */
    public static @Inline def sequentialReduce[T](space:DenseIterationSpace_2,
                                    body:(i:Long, j:Long)=>T,
                                    reduce:(a:T,b:T)=>T, identity:T):T{
        var myRes:T = identity;
        for (i in space.min0..space.max0) {
            for (j in space.min1..space.max1) {
                myRes = reduce(myRes, body(i, j));
            }
        }
        return myRes;
    }

    /**
     * Iterate over a range of indices in parallel using a basic async
     * transformation. A separate async is started for every index in min..max
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param body a closure that executes over a single value of the index
     */
    public static @Inline def basic(min:Long, max:Long,
                                    body:(i:Long)=>void) {
        if (Runtime.NTHREADS == 1n)
            sequential(min, max, body);
        else
            finish for (i in min..max) async body(i);
    }

    /**
     * Iterate over a range of indices in parallel using a basic async
     * transformation. A separate async is started for every index in min..max
     * @param min0 the minimum value of the first index dimension
     * @param max0 the maximum value of the first index dimension
     * @param min1 the minimum value of the second index dimension
     * @param max1 the maximum value of the second index dimension
     * @param body a closure that executes over a single value of the index [i,j]
     */
    public static @Inline def basic(min0:Long, max0:Long,
                                    min1:Long, max1:Long,
                                    body:(i:Long, j:Long)=>void) {
        if (Runtime.NTHREADS == 1n) {
            sequential(min0, max0, min1, max1, body);
        } else {
            finish for (i in min0..max0) {
                for (j in min1..max1) {
                    async body(i, j);
                }
            }
        }
    }

    /**
     * Iterate over a range of indices in parallel using a block decomposition.
     * <code>Runtime.NTHREADS</code> activities are created, one for each
     * worker thread.  Each activity executes sequentially over all indices in
     * a contiguous block, and the blocks are of approximately equal size.
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param body a closure that executes over a contiguous range of indices
     */
    public static @Inline def block(min:Long, max:Long,
                                    body:(min:Long, max:Long)=>void) {
        val nthreads = Runtime.NTHREADS;
        if (nthreads == 1n) {
            sequential(min, max, body);
        } else {
            val numElems = max - min + 1;
	        if (numElems < 1) return;
            val blockSize = numElems/nthreads;
            val leftOver = numElems - nthreads*blockSize;
            finish for (var t:Long = nthreads-1; t >= 0; t--) {
                val myT = t;
                async {
                    val lo = min + blockSize*myT + (myT < leftOver ? myT : leftOver);
                    val hi = lo + blockSize + (myT < leftOver ? 0 : -1);
                    body(lo, hi);
                }
            }
        }
    }

    /**
     * Iterate over a range of indices in parallel using a block decomposition.
     * <code>Runtime.NTHREADS</code> activities are created, one for each
     * worker thread.  Each activity executes sequentially over all indices in
     * a contiguous block, and the blocks are of approximately equal size.
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param body a closure that executes over a single value of the index
     */
    public static @Inline def block(min:Long, max:Long,
                                    body:(i:Long)=>void) {
        val executeRange = (start:Long, end:Long)=> { sequential(start, end, body); };
        Foreach.block(min, max, executeRange);
    }

    /**
     * Reduce over a range of indices in parallel using a block decomposition.
     * <code>Runtime.NTHREADS</code> activities are created, one for each
     * worker thread.  Each activity executes sequentially over all indices in
     * a contiguous block, and the blocks are of approximately equal size.
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param body a closure that executes over a contiguous range of indices, 
     *   returning the reduced value for that range
     * @param reduce the reduction operation
     */
    public static @Inline def blockReduce[T](min:Long, max:Long,
                                    body:(min:Long, max:Long)=>T,
                                    reduce:(a:T,b:T)=>T):T{
        val nthreads = Runtime.NTHREADS;
        if (nthreads == 1n) {
            return body(min, max); // sequential
        } else {
            val numElems = max - min + 1;
	        if (numElems < 1) return body(min, max);
            val blockSize = numElems/nthreads;
            val leftOver = numElems - nthreads*blockSize;
            val results = Unsafe.allocRailUninitialized[T](nthreads);
            finish for (var t:Long = nthreads-1; t >= 0; t--) {
                val myT = t;
                async {
                    val lo = min + blockSize*myT + (myT < leftOver ? myT : leftOver);
                    val hi = lo + blockSize + (myT < leftOver ? 0 : -1);
                    results(myT) = body(lo, hi);
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
     * Reduce over a range of indices in parallel using a block decomposition.
     * <code>Runtime.NTHREADS</code> activities are created, one for each
     * worker thread.  Each activity executes sequentially over all indices in
     * a contiguous block, and the blocks are of approximately equal size.
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param body a closure that executes over a single value of the index
     * @param reduce the reduction operation
     * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
     */
    public static @Inline def blockReduce[T](min:Long, max:Long,
                                    body:(i:Long)=>T,
                                    reduce:(a:T,b:T)=>T, identity:T):T{
        val executeRange = (start:Long, end:Long) => {
            var myRes:T = identity;
            for (i in start..end) {
                myRes = reduce(myRes, body(i));
            }
            myRes
        };

        return Foreach.blockReduce(min, max, executeRange, reduce);
    }

    /**
     * Iterate over a range of indices in parallel using a block decomposition.
     * <code>Runtime.NTHREADS</code> activities are created, one for each
     * worker thread.  Each activity executes sequentially over all indices in
     * a contiguous block, and the blocks are of approximately equal size.
     * @param min0 the minimum value of the first index dimension
     * @param max0 the maximum value of the first index dimension
     * @param min1 the minimum value of the second index dimension
     * @param max1 the maximum value of the second index dimension
     * @param body a closure that executes over a single index [i,j]
     */
    public static @Inline def block(min0:Long, max0:Long,
                                    min1:Long, max1:Long,
                                    body:(i:Long, j:Long)=>void) {
        Foreach.block(new DenseIterationSpace_2(min0, min1, max0, max1), body);
    }

    /**
     * Iterate over a dense rectangular block of indices in parallel using
     * a block decomposition.
     * <code>Runtime.NTHREADS</code> activities are created, one for each
     * worker thread.  Each activity executes sequentially over all indices in
     * a contiguous block, and the blocks are of approximately equal size.
     * @param space the 2D dense space over which to iterate
     * @param body a closure that executes over a single index [i,j]
     */
    public static @Inline def block(space:DenseIterationSpace_2,
                                    body:(i:Long, j:Long)=>void) {
        if (Runtime.NTHREADS == 1n) {
            sequential(space, body);
        } else {
            finish for (var t:Long = Runtime.NTHREADS-1; t >= 0; t--) {
                val myT = t;
                async {
                    val block = BlockingUtils.partitionBlockBlock(space, Runtime.NTHREADS, myT);
                    Foreach.sequential(block, body);
                }
            }
        }
    }

    /**
     * Reduce over a range of indices in parallel using a block decomposition.
     * <code>Runtime.NTHREADS</code> activities are created, one for each
     * worker thread.  Each activity executes sequentially over all indices in
     * a contiguous block, and the blocks are of approximately equal size.
     * @param space the 2D dense space over which to reduce
     * @param body a closure that executes over a single index [i,j]
     * @param reduce the reduction operation
     * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
     */
    public static @Inline def blockReduce[T](space:DenseIterationSpace_2,
                                    body:(i:Long, j:Long)=>T,
                                    reduce:(a:T,b:T)=>T, identity:T):T{
        if (Runtime.NTHREADS == 1n) {
            return sequentialReduce(space, body, reduce, identity);
        } else {
            val results = Unsafe.allocRailUninitialized[T](Runtime.NTHREADS);
            finish for (var t:Long = Runtime.NTHREADS-1; t >= 0; t--) {
                val myT = t;
                val block = BlockingUtils.partitionBlockBlock(space, Runtime.NTHREADS, myT);
                async results(myT) = Foreach.sequentialReduce(block, body, reduce, identity);
            }
            var res:T = results(0);
            for (myT in 1..(Runtime.NTHREADS-1)) {
                res = reduce(res, results(myT));
            }
            return res;
        }
    }

    /**
     * Iterate over a range of indices in parallel using a cyclic decomposition.
     * <code>Runtime.NTHREADS</code> activities are created, one for each
     * worker thread.  Given T=Runtime.NTHREADS, activity for thread number 
     * <code>t</code> executes iterations (t, t+T, t+2 &times; T, ...).
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param body a closure that executes over a single value of the index
     */
    public static @Inline def cyclic(min:Long, max:Long,
                                     body:(i:Long)=>void) {
        if (Runtime.NTHREADS == 1n) {
            sequential(min, max, body);
        } else {
            finish for (t in 0..(Runtime.NTHREADS-1)) async {
                for (var i:Long = min+t; i <= max; i += Runtime.NTHREADS) {
                    body(i);
                }
            }
        }
    }

    /**
     * Reduce over a range of indices in parallel using a cyclic decomposition.
     * <code>Runtime.NTHREADS</code> activities are created, one for each
     * worker thread.  Given T=Runtime.NTHREADS, activity for thread number 
     * <code>t</code> executes iterations (t, t+T, t+2 &times; T, ...).
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param body a closure that executes over a single value of the index
     * @param reduce the reduction operation
     * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
     */
    public static @Inline def cyclicReduce[T](min:Long, max:Long,
                                    body:(i:Long)=>T,
                                    reduce:(a:T,b:T)=>T, identity:T):T{
        if (Runtime.NTHREADS == 1n) {
            return sequentialReduce(min, max, body, reduce, identity);
        } else {
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
     * Iterate over a range of indices in parallel using recursive bisection.
     * The range is divided into two approximately equal pieces, with each 
     * piece constituting an activity. Bisection recurs until each activity is
     * less than or equal to a maximum grain size.
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param grainSize the maximum grain size for an activity
     * @param body a closure that executes over a contiguous range of indices
     */
    public static @Inline def bisect(min:Long, max:Long,
                                     grainSize:Long,
                                     body:(min:Long, max:Long)=>void) {
        if (Runtime.NTHREADS == 1n) {
            sequential(min, max, body);
        } else {
            finish doBisect1D(min, max+1, grainSize, body);
        }
    }

    /**
     * Iterate over a range of indices in parallel using recursive bisection.
     * The range is divided into two approximately equal pieces, with each 
     * piece constituting an activity. Bisection recurs until a minimum grain
     * size of (max-min+1) / (Runtime.NTHREADS &times; 8) is reached.
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param body a closure that executes over a contiguous range of indices
     */
    public static @Inline def bisect(min:Long, max:Long,
                                     body:(min:Long, max:Long)=>void) {
        val grainSize = Math.max(1, (max-min) / (Runtime.NTHREADS*8));
        Foreach.bisect(min, max, grainSize, body);
    }

    /**
     * Iterate over a range of indices in parallel using recursive bisection.
     * The range is divided into two approximately equal pieces, with each 
     * piece constituting an activity. Bisection recurs until each activity is
     * less than or equal to a maximum grain size.
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param grainSize the maximum grain size for an activity
     * @param body a closure that executes over a single value of the index
     */
    public static @Inline def bisect(min:Long, max:Long,
                                     grainSize:Long,
                                     body:(i:Long)=>void) {
        if (Runtime.NTHREADS == 1n) {
            sequential(min, max, body);
        } else {
            // convert single index closure into execution over range
            val executeRange = (start:Long, end:Long) => {
                for (i in start..end) body(i);
            };
            finish doBisect1D(min, max+1, grainSize, executeRange);
        }
    }

    /**
     * Iterate over a range of indices in parallel using recursive bisection.
     * The range is divided into two approximately equal pieces, with each 
     * piece constituting an activity. Bisection recurs until a minimum grain
     * size of (max-min+1) / (Runtime.NTHREADS &times; 8) is reached.
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param body a closure that executes over a contiguous range of indices
     */
    public static @Inline def bisect(min:Long, max:Long,
                                     body:(i:Long)=>void) {
        val grainSize = Math.max(1, (max-min) / (Runtime.NTHREADS*8));
        Foreach.bisect(min, max, grainSize, body);
    }

    private static def doBisect1D(start:Long, end:Long,
                                  grainSize:Long,
                                  body:(min:Long, max:Long)=>void) {
        if ((end-start) > grainSize) {
            async doBisect1D((start+end)/2L, end, grainSize, body);
            doBisect1D(start, (start+end)/2L, grainSize, body);
        } else {
            body(start, end-1);
        }
    }

    /**
     * Reduce over a range of indices in parallel using recursive bisection.
     * The range is divided into two approximately equal pieces, with each 
     * piece constituting an activity. Bisection recurs until each activity is
     * less than or equal to a maximum grain size.
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param grainSize the maximum grain size for an activity
     * @param body a closure that executes over a single value of the index
     * @param reduce the reduction operation
     * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
     */
    public static @Inline def bisectReduce[T](min:Long, max:Long,
                                     grainSize:Long,
                                     body:(i:Long)=>T,
                                     reduce:(a:T,b:T)=>T, identity:T):T {
        // convert single index closure into execution over range
        val executeRange = (start:Long, end:Long) => {
            var myRes:T = identity;
            for (i in start..end) {
                myRes = reduce(myRes, body(i));
            }
            myRes
        };
        return bisectReduce(min, max, grainSize, executeRange, reduce);
    }

    /**
     * Reduce over a range of indices in parallel using recursive bisection.
     * The range is divided into two approximately equal pieces, with each 
     * piece constituting an activity. Bisection recurs until each activity is
     * less than or equal to a maximum grain size.
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param grainSize the maximum grain size for an activity
     * @param body a closure that executes over a contiguous range of indices, 
     *   returning the reduced value for that range
     * @param reduce the reduction operation
     */
    public static @Inline def bisectReduce[T](min:Long, max:Long,
                                     grainSize:Long,
                                     body:(i:Long, j:Long)=>T,
                                     reduce:(a:T,b:T)=>T):T {
        if (Runtime.NTHREADS == 1n) {
            return sequentialReduce(min, max, body, reduce);
        } else {
            return doBisectReduce1D(min, max+1, grainSize, body, reduce);
        }
    }

    /**
     * Reduce over a range of indices in parallel using recursive bisection.
     * The range is divided into two approximately equal pieces, with each 
     * piece constituting an activity. Bisection recurs until a minimum grain
     * size of (max-min+1) / (Runtime.NTHREADS &times; 8) is reached.
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param body a closure that executes over a contiguous range of indices, 
     *   returning the reduced value for that range
     * @param reduce the reduction operation
     */
    public static @Inline def bisectReduce[T](min:Long, max:Long,
                                     body:(i:Long, j:Long)=>T,
                                     reduce:(a:T,b:T)=>T):T {
        val grainSize = Math.max(1, (max-min) / (Runtime.NTHREADS*8));
        return Foreach.bisectReduce(min, max, grainSize, body, reduce);
    }

    /**
     * Reduce over a range of indices in parallel using recursive bisection.
     * The range is divided into two approximately equal pieces, with each 
     * piece constituting an activity. Bisection recurs until a minimum grain
     * size of (max-min+1) / (Runtime.NTHREADS &times; 8) is reached.
     * @param min the minimum value of the index
     * @param max the maximum value of the index
     * @param body a closure that executes over a contiguous range of indices
     */
    public static @Inline def bisectReduce[T](min:Long, max:Long,
                                     body:(i:Long)=>T,
                                     reduce:(a:T,b:T)=>T, identity:T):T {
        val grainSize = Math.max(1, (max-min) / (Runtime.NTHREADS*8));
        return Foreach.bisectReduce(min, max, grainSize, body, reduce, identity);
    }

    private static def doBisectReduce1D[T](start:Long, end:Long,
                                        grainSize:Long,
                                        body:(min:Long, max:Long)=>T,
                                        reduce:(a:T,b:T)=>T):T {
        if ((end-start) > grainSize) {
            val asyncResult:T;
            val syncResult:T;
            finish {
                async asyncResult = doBisectReduce1D[T]((start+end)/2L, end, grainSize, body, reduce);
                syncResult = doBisectReduce1D[T](start, (start+end)/2L, grainSize, body, reduce);
            }
            return reduce(syncResult, asyncResult);
        } else {
            return body(start, end-1);
        }
    }

    /**
     * Iterate over a dense rectangular set of indices in parallel using 
     * two-dimensional recursive bisection. The index set is divided along the
     * largest dimension into two approximately equal pieces, with each piece  
     * constituting an activity. Bisection recurs on each subblock until each 
     * activity is smaller than or equal to a maximum grain size in each 
     * dimension.
     * @param min0 the minimum value of the first index dimension
     * @param max0 the maximum value of the first index dimension
     * @param min1 the minimum value of the second index dimension
     * @param max1 the maximum value of the second index dimension
     * @param grainSize0 the maximum grain size for the first index dimension
     * @param grainSize1 the maximum grain size for the second index dimension
     * @param body a closure that executes over a rectangular block of indices
     */
    public static @Inline def bisect(min0:Long, max0:Long,
                                     min1:Long, max1:Long,
                                     grainSize0:Long, grainSize1:Long,
                                     body:(min0:Long, max0:Long, min1:Long, max1:Long)=>void) {
        if (Runtime.NTHREADS == 1n) {
            body(min0, max0, min1, max1); // sequential
        } else {
            finish doBisect2D(min0, max0+1, min1, max1+1, grainSize0, grainSize1, body);
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
     * @param min0 the minimum value of the first index dimension
     * @param max0 the maximum value of the first index dimension
     * @param min1 the minimum value of the second index dimension
     * @param max1 the maximum value of the second index dimension
     * @param body a closure that executes over a rectangular block of indices
     */
    public static @Inline def bisect(min0:Long, max0:Long,
                                     min1:Long, max1:Long,
                                     body:(min0:Long, max0:Long, min1:Long, max1:Long)=>void) {
        val grainSize0 = Math.max(1, (max0-min0) / Runtime.NTHREADS);
        val grainSize1 = Math.max(1, (max1-min1) / Runtime.NTHREADS);
        Foreach.bisect(min0, max0, min1, max1, grainSize0, grainSize1, body);
    }

    /**
     * Iterate over a dense rectangular set of indices in parallel using 
     * two-dimensional recursive bisection. The index set is divided along the
     * largest dimension into two approximately equal pieces, with each piece  
     * constituting an activity. Bisection recurs on each subblock until each 
     * activity is smaller than or equal to a maximum grain size in each 
     * dimension.
     * @param min0 the minimum value of the first index dimension
     * @param max0 the maximum value of the first index dimension
     * @param min1 the minimum value of the second index dimension
     * @param max1 the maximum value of the second index dimension
     * @param grainSize0 the maximum grain size for the first index dimension
     * @param grainSize1 the maximum grain size for the second index dimension
     * @param body a closure that executes over a single index [i,j]
     */
    public static @Inline def bisect(min0:Long, max0:Long,
                                     min1:Long, max1:Long,
                                     grainSize0:Long, grainSize1:Long,
                                     body:(i:Long, j:Long)=>void) {
        // convert single index closure into execution over range
        val executeRange = (min0:Long, max0:Long, min1:Long, max1:Long) => {
            for (i in min0..max0)
                for (j in min1..max1)
                    body(i, j);
        };
        Foreach.bisect(min0, max0, min1, max1, grainSize0, grainSize1, executeRange);
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
     * @param min0 the minimum value of the first index dimension
     * @param max0 the maximum value of the first index dimension
     * @param min1 the minimum value of the second index dimension
     * @param max1 the maximum value of the second index dimension
     * @param body a closure that executes over a single index [i,j]
     */
    public static @Inline def bisect(min0:Long, max0:Long,
                                     min1:Long, max1:Long,
                                     body:(i:Long, j:Long)=>void) {
        val grainSize0 = Math.max(1, (max0-min0) / Runtime.NTHREADS);
        val grainSize1 = Math.max(1, (max1-min1) / Runtime.NTHREADS);
        Foreach.bisect(min0, max0, min1, max1, grainSize0, grainSize1, body);
    }

    /**
     * Perform a parallel iteration using recursive bisection.
     * Do not wait for termination.
     */
    private static def doBisect2D(s0:Long, e0:Long,
                                  s1:Long, e1:Long,
                                  g1:Long, g2:Long,
                                  body:(min_i1:Long, max_i1:Long, min_i2:Long, max_i2:Long)=>void) {
        if ((e0-s0) > g1 && ((e0-s0) >= (e1-s1) || (e1-s1) <= g2)) {
            async doBisect2D((s0+e0)/2L, e0, s1, e1, g1, g2, body);
            doBisect2D(s0, (s0+e0)/2L, s1, e1, g1, g2, body);
        } else if ((e1-s1) > g2) {
            async doBisect2D(s0, e0, (s1+e1)/2L, e1, g1, g2, body);
            doBisect2D(s0, e0, s1, (s1+e1)/2L, g1, g2, body);
        } else {
            body(s0, e0-1, s1, e1-1);
        }
    }

    /**
     * Reduce over a dense rectangular set of indices in parallel using 
     * two-dimensional recursive bisection. The index set is divided along the
     * largest dimension into two approximately equal pieces, with each piece  
     * constituting an activity. Bisection recurs on each subblock until each 
     * activity is smaller than or equal to a maximum grain size in each 
     * dimension.
     * @param min0 the minimum value of the first index dimension
     * @param max0 the maximum value of the first index dimension
     * @param min1 the minimum value of the second index dimension
     * @param max1 the maximum value of the second index dimension
     * @param grainSize0 the maximum grain size for the first index dimension
     * @param grainSize1 the maximum grain size for the second index dimension
     * @param body a closure that executes over a single index [i,j]
     * @param reduce the reduction operation
     * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
     */
    public static @Inline def bisectReduce[T](min0:Long, max0:Long,
                                           min1:Long, max1:Long,
                                           grainSize0:Long, grainSize1:Long,
                                           body:(i:Long, j:Long)=>T,
                                           reduce:(a:T,b:T)=>T, identity:T):T {
        if (Runtime.NTHREADS == 1n) {
            return sequentialReduce(new DenseIterationSpace_2(min0, min1, max0, max1), body, reduce, identity);
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
            return doBisectReduce2D(min0, max0+1, min1, max1+1, grainSize0, grainSize1, reduceRange, reduce);
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
     * @param grainSize0 the maximum grain size for the first index dimension
     * @param grainSize1 the maximum grain size for the second index dimension
     * @param body a closure that executes over a single index [i,j]
     * @param reduce the reduction operation
     * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
     */
    public static @Inline def bisectReduce[T](space:DenseIterationSpace_2,
                                           grainSize0:Long, grainSize1:Long,
                                           body:(i:Long, j:Long)=>T,
                                           reduce:(a:T,b:T)=>T, identity:T):T {
        return bisectReduce(space.min0, space.max0, space.min1, space.max1, grainSize0, grainSize1, body, reduce, identity);
    }

    /**
     * Reduce over a dense rectangular set of indices in parallel using 
     * two-dimensional recursive bisection. The index set is divided along the
     * largest dimension into two approximately equal pieces, with each piece  
     * constituting an activity. Bisection recurs on each subblock until each 
     * activity is smaller than or equal to a maximum grain size in each 
     * dimension.
     * @param space the 2D dense space over which to reduce
     * @param body a closure that executes over a single index [i,j]
     * @param reduce the reduction operation
     * @param identity the identity value for the reduction operation such that reduce(identity,f)=f
     */
    public static @Inline def bisectReduce[T](space:DenseIterationSpace_2,
                                           body:(i:Long, j:Long)=>T,
                                           reduce:(a:T,b:T)=>T, identity:T):T {
        val grainSize0 = Math.max(1, (space.max0-space.min0) / Runtime.NTHREADS);
        val grainSize1 = Math.max(1, (space.max1-space.min1) / Runtime.NTHREADS);
        return bisectReduce(space.min0, space.max0, space.min1, space.max1, grainSize0, grainSize1, body, reduce, identity);
    }

    /**
     * Perform a parallel reduction using recursive bisection.
     * Results of each branch are reduced together at each level of the tree.
     * Wait for termination of all nested asyncs.
     */
    private static def doBisectReduce2D[T](s0:Long, e0:Long,
                                  s1:Long, e1:Long,
                                  g1:Long, g2:Long,
                                  body:(min_i1:Long, max_i1:Long, min_i2:Long, max_i2:Long)=>T,
                                  reduce:(a:T,b:T)=>T):T {
        if ((e0-s0) > g1 && ((e0-s0) >= (e1-s1) || (e1-s1) <= g2)) {
            val asyncResult:T;
            val syncResult:T;
            finish {
                async asyncResult = doBisectReduce2D[T]((s0+e0)/2L, e0, s1, e1, g1, g2, body, reduce);
                syncResult = doBisectReduce2D[T](s0, (s0+e0)/2L, s1, e1, g1, g2, body, reduce);
            }
            return reduce(asyncResult, syncResult);
        } else if ((e1-s1) > g2) {
            val asyncResult:T;
            val syncResult:T;
            finish {
                async asyncResult = doBisectReduce2D[T](s0, e0, (s1+e1)/2L, e1, g1, g2, body, reduce);
                syncResult = doBisectReduce2D[T](s0, e0, s1, (s1+e1)/2L, g1, g2, body, reduce);
            }
            return reduce(asyncResult, syncResult);
        } else {
            return body(s0, e0-1, s1, e1-1);
        }
    }

    /**
     * Implementation where iterations are executes in sequence. This
     * may be used for debugging purposes or when there is no benefit
     * to be gained from parallelizing an iteration.
     */
    public static final class Sequential {
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
         * @param min the minimum value of the index
         * @param max the maximum value of the index
         * @param body a closure that executes over a contiguous range of indices
         */
        private static @Inline def slice(min:Long, max:Long,
                                         body:(min:Long, max:Long)=>void) {
            body(min, max);
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
         * @param min the minimum value of the index
         * @param max the maximum value of the index
         * @param reduce the reduction operation
         * @param body a closure that executes over a contiguous range of indices,
         *   returning the reduced value for that range
         */
        private static @Inline def reduceSlice[T](min:Long, max:Long,
                                                  reduce:(a:T,b:T)=>T,
                                                  body:(min:Long, max:Long)=>T):T{
            return body(min, max);
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
             * raise <code>NotReady</code> if no result has been
             * computed yet.
             */
            public final @Inline def value () throws NotReady {
                if (this.result == null) { throw new NotReady(); }
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

    /**
     * Basic implementation where a separate async is started for
     * every iteration.
     */
    public static final class Basic {

        /**
         * Iterate over a range of indices in parallel using a basic async
         * transformation.
         * @param range the iteration space
         * @param body a closure that executes over a single value of the index
         */
        public static @Inline operator for(range: LongRange,
                                           body:(i:Long)=>void) {
            if (Runtime.NTHREADS == 1n) {
                Sequential.operator for(range,  body);
            } else {
                finish for (i in range) async body(i);
            }
        }

        /**
         * Iterate over a range of indices in parallel using a basic async
         * transformation.
         * @param space the 2D dense space over which to iterate
         * @param body a closure that executes over a single value of the index [i,j]
         */
        public static @Inline operator for(space:DenseIterationSpace_2,
                                           body:(i:Long, j:Long)=>void) {
            if (Runtime.NTHREADS == 1n) {
                Sequential.operator for(space, body);
            } else {
                finish for ([i, j] in space) {
                    async body(i, j);
                }
            }
        }
    }

    /**
     * Implementation where <code>Runtime.NTHREADS</code> activities
     * are created, one for each worker thread. Each activity executes
     * sequentially over all indices in a contiguous block, and the
     * blocks are of approximately equal size.
     */
    public static final class Block {

        /**
         * Execute the closure that iterates over a range of
         * indices. Several activities execute in parallel the closure
         * on a slice of the indices using a block decomposition.
         * @param min the minimum value of the index
         * @param max the maximum value of the index
         * @param body a closure that executes over a contiguous range of indices
         */
        private static @Inline def slice(min:Long, max:Long,
                                         body:(min:Long, max:Long)=>void) {
            val nthreads = Runtime.NTHREADS;
            if (nthreads == 1n) {
                Sequential.slice(min, max, body);
            } else {
                val numElems = max - min + 1;
                if (numElems < 1) return;
                val blockSize = numElems/nthreads;
                val leftOver = numElems - nthreads*blockSize;
                finish for (var t:Long = nthreads-1; t >= 0; t--) {
                    val myT = t;
                    async {
                        val lo = min + blockSize*myT + (myT < leftOver ? myT : leftOver);
                        val hi = lo + blockSize + (myT < leftOver ? 0 : -1);
                        body(lo, hi);
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
            val executeRange = (start:Long, end:Long)=> { Sequential.operator for(start..end, body); };
            Foreach.Block.slice(range.min, range.max, executeRange);
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
                         Foreach.Sequential.operator for(block, body);
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
            val executeRange = (start:Long, end:Long) => {
                var myRes:T = identity;
                for (i in start..end) {
                    myRes = reduce(myRes, body(i));
                }
                myRes
            };
            return Foreach.Block.reduceSlice(range.min, range.max, reduce, executeRange);
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
         * @param min the minimum value of the index
         * @param max the maximum value of the index
         * @param reduce the reduction operation
         * @param body a closure that executes over a contiguous range of indices,
         *   returning the reduced value for that range
         */
        private static @Inline def reduceSlice[T](min:Long, max:Long,
                                                  reduce:(a:T,b:T)=>T,
                                                  body:(min:Long, max:Long)=>T):T{
            val nthreads = Runtime.NTHREADS;
            if (nthreads == 1n) {
                return body(min, max); // sequential
            } else {
                val numElems = max - min + 1;
                if (numElems < 1) return body(min, max);
                val blockSize = numElems/nthreads;
                val leftOver = numElems - nthreads*blockSize;
                val results = Unsafe.allocRailUninitialized[T](nthreads);
                finish for (var t:Long = nthreads-1; t >= 0; t--) {
                    val myT = t;
                    async {
                        val lo = min + blockSize*myT + (myT < leftOver ? myT : leftOver);
                        val hi = lo + blockSize + (myT < leftOver ? 0 : -1);
                        results(myT) = body(lo, hi);
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
             * raise <code>NotReady</code> if no result has been
             * computed yet.
             */
            public final @Inline def value () throws NotReady {
                if (this.result == null) { throw new NotReady(); }
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



    /**
     * Implementation where <code>Runtime.NTHREADS</code> activities are
     * created, one for each worker thread.  Given T=Runtime.NTHREADS,
     * activity for thread number <code>t</code> executes iterations
     * (t, t+T, t+2 &times; T, ...).
     */
    public static final class Cyclic {

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
             * raise <code>NotReady</code> if no result has been
             * computed yet.
             */
            public final @Inline def value () throws NotReady {
                if (this.result == null) { throw new NotReady(); }
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

    /**
     * Implementation where the range is divided into two
     * approximately equal pieces, with each piece constituting an
     * activity. Bisection recurs until each activity is less than or
     * equal to a maximum grain size.
     */
    public static final class Bisect {

        /**
         * Iterate over a range of indices in parallel using recursive bisection.
         * @param min the minimum value of the index
         * @param max the maximum value of the index
         * @param grainSize the maximum grain size for an activity
         * @param body a closure that executes over a contiguous range of indices
         */
        private static @Inline def slice(min:Long, max:Long,
                                         grainSize:Long,
                                         body:(min:Long, max:Long)=>void) {
            if (Runtime.NTHREADS == 1n) {
                Sequential.slice(min, max, body);
            } else {
                finish doBisect1D(min, max+1, grainSize, body);
            }
        }

        /**
         * Iterate over a range of indices in parallel using recursive
         * bisection. Bisection recurs until a minimum grain size of
         * (max-min+1) / (Runtime.NTHREADS &times; 8) is reached.
         * @param min the minimum value of the index
         * @param max the maximum value of the index
         * @param body a closure that executes over a contiguous range of indices
         */
        private static @Inline def slice(min:Long, max:Long,
                                         body:(min:Long, max:Long)=>void) {
            val grainSize = Math.max(1, (max-min) / (Runtime.NTHREADS*8));
            Foreach.Bisect.slice(min, max, grainSize, body);
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
                val executeRange = (start:Long, end:Long) => {
                    for (i in start..end) body(i);
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
            Foreach.Bisect.operator for(range, grainSize, body);
        }

        private static def doBisect1D(start:Long, end:Long,
                                      grainSize:Long,
                                      body:(min:Long, max:Long)=>void) {
            if ((end-start) > grainSize) {
                async doBisect1D((start+end)/2L, end, grainSize, body);
                doBisect1D(start, (start+end)/2L, grainSize, body);
            } else {
                body(start, end-1);
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
            val executeRange = (start:Long, end:Long) => {
                var myRes:T = identity;
                for (i in start..end) {
                    myRes = reduce(myRes, body(i));
                }
                myRes
            };
            return reduceSlice(range.min, range.max, grainSize, reduce, executeRange);
        }

        /**
         * Reduce over a range of indices in parallel using recursive bisection.
         * @param min the minimum value of the index
         * @param max the maximum value of the index
         * @param grainSize the maximum grain size for an activity
         * @param body a closure that executes over a contiguous range of indices,
         *   returning the reduced value for that range
         * @param reduce the reduction operation
         */
        private static @Inline def reduceSlice[T](min:Long, max:Long,
                                                  grainSize:Long,
                                                  reduce:(a:T,b:T)=>T,
                                                  body:(i:Long, j:Long)=>T):T {
            if (Runtime.NTHREADS == 1n) {
                return Sequential.reduceSlice(min, max, reduce, body);
            } else {
                return doBisectReduce1D(min, max+1, grainSize, reduce, body);
            }
        }

        /**
         * Reduce over a range of indices in parallel using recursive
         * bisection.  Bisection recurs until a minimum grain * size of
         * (max-min+1) / (Runtime.NTHREADS &times; 8) is reached.
         * @param min the minimum value of the index
         * @param max the maximum value of the index
         * @param reduce the reduction operation
         * @param body a closure that executes over a contiguous range of indices,
         *   returning the reduced value for that range
         */
        private static @Inline def reduceSlice[T](min:Long, max:Long,
                                                  reduce:(a:T,b:T)=>T,
                                                  body:(i:Long, j:Long)=>T):T {
            val grainSize = Math.max(1, (max-min) / (Runtime.NTHREADS*8));
            return Foreach.Bisect.reduceSlice(min, max, grainSize, reduce, body);
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
            return Foreach.Bisect.operator for(range, grainSize, reduce, identity, body);
        }

        private static def doBisectReduce1D[T](start:Long, end:Long,
                                            grainSize:Long,
                                            reduce:(a:T,b:T)=>T,
                                            body:(min:Long, max:Long)=>T):T {
            if ((end-start) > grainSize) {
                val asyncResult:T;
                val syncResult:T;
                finish {
                    async asyncResult = doBisectReduce1D[T]((start+end)/2L, end, grainSize, reduce, body);
                    syncResult = doBisectReduce1D[T](start, (start+end)/2L, grainSize, reduce, body);
                }
                return reduce(syncResult, asyncResult);
            } else {
                return body(start, end-1);
            }
        }

        /**
         * Iterate over a dense rectangular set of indices in parallel using
         * two-dimensional recursive bisection. The index set is divided along the
         * largest dimension into two approximately equal pieces, with each piece
         * constituting an activity. Bisection recurs on each subblock until each
         * activity is smaller than or equal to a maximum grain size in each
         * dimension.
         * @param min0 the minimum value of the first index dimension
         * @param max0 the maximum value of the first index dimension
         * @param min1 the minimum value of the second index dimension
         * @param max1 the maximum value of the second index dimension
         * @param grainSize0 the maximum grain size for the first index dimension
         * @param grainSize1 the maximum grain size for the second index dimension
         * @param body a closure that executes over a rectangular block of indices
         */
        private static @Inline def slice(min0:Long, max0:Long,
                                         min1:Long, max1:Long,
                                         grainSize0:Long, grainSize1:Long,
                                         body:(min0:Long, max0:Long, min1:Long, max1:Long)=>void) {
            if (Runtime.NTHREADS == 1n) {
                body(min0, max0, min1, max1); // sequential
            } else {
                finish doBisect2D(min0, max0+1, min1, max1+1, grainSize0, grainSize1, body);
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
         * @param min0 the minimum value of the first index dimension
         * @param max0 the maximum value of the first index dimension
         * @param min1 the minimum value of the second index dimension
         * @param max1 the maximum value of the second index dimension
         * @param body a closure that executes over a rectangular block of indices
         */
        private static @Inline def slice(min0:Long, max0:Long,
                                         min1:Long, max1:Long,
                                         body:(min0:Long, max0:Long, min1:Long, max1:Long)=>void) {
            val grainSize0 = Math.max(1, (max0-min0) / Runtime.NTHREADS);
            val grainSize1 = Math.max(1, (max1-min1) / Runtime.NTHREADS);
            Foreach.Bisect.slice(min0, max0, min1, max1, grainSize0, grainSize1, body);
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
            val executeRange = (min0:Long, max0:Long, min1:Long, max1:Long) => {
                for (i in min0..max0)
                    for (j in min1..max1)
                        body(i, j);
            };
            Foreach.Bisect.slice(space.min0, space.max0, space.min1, space.max1, grainSize0, grainSize1, executeRange);
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
            Foreach.Bisect.operator for (space, grainSize0, grainSize1, body);
        }

        /**
         * Perform a parallel iteration using recursive bisection.
         * Do not wait for termination.
         */
        private static def doBisect2D(s0:Long, e0:Long,
                                      s1:Long, e1:Long,
                                      g1:Long, g2:Long,
                                      body:(min_i1:Long, max_i1:Long, min_i2:Long, max_i2:Long)=>void) {
            if ((e0-s0) > g1 && ((e0-s0) >= (e1-s1) || (e1-s1) <= g2)) {
                async doBisect2D((s0+e0)/2L, e0, s1, e1, g1, g2, body);
                doBisect2D(s0, (s0+e0)/2L, s1, e1, g1, g2, body);
            } else if ((e1-s1) > g2) {
                async doBisect2D(s0, e0, (s1+e1)/2L, e1, g1, g2, body);
                doBisect2D(s0, e0, s1, (s1+e1)/2L, g1, g2, body);
            } else {
                body(s0, e0-1, s1, e1-1);
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
             * raise <code>NotReady</code> if no result has been
             * computed yet.
             */
            public final @Inline def value () throws NotReady {
                if (this.result == null) { throw new NotReady(); }
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

    /**
     * Exception raised when the value build by a reducing loop is not yet ready.
     */
    public static final class NotReady extends Exception {
        public def this () { super(); }
    }

}
