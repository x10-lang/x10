/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * Interface with native runtime
 * @author tardieu
 */
@NativeRep("java", "x10.runtime.impl.java.Deque", null, null)
@NativeRep("c++", "x10aux::ref<x10::runtime::Deque>", "x10::runtime::Deque", null)
public final class Deque {
    @Native("java", "#0.getQueueSize()")
    @Native("c++", "(#0)->getQueueSize()")
    public native def size():Int;

    @Native("java", "#0.popTask()")
    @Native("c++", "(#0)->popTask()")
    public native def poll():Object;

    @Native("java", "#0.pushTask(#1)")
    @Native("c++", "(#0)->pushTask(#1)")
    public native def push(t:Object):Void;

    @Native("java", "#0.deqTask()")
    @Native("c++", "(#0)->deqTask()")
    public native def steal():Object;
}

// vim:shiftwidth=4:tabstop=4:expandtab
