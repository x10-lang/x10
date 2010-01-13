package x10.util;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/** We make this a native class for efficiency. */
@NativeRep("java", "x10.core.GrowableRail<#1>", null, null)
@NativeRep("c++", "x10aux::ref<x10::util::GrowableRail<#1 > >", "x10::util::GrowableRail<#1 >", null)
public final class GrowableRail[T] implements Indexable[Int,T], Settable[Int,T] {
    /** Return a rail of length 0 */
    public native def this();
    
    /** Return a rail of length 0, with Int elements allocated */
    public native def this(Int);

    
    /** Add an element to the rail, incrementing length. */
    @Native("java", "#0.add(#1)")
    @Native("c++", "(#0)->add(#1)")
    public native def add(T): Void;

    /** Get the Int element of the rail, failing unless 0 <= Int < length. */
    @Native("java", "#0.apply(#1)")
    @Native("c++", "(#0)->apply(#1)")
    public native def apply(Int): T;

    /** Set the Int element of the rail, failing unless 0 <= Int < length. */
    @Native("java", "#0.set(#1, #2)")
    @Native("c++", "(#0)->set(#1, #2)")
    public native def set(T, Int): T;

    /** Get the length of the rail (which may be less than the allocated storage for the rail. */
    @Native("java", "#0.length()")
    @Native("c++", "(#0)->length()")
    public native def length(): Int;
    
    /** Remove the last element of the rail, decrementing the length. */
    @Native("java", "#0.removeLast()")
    @Native("c++", "(#0)->removeLast()")
    public native def removeLast(): Void;

    /** Convert to a mutable rail.  This copies the content of the rail. */
    @Native("java", "#0.toRail()")
    @Native("c++", "(#0)->toRail()")
    public native def toRail(): Rail[T]!;

    /** Convert to an immutable rail.  This copies the content of the rail. */
    @Native("java", "#0.toValRail()")
    @Native("c++", "(#0)->toValRail()")
    public native def toValRail(): ValRail[T]!;
}

