/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.Native;
import x10.io.Console;
import x10.util.Timer;
import x10.util.Pair;

public class System {

    private def this() {}

    /**
     * Provides the current time in milliseconds.
     *
     * @return The current time in milliseconds.
     */
    public static def currentTimeMillis() = Timer.milliTime();

    /**
     * Provides the current time in nanoseconds, as precise as the system timers provide.
     *
     * @return The current time in nanoseconds.
     */
    public static def nanoTime() = Timer.nanoTime();

    /**
     * Terminates the application with a given exit code, as quickly as possible.
     * All finally blocks for the currently executing activities are executed.
     * LIMITATION: will only work if invoked from the main thread in place 0 (see XTENLANG-874).
     *
     * @see #exit()
     * @see #setExitCode(Int)
     */
    @Native("java", "java.lang.System.exit(#1)")
    @Native("c++", "x10aux::system_utils::exit(#1)")
    public static native def exit(code: Int): void;

    /**
     * Terminates the application with exit code -1, as quickly as possible.
     * Invoking this method is equivalent to invoking {@link #exit(Int)} with argument -1.
     * LIMITATION: will only work if invoked from the main thread in place 0 (see XTENLANG-874).
     *
     * @see #exit(Int)
     * @see #setExitCode(Int)
     */
    public static def exit() = exit(-1);

    /**
     * Sets the system exit code.
     * The exit code will be returned from the application when main() terminates.
     * LIMITATION: will only work if invoked in place 0 (see XTENLANG-874).
     *
     * @see #exit(int)
     * @see #exit()
     */
    @Native("java", "x10.runtime.impl.java.Runtime.setExitCode(#1)")
    @Native("c++", "(x10aux::exitCode = (#1))")
    public static def setExitCode(exitCode: int): void {}

    /**
     * Provides an estimate in bytes of the size of the X10 heap
     * allocated to the current place. The accuracy of this estimate
     * is highly dependent on the implementation details of the
     * underlying memory management scheme being used by the X10 runtime,
     * and in some cases may simply return Long.MAX_VALUE or some other similarly
     * over conservative approximation.
     *
     * @return An upper bound in bytes on the size of the X10 heap allocated to the current place.
     */
    @Native("java", "java.lang.Runtime.totalMemory()")
    @Native("c++", "x10aux::heap_size()")
    public static native def heapSize():long;

    /**
     * Sets the system property with the given name to the given value.
     *
     * @param p the name of the system property.
     * @param v the value for the system property.
     * TODO: @ return The previous value of the property, or null if it did not have one.
     */
    // TODO: XTENLANG-180.  Provide full System properties API in straight X10
    @Native("java", "java.lang.System.setProperty(#1,#2)")
    @Native("c++", "printf(\"not setting %s\\n\", (#1)->c_str())") // FIXME: Trivial definition to allow XRX compilation to go through.
    public static native def setProperty(p:String,v:String):void;

    /**
     * Copies a portion of a given ValRail into a given remote Rail.
     * Upon completion, notifies the enclosing finish.
     *
     * @param src the source ValRail.
     * @param src_off the offset of the first element to copy in the source.
     * @param dst the destination Rail.
     * @param dst_off the offset of the first element store in the destination.
     * @param len the number of elements to copy.
     */
    // FIXME: this ought to be in ValRail but @Native system does not allow this
    public static def copyTo[T](src:ValRail[T], src_off:Int, dst:Rail[T], dst_off:Int, len:Int) {
        // could be further optimised to send only the part of the valrail needed
        at (dst) {
            //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
            for (var i:Int=0 ; i<len ; ++i) {
                dst(dst_off+i) = src(src_off+i);
            }
        }
    }

    /**
     * Copies a portion of a given ValRail into a remote Rail indicated by the given closure.
     * Upon completion, notifies the enclosing finish.
     *
     * @param src the source ValRail.
     * @param src_off the offset of the first element to copy in the source.
     * @param dst_place the location of the destination Rail.
     * @param dst_finder a function returning a {@link x10.util.Pair} that consists
     *                   of the reference to the destination Rail and the offset of
     *                   the first element to copy to in the destination.
     * @param len the number of elements to copy.
     */
    // FIXME: this ought to be in ValRail but @Native system does not allow this
    public static def copyTo[T](src:ValRail[T], src_off:Int,
                                dst_place:Place, dst_finder:()=>Pair[Rail[T]!,Int],
                                len:Int) {
        // could be further optimised to send only the part of the valrail needed
        at (dst_place) {
            val pair = dst_finder();
            val dst = pair.first;
            val dst_off = pair.second;
            //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
            for (var i:Int=0 ; i<len ; ++i) {
                dst(dst_off+i) = src(src_off+i);
            }
        }
    }

