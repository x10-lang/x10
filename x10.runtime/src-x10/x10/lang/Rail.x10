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

package x10.lang;

import x10.compiler.Inline;
import x10.compiler.NativeRep;
import x10.util.IndexedMemoryChunk;


@NativeRep("java", "x10.core.Rail<#T$box>", null, "x10.rtt.ParameterizedType.make(x10.core.Rail.$RTT, #T$rtt)")
@NativeRep("c++", "x10::lang::Rail<#T >*", "x10::lang::Rail<#T >", null)
public final class Rail[T](size:Long) implements Iterable[T],(Int)=>T,(Long)=>T {

    public native property def range():LongRange;

    public native def iterator():Iterator[T];

    public native def toString():String;

    // TODO: This will go away as soon as we switch to
    //       the optimized @Native implementations.
    public native def raw():IndexedMemoryChunk[T];

    /**
     * @deprecated x10.util.IndexedMemoryChunk will be removed in X10 2.4.1
     */
    public native def this(backingStore:IndexedMemoryChunk[T]):Rail[T];

    public native def this():Rail[T]{self.size==0L};

    // unsafe constructor
    native def this(Unsafe.Token, size:Long, zeroInitialized:Boolean):Rail[T]{self.size==size};

    public native def this(src:Rail[T]):Rail[T]{self.size==src.size};

    public native def this(size:Long){T haszero}:Rail[T]{self.size==size};

    public native def this(size:Long, init:T):Rail[T]{self.size==size};

    public native def this(size:Long, init:(Long)=>T):Rail[T]{self.size==size};

    public native operator this(index:Long):T;

    public native operator this(index:Long)=(v:T):T{self==v};

    public static native def copy[T](src:Rail[T], dst:Rail[T]):void;

    public static native def copy[T](src:Rail[T], srcIndex:Long, 
                                     dst:Rail[T], dstIndex:Long, numElems:Long):void;

    /**
     * Clears the entire Rail by zeroing the storage.  
     * Note that this is intentionally not type safe because it does require T hasZero.
     */
    native def clear():void;

    /**
     * Clears numElems of the backing storage starting at index start
     * by zeroing the storage.  
     * Note that this is intentionally not type safe because it does require T hasZero.
     */
    native def clear(start:Long, numElems:Long):void;

    // secondary api: int indices

    // TODO: Returned rail should have constraint on size
    public native def this(size:Int){T haszero}:Rail[T];

    // TODO: Returned rail should have constraint on size
    public native def this(size:Int, init:T):Rail[T];

    // TODO: Returned rail should have constraint on size
    public native def this(size:Int, init:(Int)=>T):Rail[T];

    public native operator this(index:Int):T;

    public native operator this(index:int)=(v:T):T{self==v};

    public native static def copy[T](src:Rail[T], srcIndex:Int, 
                                     dst:Rail[T], dstIndex:Int, numElems:Int):void;
}

