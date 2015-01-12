/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.lang;

import x10.compiler.Native;

/** A read-only view on a rail in memory.  On the CPU, this class has no effect
 * on behavior or performance.  On the GPU, it denotes memory that should back
 * the GPU constant cache.  Behaviour is therefore undefined if the backing rail
 * is modified while the CUDAConstantRail view is being used.
 */
public struct CUDAConstantRail[T] {
    
    /** Backing rail. */
    private val backing : Rail[T];

    /** Create a view on given a backing rail. */
    public def this (backing:Rail[T]) {
        this.backing = backing;
        // TODO: check size, make sure it will fit in gpu constant memory
    }

    /** On the CPU, delegate accesses to the backing rail. */
    @Native("cuda", "(#this).raw[#i]")
    public operator this (i:Long) : T = backing(i);
}