    /**
     * Copies a portion of a given Rail into a given remote Rail.
     * Upon completion, notifies the enclosing finish.
     *
     * @param src the source Rail.
     * @param src_off the offset of the first element to copy in the source.
     * @param dst the destination Rail.
     * @param dst_off the offset of the first element store in the destination.
     * @param len the number of elements to copy.
     */
    // FIXME: this ought to be in Rail but @Native system does not allow this
    public static def copyTo[T](src:Rail[T], src_off:Int, dst:Rail[T], dst_off:Int, len:Int) {
        // semantics allows an async per rail element inside a single finish
        // this version is optimised to use a single async for the whole rail
        // it could be further optimised to send only the part of the rail needed
        val to_serialize = src as ValRail[T];
        at (dst) {
            //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
            for (var i:Int=0 ; i<len ; ++i) {
                dst(dst_off+i) = to_serialize(src_off+i);
            }
        }
    }

    /**
     * Copies a portion of a given Rail into a remote Rail indicated by the given closure.
     * Upon completion, notifies the enclosing finish.
     *
     * @param src the source Rail.
     * @param src_off the offset of the first element to copy in the source.
     * @param dst_place the location of the destination Rail.
     * @param dst_finder a function returning a {@link x10.util.Pair} that consists
     *                   of the reference to the destination Rail and the offset of
     *                   the first element to store in the destination.
     * @param len the number of elements to copy.
     */
    // FIXME: this ought to be in Rail but @Native system does not allow this
    static public def copyTo[T](src:Rail[T], src_off:Int,
                                dst_place:Place, dst_finder:()=>Pair[Rail[T]!,Int],
                                len:Int) {
        // semantics allows an async per rail element inside a single finish
        // this version is optimised to use a single async for the whole rail
        // it could be further optimised to send only the part of the rail needed
        val to_serialize = src as ValRail[T];
        at (dst_place) {
            val pair = dst_finder();
            val dst = pair.first;
            val dst_off = pair.second;
            //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
            for (var i:Int=0 ; i<len ; ++i) {
                dst(dst_off+i) = to_serialize(src_off+i);
            }
        }
    }

    /**
     * Copies a portion of a given Rail into a remote Rail indicated by the given closure.
     * Upon completion, invokes the notifier closure.  The enclosing finish is not affected.
     *
     * @param src the source Rail.
     * @param src_off the offset of the first element to copy in the source.
     * @param dst_place the location of the destination Rail.
     * @param dst_finder a function returning a {@link x10.util.Pair} that consists
     *                   of the reference to the destination Rail and the offset of
     *                   the first element to store in the destination.
     * @param len the number of elements to copy.
     * @param notifier the function to invoke upon completion.
     */
    // FIXME: this ought to be in Rail but @Native system does not allow this
    public static def copyTo[T](src:Rail[T], src_off:Int,
                                dst_place:Place, dst_finder:()=>Pair[Rail[T]!,Int],
                                len:Int, notifier:()=>Void) {
        // semantics allows an async per rail element inside a single finish
        // this version is optimised to use a single async for the whole rail
        // it could be further optimised to send only the part of the rail needed
        val to_serialize = src as ValRail[T];
        Runtime.runAtNative(dst_place.id, ()=>{
            val pair = dst_finder();
            val dst = pair.first;
            val dst_off = pair.second;
            //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
            for (var i:Int=0 ; i<len ; ++i) {
                dst(dst_off+i) = to_serialize(src_off+i);
            }
            notifier();
        });
    }

    /**
     * Copies a portion of a given local Rail into the Rail stored in a given
     * place-local handle at a given place.
     * Upon completion, notifies the enclosing finish.
     *
     * @param src the source Rail.
     * @param src_off the offset of the first element to copy in the source.
     * @param dst_place the location of the destination Rail.
     * @param dst_handle the place-local handle that references the destination Rail.
     * @param dst_off the offset of the first element store in the destination.
     * @param len the number of elements to copy.
     */
    // This function exists because we do not want to call dealloc in user code (finder)
    public static def copyTo[T](src:Rail[T]!, src_off:Int,
                                dst_place:Place, dst_handle:PlaceLocalHandle[Rail[T]]!, dst_off:Int,
                                len:Int) {
        val finder = ()=> Pair[Rail[T],Int](dst_handle(), dst_off);
        src.copyTo[T](src_off, dst_place, finder, len);
        Runtime.dealloc(finder);
    }

