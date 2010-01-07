/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;

import x10.compiler.NativeClass;
import x10.compiler.NativeDef;

/**
 * Interface with native runtime
 * @author tardieu
 */
@NativeClass("java", "x10.runtime.impl.java", "Deque")
@NativeClass("c++", "x10.lang", "Deque")
public final class X10Deque {
    public native def this();

    public native def size():Int;

    public native def poll():Object;

    public native def push(t:Object):Void;

    public native def steal():Object;
}

// vim:shiftwidth=4:tabstop=4:expandtab
