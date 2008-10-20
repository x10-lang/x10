/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * Manages the clocks of the current activity
 */
@NativeRep("java", "x10.runtime.ActivityClocks")
public value ActivityClocks {
    @Native("java", "#0.contains(#1)")
    public native static def contains(clock: Clock): boolean;

    @Native("java", "#0.get(#1)")
    public native static def get(clock: Clock): int;

    @Native("java", "#0.put(#1,#2)")
    public native static def put(clock: Clock, phase: int): void;

    @Native("java", "#0.remove(#1)")
    public native static def remove(clock: Clock): int;
}