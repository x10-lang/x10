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

@NativeRep("java", "x10.core.ValRail<#1>", "x10.core.ValRail.BoxedValRail", "new x10.core.ValRail.RTT(#2)")
@NativeRep("c++", "x10aux::ref<x10::lang::ValRail<#1 > >", "x10::lang::ValRail<#1 >", null)
public final class ValRail[+T](length: Int) implements (Int) => T, Iterable[T] {

    // need to declare a constructor to shut up the initialization checker
    private native def this(n: Int): ValRail[T]{self.length==n};
    
/*
    @Native("java", "x10.core.RailFactory.<#2>makeValRail(#3, #4, #5)")
    @Native("c++", "x10::lang::ValRail<#1 >::make(#4, #5)")
    public native static def make[T](length: Int, init: (Int) => T, value: boolean): ValRail[T](length);
*/
    
    @Native("java", "x10.core.RailFactory.<#2>makeValRail(#3, #4, #5)")
    @Native("c++", "x10::lang::ValRail<#1 >::make(#4, #5)")
    public native static def make[T](length: Int, init: (Int) => T): ValRail[T](length);

    @Native("java", "x10.core.RailFactory.<#2>makeValRailFromRail(#3, #4)")
    @Native("c++", "x10::lang::ValRail<#1 >::make(#4)")
    public native static operator[U](r: Rail[U]): ValRail[U]{self.length==r.length};

/*
    @Native("java", "#0.get(#1)")
    @Native("c++", "(#0)->get(#1)")
    public global native safe def get(i: Int): T;
*/

    @Native("java", "#0.apply(#1)")
    @Native("c++", "(*#0)[#1]")
    @Native("cuda", "(#0)[#1]")
    public global native safe def apply(i: Int): T;
    
    @Native("java", "#0.iterator()")
    @Native("c++", "(#0)->iterator()")
    public global native def iterator(): Iterator[T];

    /**
     * Equals on ValRail is defined by (1) equal length and
     * (2) pairwise comparision by equals of the elements of the
     * two ValRails.
     *
     * @param other the ValRail[T] to compare this to.
     * @return <code>true</code> if <code>this</code> is equal
     * to <code>other</code> and <code>false</code> otherwise.
     */
    @Native("java", "#0.equals(#1)")
    @Native("c++", "(#0)->equals(#1)")
    public global safe native def equals(other:Any):boolean;

    @Native("java", "x10.lang.System.copyTo(#0,#1,#2,#3,#4,#5)")
    @Native("c++", "x10::lang::System::copyTo(#0,#1,#2,#3,#4,#5)")
    //@Native("c++", "(#0)->copyTo(#1, #2, #3, #4, #5)")
    public global native def copyTo (src_off:Int, dst:Rail[T], dst_off:Int, len:Int) : Void;

    @Native("java", "x10.lang.System.copyTo(#0,#1,#2,#3,#4,#5,#6)")
    @Native("c++", "x10::lang::System::copyTo(#0,#1,#2,#3,#4,#5)")
    //@Native("c++", "(#0)->copyTo(#1,#2,#3,#4,#5,#6)")
    public global native def copyTo (src_off:Int,
                                     dst_place:Place, dst_finder:()=>Pair[Rail[T],Int],
                                     len:Int) : Void;

    private static class RailIterator[S] implements Iterator[S] {
        private var curIndex:int = 0;
        private val rail:ValRail[S]!;
	
	private def this(r:ValRail[S]!) { rail = r; }
        public def hasNext() = curIndex < rail.length;
	public def next() = rail(curIndex++);
    }
}
