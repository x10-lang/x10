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
import x10.util.Pair;

@NativeRep("java", "x10.core.Rail<#1>", "x10.core.Rail.BoxedRail", "new x10.core.Rail.RTT(#2)")
@NativeRep("c++", "x10aux::ref<x10::lang::Rail<#1 > >", "x10::lang::Rail<#1 >", null)
public final class Rail[T](length: Int)
    implements Settable[Int,T], Iterable[T]
{
    // need to declare a constructor to shut up the initialization checker
    private native def this(n: Int): Rail[T]{self.length==n};


    /*
    @Native("java", "x10.core.RailFactory.<#2>makeValRail(#3, #4, #5)")
    @Native("c++", "x10::lang::ValRail<#1 >::make(#4, #5)")
    public native static safe def makeVal[S](length: Int, init: (Int) => S): ValRail[S]{self.length==length};

    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4, #5)")
    @Native("c++", "x10::lang::Rail<#1 >::make(#4, #5)")
    public native static safe def makeVar[S](length: Int, init: (Int) => S): Rail[S]!{self.length==length};
    */

    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4, #5)")
    @Native("c++", "x10::lang::Rail<#1 >::make(#4, #5)")
    public native static safe def make[S](length: Int, init: (Int) => S): Rail[S]!{self.length==length};

    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4, #5, #6)")
    @Native("c++", "x10::lang::Rail<#1 >::make(#4, #5, #6)")
    public native static safe def make[S](length: Int, off:Int, init:Rail[S]): Rail[S]!{self.length==length};

    // FIXME: hack! uninitialised rail is unsound (used by x10.array.DistArray)
    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4)")
    @Native("c++", "x10::lang::Rail<#1 >::make(#4)")
    public native static safe def make[S](length: Int): Rail[S]!{self.length==length};

    @Native("java", "#0.reset(#1)")
    @Native("c++", "(#0)->reset(#1)")
    public native safe def reset(init: (Int) => T): Void;

    @Native("java", "#0.reset(#1)")
    @Native("c++", "(#0)->reset(#1)")
    public native safe def reset(init: T): Void;

/*
    @Native("java", "x10.core.RailFactory.<#2>makeValRail(#3, #4)")
    @Native("c++", "x10::lang::ValRail<#1 >::make(#4)")
    public native static safe def makeVal[S](length: Int): ValRail[S]{self.length==length};

*/

/*
    @Native("java", "x10.core.RailFactory.<#2>makeRailFromValRail(#3, #4)")
    @Native("c++", "x10::lang::Rail<#1 >::make(#4)")
    public native static safe def make[U](r: ValRail[U]): Rail[U]!{self.length==r.length};
*/

    @Native("java", "x10.core.RailFactory.<#2>makeRailFromValRail(#3, #4)")
    @Native("c++", "x10::lang::Rail<#1 >::make(#4)")
    public native static safe operator [U](r: ValRail[U]): Rail[U]!{self.length==r.length};

