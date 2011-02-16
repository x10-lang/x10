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

import x10.compiler.Header;
import x10.compiler.Inline;
import x10.compiler.Native;
import x10.compiler.NativeRep;


/**
 * A low-level abstraction of a chunk of memory that
 * contains a dense, indexed from 0 collection of 
 * values of type T.  No bounds checking or other
 * error checking is performed on read/write access to
 * this memory.<p>
 *
 * This abstraction is provide to enable other higher-level
 * abstractions (such as Array) to be implemented efficiently
 * and to allow low-level programming of memory regions at the
 * X10 level when absolutely required for performance. This class
 * is not intended for general usage, since it is inherently unsafe.<p>
 */
@NativeRep("java", "x10.core.RemoteIndexedMemoryChunk<#1>", null, "new x10.rtt.ParameterizedType(x10.core.RemoteIndexedMemoryChunk._RTT, #2)")
@NativeRep("c++", "x10::util::RemoteIndexedMemoryChunk<#T >", "x10::util::RemoteIndexedMemoryChunk<#T >", null)
public struct RemoteIndexedMemoryChunk[T] {

    @Native("java", "null")
    @Native("c++", "null")
    private native def this(); // unused; prevent instantiaton outside of native code

    @Native("java", "x10.core.RemoteIndexedMemoryChunk.<#2>wrap(#4)")
    @Native("c++", "x10::util::RemoteIndexedMemoryChunk<#T >((#imc)->raw(), (#imc)->length())")
    public static native def wrap[T](imc:IndexedMemoryChunk[T]):RemoteIndexedMemoryChunk[T];
    
    /** 
     * Can only be invoked at the place at which the RemoteIndexedMemoryChunk was
     * created. Returns the encapsulated IndexedMemoryChunk.
     */
    @Native("java", "(#0).$apply$G()")
    @Native("c++", "x10::util::IndexedMemoryChunk<#T >((#this)->raw(), (#this)->length())")
    public native operator this(){here == this.home()}:IndexedMemoryChunk[T];

    /**
     * Return the size of the RemoteIndexedMemoryChunk (in elements)
     *
     * @return the size of the RemoteIndexedMemoryChunk (in elements)
     */
    @Native("java", "((#0).length)")
    @Native("c++", "(#this)->length()")
    public native def length():int; /* TODO: We need to convert this to returning a long */

    /**
     * Return the home place of the RemoteIndexedMemoryChunk
     * @return the home place of the RemoteIndexedMemoryChunk
     */
    @Native("java", "((#0).home)")
    @Native("c++", "(#this)->home")
    public native property home():Place; 
         

   /*
    * @Native methods from Any because the handwritten C++ code doesn't 100% match 
    * what the compiler would have generated.
    */

    @Native("java", "(#0).toString()")
    @Native("c++", "(#this)->toString()")
    public native def  toString():String;

    @Native("java", "(#0).equals(#1)")
    @Native("c++", "(#this)->equals(#that)")
    public native def equals(that:Any):Boolean;

    @Native("java", "(#0).hashCode()")
    @Native("c++", "(#this)->hash_code()")
    public native def  hashCode():Int;

    @Native("java", "(#0).remoteAdd(#1,#2)")
    @Native("c++", "(#this)->remoteAdd(#idx,#v)")
    public native def remoteAdd(idx:Int, v:ULong) : void;
    @Native("java", "(#0).remoteAnd(#1,#2)")
    @Native("c++", "(#this)->remoteAnd(#idx,#v)")
    public native def remoteAnd(idx:Int, v:ULong) : void;
    @Native("java", "(#0).remoteOr(#1,#2)")
    @Native("c++", "(#this)->remoteOr(#idx,#v)")
    public native def remoteOr(idx:Int, v:ULong) : void;
    @Native("java", "(#0).remoteXor(#1,#2)")
    @Native("c++", "(#this)->remoteXor(#idx,#v)")
    public native def remoteXor(idx:Int, v:ULong) : void;
    @Native("java", "(#0).remoteAdd(#1,#2)")
    @Native("c++", "(#this)->remoteAdd(#idx,#v)")
    public native def remoteAdd(idx:Int, v:Long) : void;
    @Native("java", "(#0).remoteAnd(#1,#2)")
    @Native("c++", "(#this)->remoteAnd(#idx,#v)")
    public native def remoteAnd(idx:Int, v:Long) : void;
    @Native("java", "(#0).remoteOr(#1,#2)")
    @Native("c++", "(#this)->remoteOr(#idx,#v)")
    public native def remoteOr(idx:Int, v:Long) : void;
    @Native("java", "(#0).remoteXor(#1,#2)")
    @Native("c++", "(#this)->remoteXor(#idx,#v)")
    public native def remoteXor(idx:Int, v:Long) : void;
}

// vim:shiftwidth=4:tabstop=4:expandtab
