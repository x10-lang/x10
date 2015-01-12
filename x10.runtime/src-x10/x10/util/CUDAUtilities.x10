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

package x10.util;

import x10.compiler.Native;

/**
 * A collection of functions useful in/around CUDA kernels.
 */
public class CUDAUtilities {

    /**
     * Automatically choose enough blocks to saturate the GPU.  This takes
     * account features of the GPU and kernel in question.  If running on the CPU
     * it returns a fixed number of blocks.  Intended to be used with autoThreads().
     * <p><blockquote><pre>
     * async at (gpu) &#064;CUDA {
     *     val threads = CUDAUtilities.autoThreads(), blocks = CUDAUtilities.autoBlocks();
     *     finish for (block in 0n..(blocks-1n)) async {
     *         ...
     *         clocked finish for (thread in 0n..(threads-1n)) clocked async { ... }
     *     }
     * }
     * </pre></blockquote><p>
     * @see autoThreads
     */
    public static def autoBlocks() : Int = 8n;

    /**
     * Automatically choose enough threads to saturate the GPU.
     * @see autoBlocks
     */
    public static def autoThreads() : Int = 1n;

    private static def initCUDARail[T](local:Rail[T],
                                       remote:GlobalRail[T],
                                       numElements:Long) : void {
          finish Rail.asyncCopy(local, 0, remote, 0, numElements);
    }

    private static def makeCUDARail[T](gpu:Place, numElements:Long, init:Rail[T])
      : GlobalRail[T]{self.home==gpu} {
        @Native("c++",
            "x10_ulong addr = x10aux::remote_alloc(gpu.FMGL(id), ((size_t)numElements)*sizeof(TPMGL(T)));\n"+
            "x10::lang::GlobalRef<x10::lang::Rail<TPMGL(T)> *> gr(gpu->FMGL(id), addr);\n"+
            "x10::lang::GlobalRail<TPMGL(T)> remote_rail = x10::lang::GlobalRail<TPMGL(T)>::_make(numElements, gr);\n"+
            "initCUDARail<TPMGL(T)>(init,remote_rail,numElements);\n"+
            "return remote_rail;\n"
        ) { }
        throw new UnsupportedOperationException();
    }


    // Init from Rail[T]
    public static def makeGlobalRail[T](place:Place, numElements:Long, init: Rail[T])
    {
        if (place.isCUDA()) {
            return makeCUDARail(place, numElements, init);
        } else {
            return (at (place) GlobalRail(new Rail[T](numElements, (p:Long)=>init(p as Int)))) as GlobalRail[T]{self.home==place};
        }
    }

    // Init as zero
    public static def makeGlobalRail[T](place:Place, numElements:Long) { T haszero }
    {
        return makeGlobalRail[T](place, numElements, Zero.get[T]());
    }

    // Init from single T value
    public static def makeGlobalRail[T](place:Place, numElements:Long, init: T)
    {
        if (place.isCUDA()) {
            val chunk = new Rail[T](numElements, init);
            return makeCUDARail(place, numElements, chunk);
        } else {
            return (at (place) GlobalRail(new Rail[T](numElements, init))) as GlobalRail[T]{self.home==place};
        }
    }

    // Init with closure
    public static def makeGlobalRail[T](place:Place, numElements:Long, init: (Long)=>T)
    {
        if (place.isCUDA()) {
            val chunk = new Rail[T](numElements, init);
            return makeCUDARail(place, numElements, chunk);
        } else {
            return (at (place) GlobalRail(new Rail[T](numElements, (p:Long)=>init(p as Int)))) as GlobalRail[T]{self.home==place};
        }
    }


    public static def deleteGlobalRail[T](arr: GlobalRail[T]) : void
    {
        val place = arr.home;
        if (place.isCUDA()) {
            @Native("c++",
                "x10aux::remote_free(place.FMGL(id), arr->FMGL(rail)->value);\n"
            ) { }
        } else {
            // let it be garbage collected
        }
    }

    @Native("cuda","__mul24(#a, #b)")
    public static def mul24(a:Int, b:Int) : Int = a * b;


    /**
     * This is needed to copy the shared FIFO used by printf device system call to host
     * before launching another kernel on the same CUDA device.
     */
    public static def syncDevice(place:Place):void
    {
        if (place.isCUDA()) {
            @Native("c++", "::x10aux::device_sync(place.FMGL(id));") { }
        }
    }

}

// vim: shiftwidth=4:tabstop=4:expandtab
