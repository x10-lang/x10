package x10.util;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/** We make this a native class for efficiency. */
@NativeRep("java", "x10.core.GrowableRail<#1>", null, null)
public class GrowableRail[T] implements (Int) => T, Settable[Int,T] {
    public native def this();
    public native def this(Int);

    @Native("java", "#0.add(#1)")
    public native def add(T): Void;

    @Native("java", "#0.apply(#1)")
    public native def apply(Int): T;

    @Native("java", "#0.set(#1, #2)")
    public native def set(T, Int): T;

    @Native("java", "#0.length()")
    public native def length(): Int;
    
    @Native("java", "#0.removeLast()")
    public native def removeLast(): Void;

    @Native("java", "#0.toRail()")
    public native def toRail(): Rail[T];

    @Native("java", "#0.toValRail()")
    public native def toValRail(): ValRail[T];
}

