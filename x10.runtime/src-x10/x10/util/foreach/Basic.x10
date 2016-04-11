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
 * Basic implementation where a separate async is started for
 * every iteration.
 */
public final class Basic {

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