/*
    @Native("java", "#0.get(#1)")
    @Native("c++", "(*#0)[#1]")
    public native safe def get(i: Int): T;
*/

    @Native("java", "#0.apply(#1)")
    @Native("c++", "(*#0)[#1]")
    @Native("cuda", "(#0)[#1]")
    public native safe def apply(i: Int): T;

    @Native("java", "#0.set(#1, #2)")
    @Native("c++", "(*#0)[#2] = #1")
    @Native("cuda", "(#0)[#2] = #1")
    public native safe def set(v: T, i: Int): T;

    @Native("java", "#0.iterator()")
    @Native("c++", "(#0)->iterator()")
    public native safe def iterator(): Iterator[T];


    @Native("java", "x10.lang.System.makeRemoteRail(#3, #4,#5,#6)")
    @Native("c++", "x10::lang::System::makeRemoteRail(#4,#5,#6)")
    public native static safe def makeRemote[T] (p:Place, length:Int, init: (Int) => T) : Rail[T]!p{self.length==length};

    @Native("java", "x10.lang.System.makeRemoteRail(#3, #4,#5,#6)")
    @Native("c++", "x10::lang::System::makeRemoteRail(#4,#5,#6)")
    public native static safe def makeRemote[T] (p:Place, length:Int, init: Rail[T]!) : Rail[T]!p{self.length==length};

    // Transfer functions

    @Native("java", "x10.lang.System.copyTo(#7, #0,#1,#2,#3,#4)")
    @Native("c++", "(#0)->copyTo(#1,#2,#3,#4)")
    public native def copyTo (src_off:Int, dst:Rail[T], dst_off:Int, len:Int) : Void;

    @Native("java", "x10.lang.System.copyTo(#7, #0,#1,#2,#3,#4)")
    @Native("c++", "(#0)->copyTo(#1,#2,#3,#4)")
    public native def copyTo (src_off:Int,
                              dst_place:Place, dst_finder:()=>Pair[Rail[T],Int],
                              len:Int) : Void;

    @Native("java", "x10.lang.System.copyTo(#8, #0,#1,#2,#3,#4,#5)")
    @Native("c++", "(#0)->copyTo(#1,#2,#3,#4,#5)")
    public native def copyTo (src_off:Int,
                              dst_place:Place, dst_finder:()=>Pair[Rail[T],Int],
                              len:Int, notifier:()=>Void) : Void;

    @Native("java", "x10.lang.System.copyTo(#8, #0,#1,#2,#3,#4,#5)")
    @Native("c++", "x10::lang::System::copyTo(#0,#1,#2,#3,#4,#5)")
    public native def copyTo (src_off:Int,
                              dst_place:Place, dst_handle:PlaceLocalHandle[Rail[T]], dst_off:Int,
                              len:Int) : Void;

    @Native("java", "x10.lang.System.copyTo(#9, #0,#1,#2,#3,#4,#5,#6)")
    @Native("c++", "x10::lang::System::copyTo(#0,#1,#2,#3,#4,#5,#6)")
    public native def copyTo (src_off:Int,
                              dst_place:Place, dst_handle:PlaceLocalHandle[Rail[T]], dst_off:Int,
                              len:Int, notifier:()=>Void) : Void;

    @Native("java", "x10.lang.System.copyTo(#8 #0,#1,#2,#3,#4,#5)")
    @Native("c++", "x10::lang::System::copyTo(#0,#1,#2,#3,#4,#5)")
    public native def copyTo (src_off:Int, dst:Rail[T], dst_off:Int,
                              len:Int, notifier:()=>Void) : Void;

    @Native("java", "x10.lang.System.copyFrom(#7, #0,#1,#2,#3,#4)")
    @Native("c++", "(#0)->copyFrom(#1,#2,#3,#4)")
    public native def copyFrom (dst_off:Int, src:Rail[T], src_off:Int, len:Int) : Void;

    @Native("java", "x10.lang.System.copyFrom(#7, #0,#1,#2,#3,#4)")
    @Native("c++", "(#0)->copyFrom(#1,#2,#3,#4)")
    public native def copyFrom (dst_off:Int,
                                src_place:Place, src_finder:()=>Pair[Rail[T],Int],
                                len:Int) : Void;

    @Native("java", "x10.lang.System.copyFrom(#7, #0,#1,#2,#3,#4)")
    @Native("c++", "(#0)->copyFrom(#1,#2,#3,#4)")
    public native def copyFrom (dst_off:Int, src:ValRail[T], src_off:Int, len:Int):Void;

    @Native("java", "x10.lang.System.copyFrom(#7, #0,#1,#2,#3,#4)")
    @Native("c++", "(#0)->copyFrom(#1,#2,#3,#4)")
    public native def copyFrom (dst_off:Int,
                                src_place:Place, src_finder:()=>Pair[ValRail[T],Int],
                                len:Int) : Void;

    @Native("java", "#0.view()")
    @Native("c++", "#0->view()")
    public native def view(): ValRail[T]{self.length==this.length};

    private static class RailIterator[S] implements Iterator[S] {
        private var curIndex:int = 0;
        private val rail:Rail[S]!;

	private def this(r:Rail[S]!) { rail = r; }
        public def hasNext() = curIndex < rail.length;
	public def next() = rail(curIndex++);
    }
}
