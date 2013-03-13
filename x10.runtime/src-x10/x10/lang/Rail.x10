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

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.util.IndexedMemoryChunk;

@NativeRep("java", "x10.core.Rail<#T$box>", null, "x10.rtt.ParameterizedType.make(x10.core.Rail.$RTT, #T$rtt)")
@NativeRep("c++", "x10::lang::Rail<#T >*", "x10::lang::Rail<#T >", null)
public final class Rail[T](size:Long) implements Iterable[T],(Int)=>T,(Long)=>T {

    public native property def range():LongRange;

    public native def iterator():Iterator[T];

    public native def toString():String;

    /**
     * @deprecated x10.util.IndexedMemoryChunk will be removed in X10 2.4.1
     */
    public native def this(backingStore:IndexedMemoryChunk[T]):Rail[T];

    public native def this():Rail[T]{self.size==0L};

    public native def this(src:Rail[T]):Rail[T]{self.size==src.size};

    public native def this(size:Long){T haszero}:Rail[T]{self.size==size};

    public native def this(size:Long, init:T):Rail[T]{self.size==size};

    public native def this(size:Long, init:(Long)=>T):Rail[T]{self.size==size};

    public native operator this(index:Long):T;

    public native operator this(index:Long)=(v:T):T{self==v};

    public static native def copy[T](src:Rail[T], dst:Rail[T]){src.size==dst.size}:void;

    public static native def copy[T](src:Rail[T], srcIndex:Long, 
                                     dst:Rail[T], dstIndex:Long, numElems:Long):void;

    @Native("java", "x10.core.Rail.<#T$box>asyncCopy(#src,(int)#srcIndex,#dst,(int)#dstIndex,(int)#numElems)")
    @Native("c++", "x10::lang::Rail<void>::asyncCopy<#T >(#src,#srcIndex,#dst,#dstIndex,#numElems,NULL)")
    public static native def asyncCopy[T](src:Rail[T], srcIndex:Long, 
            dst:GlobalRef[Rail[T]], dstIndex:Long, numElems:Long):void;

    @Native("java", "x10.core.Rail.<#T$box>asyncCopy(#src,(int)#srcIndex,#dst,(int)#dstIndex,(int)#numElems)")
    @Native("c++", "x10::lang::Rail<void>::asyncCopy<#T >(#src,#srcIndex,#dst,#dstIndex,#numElems,NULL)")
    public static native def asyncCopy[T](src:GlobalRef[Rail[T]], srcIndex:Long, 
            dst:Rail[T], dstIndex:Long, numElems:Long):void;

    @Native("java", "x10.core.Rail.<#T$box>asyncCopy(#src,(int)#srcIndex,#dst,(int)#dstIndex,(int)#numElems,#notifier)")
    @Native("c++", "x10::lang::Rail<void>::asyncCopy<#T >(#src,#srcIndex,#dst,#dstIndex,#numElems,#notifier)")
    public static native def uncountedCopy[T](src:Rail[T], srcIndex:Long, 
            dst:GlobalRef[Rail[T]], dstIndex:Long, numElems:Long, 
            notifier:()=>void):void;

    @Native("java", "x10.core.Rail.<#T$box>asyncCopy(#src,(int)#srcIndex,#dst,(int)#dstIndex,(int)#numElems,#notifier)")
    @Native("c++", "x10::lang::Rail<void>::asyncCopy<#T >(#src,#srcIndex,#dst,#dstIndex,#numElems,#notifier)")
    public static native def uncountedCopy[T](src:GlobalRef[Rail[T]], srcIndex:Long, 
            dst:Rail[T], dstIndex:Long, numElems:Long, 
            notifier:()=>void):void;

    /**
     * Clears the entire Rail by zeroing the storage.
     */
    public native def clear(){T haszero}:void;

    /**
     * Clears numElems of the backing storage begining at index start by zeroing the storage.
     */
    public native def clear(start:Long, numElems:Long){T haszero}:void;

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

    /**
     * Clears numElems of the backing storage begining at index start by zeroing the storage.
     */
    public native def clear(start:Int, numElems:Int){T haszero}:void;
}