    /**
     * Copies a portion of a given local Rail into the Rail stored in a given
     * place-local handle at a given place.
     * Upon completion, invokes the notifier closure.  The enclosing finish is not affected.
     *
     * @param src the source Rail.
     * @param src_off the offset of the first element to copy in the source.
     * @param dst_place the location of the destination Rail.
     * @param dst_handle the place-local handle that references the destination Rail.
     * @param dst_off the offset of the first element store in the destination.
     * @param len the number of elements to copy.
     * @param notifier the function to invoke upon completion.
     */
    // This function exists because we do not want to call dealloc in user code (finder)
    public static def copyTo[T](src:Rail[T]!, src_off:Int,
                                dst:Place, dst_handle:PlaceLocalHandle[Rail[T]]!, dst_off:Int,
                                len:Int, notifier:()=>Void) {
        val finder = ()=> Pair[Rail[T],Int](dst_handle(), dst_off);
        src.copyTo[T](src_off, dst, finder, len, notifier);
        Runtime.dealloc(finder);
        Runtime.dealloc(notifier);
    }

    /**
     * Copies the contents of a Rail stored in the given place-local handle in the
     * current place to the Rail stored in the same handle at a given place.
     * Upon completion, invokes the notifier closure.  The enclosing finish is not affected.
     *
     * @param handle the place-local handle that references the source and destination Rails.
     * @param dst_place the location of the destination Rail.
     * @param len the number of elements to copy.
     * @param notifier the function to invoke upon completion.
     */
    // This function exists because we do not want to call dealloc in user code (finder, notifier)
    // Also it is arguably a simpler interface because it has one less param
    public static def copyTo[T](handle:PlaceLocalHandle[Rail[T]]!,
                                dst_place:Place, len:Int, notifier:()=>Void) {
        val finder = ()=>Pair[Rail[T],Int](handle(), 0);
        handle().copyTo[T](0, dst_place, finder, len, notifier);
        Runtime.dealloc(finder);
        Runtime.dealloc(notifier);
    }

    /**
     * Copies a portion of a given Rail into a given remote Rail.
     * Upon completion, invokes the notifier closure.  The enclosing finish is not affected.
     *
     * @param src the source Rail.
     * @param src_off the offset of the first element to copy in the source.
     * @param dst the destination Rail.
     * @param dst_off the offset of the first element store in the destination.
     * @param len the number of elements to copy.
     */
    // This function exists because we do not want to call dealloc in user code (finder, notifier)
    public static def copyTo[T](src:Rail[T]!, src_off:Int, dst:Rail[T], dst_off:Int,
                                len:Int, notifier:()=>Void) {
        val finder = ()=>Pair[Rail[T],Int](dst,dst_off);
        src.copyTo[T](src_off, dst.home, finder, len, notifier);
        Runtime.dealloc(finder);
        Runtime.dealloc(notifier);
    }

    /**
     * Copies a portion of a given remote Rail into a given Rail (a DMA get).
     * Upon completion, notifies the enclosing finish.
     *
     * @param dst the destination Rail.
     * @param dst_off the offset of the first element store in the destination.
     * @param src the source Rail.
     * @param src_off the offset of the first element to copy in the source.
     * @param len the number of elements to copy.
     */
    // FIXME: this ought to be in Rail but @Native system does not allow this
    static public def copyFrom[T](dst:Rail[T], dst_off:Int, src:Rail[T], src_off:Int, len:Int) {
        // semantics allows an async per rail element inside a single finish
        // this version is optimised to use a single async for the whole rail
        // it could be further optimised to send only the part of the rail needed
        at (src) {
            val to_serialize = src as ValRail[T];
            at (dst) {
                //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
                for (var i:Int=0 ; i<len ; ++i) {
                    dst(dst_off+i) = to_serialize(src_off+i);
                }
            }
        }
    }

