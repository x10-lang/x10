/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.util;

import x10.compiler.Native;

/** A collection of functions useful in/around CUDA kernels.
 */
public class CUDAUtilities {

    /** Automatically choose enough blocks to saturate the GPU.  This takes
     * account features of the GPU and kernel in question.  If running on the CPU
     * it returns a fixed number of blocks.  Intended to be used with autoThreads().
     * <p>
     * <code>
     * async at (gpu) {
     *     val threads = CUDAUtilities.autoThreads(), blocks = CUDAUtilities.autoBlocks();
     *     for ((block) in 0..(blocks-1)) {
     *         ...
     *         for ((thread) in 0..(threads-1)) async { ... }
     *     }
     * }
     * </code>
     * @see autoThreads
     */
    public static def autoBlocks() : Int = 8;

    /** Automatically choose enough threads to saturate the GPU.  
      * @see autoBlocks
      */
    public static def autoThreads() : Int = 1;

    private static def initCUDARail[T](local:Rail[T],
                                       remote:GlobalRef[Rail[T]],
                                       numElements:Long) : void {
          finish Rail.asyncCopy(local, 0l, remote, 0l, numElements);
    }

    private static def makeCUDARail[T](gpu:Place, numElements:Long, init:Rail[T])
      : GlobalRef[Rail[T]]{self.home==gpu} {
/*
        @Native("c++",
            "x10_ulong addr = x10aux::remote_alloc(gpu.FMGL(id), ((size_t)numElements)*sizeof(TPMGL(T)));\n"+
            //"RemoteIndexedMemoryChunk<TPMGL(T)> rimc(addr, numElements, gpu);\n"+
            "initCUDARail<TPMGL(T)>(init,rimc,numElements);\n"+
            "return x10::lang::GlobalRef<TPMGL(T)>::_make(gpu, addr);\n"
        ) { }
*/
        throw new UnsupportedOperationException();
    }


    // Init from Rail[T]
    public static def makeRemoteRail[T](place:Place, numElements:Long, init: Rail[T])
    {
        if (place.isCUDA()) {
            return makeCUDARail(place, numElements, init);
        } else {
            return (at (place) GlobalRef[Rail[T]](new Rail[T](numElements, (p:Long)=>init(p as int)))) as GlobalRef[Rail[T]]{self.home==place};
        }
    }

    // Init as zero
    public static def makeRemoteRail[T](place:Place, numElements:Long) { T haszero }
      : GlobalRef[Rail[T]]{self.home==place} 
    {
        return makeRemoteRail[T](place, numElements, Zero.get[T]());
    }

    // Init from single T value
    public static def makeRemoteRail[T](place:Place, numElements:Long, init: T)
    {
        if (place.isCUDA()) {
            val chunk = new Rail[T](numElements, init);
            return makeCUDARail(place, numElements, chunk);
        } else {
            return (at (place) GlobalRef[Rail[T]](new Rail[T](numElements, init))) as GlobalRef[Rail[T]]{self.home==place};
        }
    }

    // Init with closure
    public static def makeRemoteRail[T](place:Place, numElements:Long, init: (Long)=>T)
    {
        if (place.isCUDA()) {
            val chunk = new Rail[T](numElements, init);
            return makeCUDARail(place, numElements, chunk);
        } else {
            return (at (place) GlobalRef[Rail[T]](new Rail[T](numElements, (p:long)=>init(p as int)))) as GlobalRef[Rail[T]]{self.home==place};
        }
    }


    public static def deleteRemoteRail[T](arr: GlobalRef[Rail[T]]) : void
    {
        val place = arr.home;
        if (place.isCUDA()) {
/*
            @Native("c++",
                "RemoteIndexedMemoryChunk<TPMGL(T)> rimc = arr->FMGL(rawData);\n"+
                "x10aux::remote_free(place.FMGL(id), (x10_ulong)(size_t)rimc->data);\n"
            ) { }
*/
        }
    }

    @Native("cuda","__mul24(#1,#2)")
    public static def mul24(a:Int, b:Int) : Int = a * b;
}

// vim: shiftwidth=4:tabstop=4:expandtab
