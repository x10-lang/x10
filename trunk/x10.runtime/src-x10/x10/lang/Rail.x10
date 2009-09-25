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

@NativeRep("java", "x10.core.Rail<#1>", "x10.core.Rail.BoxedRail", "new x10.core.Rail.RTT(#2)")
@NativeRep("c++", "x10aux::ref<x10::lang::Rail<#1 > >", "x10::lang::Rail<#1 >", null)
public final class Rail[T](length: nat)
    implements Settable[nat,T], Iterable[T]
{

    // need to declare a constructor to shut up the initialization checker
    private native def this(n: nat): Rail[T]{self.length==n};
    
    @Native("java", "x10.core.RailFactory.<#2>makeValRail(#3, #4, #5)")
    @Native("c++", "x10::lang::ValRail<#1 >::make(#4, #5)")
    public native static safe def makeVal[S](length: nat, init: (nat) => S): ValRail[S]{self.length==length};

    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4, #5)")
    @Native("c++", "x10::lang::Rail<#1 >::make(#4, #5)")
    public native static safe def makeVar[S](length: nat, init: (nat) => S): Rail[S]!{self.length==length};

    @Native("java", "x10.core.RailFactory.<#2>makeValRail(#3, #4)")
    @Native("c++", "x10::lang::ValRail<#1 >::make(#4)")
    public native static safe def makeVal[S](length: nat): ValRail[S]{self.length==length};
    
    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4)")
    @Native("c++", "x10::lang::Rail<#1 >::make(#4)")
    public native static safe def makeVar[S](length: nat): Rail[S]!{self.length==length};
    
    @Native("java", "x10.core.RailFactory.<#2>makeRailFromValRail(#3, #4)")
    @Native("c++", "x10::lang::Rail<#1 >::make(#4)")
    public native static safe def make[U](r: ValRail[U]): Rail[U]!{self.length==r.length};

    @Native("java", "x10.core.RailFactory.<#2>makeRailFromValRail(#3, #4)")
    @Native("c++", "x10::lang::Rail<#1 >::make(#4)")
    public native static safe operator [U](r: ValRail[U]): Rail[U]!{self.length==r.length};

    @Native("java", "#0.get(#1)")
    @Native("c++", "(*#0)[#1]")
    public native safe def get(i: nat): T;

    @Native("java", "#0.apply(#1)")
    @Native("c++", "(*#0)[#1]")
    @Native("cuda", "(#0)[#1]")
    public native safe def apply(i: nat): T;

    @Native("java", "#0.set(#1, #2)")
    @Native("c++", "(*#0)[#2] = #1")
    @Native("cuda", "(#0)[#2] = #1")
    public native safe def set(v: T, i: nat): T;
    
    @Native("java", "#0.iterator()")
    @Native("c++", "(#0)->iterator()")
    public native safe def iterator(): Iterator[T];

    @Native("java", "x10.lang.System.copyTo(#0,#1,#2,#3,#4)")
    @Native("c++", "x10::lang::System::copyTo(#0,#1,#2,#3,#4)")
    //@Native("c++", "(#0)->copyTo(#1,#2,#3,#4,#5)")
    public native def copyTo (src_off:Int, dst:Rail[T], dst_off:Int, len:Int) : Void;

/* FIXME: This interface is not possible to define properly without structs:
 * the closure needs to return both an offset and a Rail. 
 * For now we assume the offset on the remote side is 0.
 */
    @Native("java", "x10.lang.System.copyTo(#0,#1,#2,#3,#4)")
    @Native("c++", "x10::lang::System::copyTo(#0,#1,#2,#3,#4)")
    //@Native("c++", "(#0)->copyTo(#1,#2,#3,#4)")
    public native def copyTo (src_off:Int,
                              dst_place:Place, dst_finder:()=>Rail[T],
                              len:Int) : Void;

    @Native("java", "x10.lang.System.copyFrom(#0,#1,#2,#3,#4)")
    @Native("c++", "x10::lang::System::copyFrom(#0,#1,#2,#3,#4)")
    //@Native("c++", "(#0)->copyFrom(#1,#2,#3,#4)")
    public native def copyFrom (dst_off:Int, src:Rail[T], src_off:Int, len:Int) : Void;

/* FIXME: This interface is not possible to define properly without structs:
 * the closure needs to return both an offset and a Rail. 
 * For now we assume the offset on the remote side is 0.
 */
    @Native("java", "x10.lang.System.copyFrom(#0,#1,#2,#3,#4)")
    @Native("c++", "x10::lang::System::copyFrom(#0,#1,#2,#3,#4)")
    //@Native("c++", "(#0)->copyFrom(#1,#2,#3,#4)")
    public native def copyFrom (dst_off:Int,
                                src_place:Place, src_finder:()=>Rail[T],
                                len:Int) : Void;

    @Native("java", "x10.lang.System.copyFrom(#0,#1,#2,#3,#4)")
    @Native("c++", "x10::lang::System::copyFrom(#0,#1,#2,#3,#4)")
    //@Native("c++", "(#0)->copyFrom(#1,#2,#3,#4)")
    public native def copyFrom (dst_off:Int, src:ValRail[T], src_off:Int, len:Int) : Void;

/* FIXME: This interface is not possible to define properly without structs:
 * the closure needs to return both an offset and a ValRail. 
 * For now we assume the offset on the remote side is 0.
 */
    @Native("java", "x10.lang.System.copyFrom(#0,#1,#2,#3,#4)")
    @Native("c++", "x10::lang::System::copyFrom(#0,#1,#2,#3,#4)")
    //@Native("c++", "(#0)->copyFrom(#1,#2,#3,#4)")
    public native def copyFrom (dst_off:Int,
                                src_place:Place, src_finder:()=>ValRail[T],
                                len:Int) : Void;
}
