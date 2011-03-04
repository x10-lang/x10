/*
 *
 * (C) Copyright IBM Corporation 2009
 *
 *  This file is part of X10 runtime. It is 
 *  governed by the licence under which 
 *  X10 is released.
 *
 */
package x10.util.concurrent.atomic;

import x10.compiler.Native;

public class Fences {
    @Native("java", "x10.runtime.impl.java.Fences.loadLoadBarrier()")
    @Native("c++", "x10aux::atomic_ops::load_load_barrier()")
    public static native def loadLoadBarrier():void;

    @Native("java", "x10.runtime.impl.java.Fences.loadStoreBarrier()")
    @Native("c++", "x10aux::atomic_ops::load_store_barrier()")
    public static native def loadStoreBarrier():void;

    @Native("java", "x10.runtime.impl.java.Fences.storeLoadBarrier()")
    @Native("c++", "x10aux::atomic_ops::store_load_barrier()")
    public static native def storeLoadBarrier():void;

    @Native("java", "x10.runtime.impl.java.Fences.storeStoreBarrier()")
    @Native("c++", "x10aux::atomic_ops::store_store_barrier()")
    public static native def storeStoreBarrier():void;
}
 