    /**
     * Copies a portion of a remote Rail indicated by the given closure into a given Rail (a DMA get).
     * Upon completion, notifies the enclosing finish.
     *
     * @param dst the destination Rail.
     * @param dst_off the offset of the first element to store in the destination.
     * @param src_place the location of the source Rail.
     * @param src_finder a function returning a {@link x10.util.Pair} that consists
     *                   of the reference to the source Rail and the offset of
     *                   the first element to copy in the source.
     * @param len the number of elements to copy.
     */
    // FIXME: this ought to be in Rail but @Native system does not allow this
    static public def copyFrom[T](dst:Rail[T], dst_off:Int,
                                  src_place:Place, src_finder:()=>Pair[Rail[T]!,Int],
                                  len:Int) {
        // semantics allows an async per rail element inside a single finish
        // this version is optimised to use a single async for the whole rail
        // it could be further optimised to send only the part of the rail needed
        at (src_place) {
            val pair = src_finder();
            val src = pair.first;
            val src_off = pair.second;
            val to_serialize = src as ValRail[T];
            at (dst) {
                //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
                for (var i:Int=0 ; i<len ; ++i) {
                    dst(dst_off+i) = to_serialize(src_off+i);
                }
            }
        }
    }

    /**
     * Copies a portion of a given ValRail into a given Rail (a DMA get).
     * Upon completion, notifies the enclosing finish.
     *
     * @param dst the destination Rail.
     * @param dst_off the offset of the first element store in the destination.
     * @param src the source ValRail.
     * @param src_off the offset of the first element to copy in the source.
     * @param len the number of elements to copy.
     */
    // FIXME: this ought to be in Rail but @Native system does not allow this
    static public def copyFrom[T](dst:Rail[T]!, dst_off:Int, src:ValRail[T], src_off:Int, len:Int) {
        // source is always local
        // semantics allows an async per rail element inside a single finish
        // this version is optimised to not use any asynchrony
        //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
        for (var i:Int=0 ; i<len ; ++i) {
            dst(dst_off+i) = src(src_off+i);
        }
    }

/* uncomment upon resolution of XTENLANG-533
    / **
     * Copies a portion of a ValRail indicated by the given closure into a given Rail (a DMA get).
     * Upon completion, notifies the enclosing finish.
     *
     * @param dst the destination Rail.
     * @param dst_off the offset of the first element to store in the destination.
     * @param src_place the location of the source ValRail (to evaluate the closure).
     * @param src_finder a function returning a {@link x10.util.Pair} that consists
     *                   of the reference to the source ValRail and the offset of
     *                   the first element to copy in the source.
     * @param len the number of elements to copy.
     * /
    // FIXME: this ought to be in Rail but @Native system does not allow this
/*  uncomment upon resolution of XTENLANG-533
    static def copyFrom[T](dst:Rail[T], dst_off:Int,
                           src_place:Place, src_finder:()=>Pair[ValRail[T]!,Int],
                           len:Int) {
        // not necessarily local, so go and fetch the ValRail...
        // semantics allows an async per rail element inside a single finish
        // this version is optimised to use a single async for the whole rail
        // it could be further optimised to send only the part of the rail needed
        at (src_place) {
            val pair = src_finder();
            val src = pair.first;
            val src_off = pair.second;
            at (dst) {
                //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
                for (var i:Int=0 ; i<len ; ++i) {
                    dst(dst_off+i) = src(src_off+i);
                }
            }
        }
    }
*/


    @Native("c++", "x10::lang::Rail<#1>::makeCUDA(#4,#5)")
    private static def cudaMakeRail[T](dst:Place, length:Int) : Rail[T]{self.length==length} {
        return null;
    }

    /**
     * Create a rail in a given place and initialize it using the given function.
     * This is a synchronous operation.
     *
     * @param p the location of the new Rail.
     * @param length the length of the new Rail.
     * @param init the initialization function.
     * @return The reference to a remote Rail.
     */
    public static safe def makeRemoteRail[T](p:Place, length:Int, init: (Int) => T)
        : Rail[T]{self.length==length}
    {
        val tmp = Rail.make(length, init);
        return makeRemoteRail[T](p,length,tmp);
    }

    /**
     * Create a rail in a given place and initialize it by copying from a given Rail.
     * This is a synchronous operation.
     *
     * @param p the location of the new Rail.
     * @param length the length of the new Rail.
     * @param init the Rail whose contents will be used to initialize the new Rail.
     * @return The reference to a remote Rail.
     */
    public static safe def makeRemoteRail[T](p:Place, length:Int, init: Rail[T]!)
        : Rail[T]{self.length==length}
    {
        val r = p.isCUDA() ? cudaMakeRail[T](p,length) : at (p) Rail.make[T](length);
        finish init.copyTo(0, r, 0, length);
        return r;
    }

}
