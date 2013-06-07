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

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;
import x10.compiler.Inline;

@NativeCPPInclude("x10/lang/UnsafeNatives.h")
public final class Unsafe {

    @Native("c++", "x10::lang::Rail<#T >::_makeUnsafe(#size, false)")
    @Native("java", "x10.core.Rail.<#T$box>makeUnsafe(#T$rtt, #size, false)")
    public native static def allocRailUninitialized[T](size:Long):Rail[T]{self.size==size, self!=null};

    @Native("c++", "x10::lang::Rail<#T >::_makeUnsafe(#size, true)")
    @Native("java", "x10.core.Rail.<#T$box>makeUnsafe(#T$rtt, #size, true)")
    public native static def allocRailZeroed[T](size:Long):Rail[T]{self.size==size,self!=null}; 

    @Native("c++", "(#x)->clear()")
    @Native("java", "(#x).clear()")
    public native static def clearRail[T](x:Rail[T]):void;

    @Native("c++", "(#x)->clear(#start, #numElems)")
    @Native("java", "(#x).clear(#start, #numElems)")
    public native static def clearRail[T](x:Rail[T], start:long, numElems:long):void;

    @Native("c++", "(#r)->x10::lang::Rail<#T >::unchecked_apply(#i)")
    public static @Inline def uncheckedRailApply[T](r:Rail[T], i:long):T = r(i);

    @Native("c++", "(#r)->x10::lang::Rail<#T >::unchecked_set(#i, #v)")
    public static @Inline def uncheckedRailSet[T](r:Rail[T], i:long, v:T):T {
        r(i) = v;
        return v;
    }

    @Native("c++", "x10aux::dealloc(#o)")
    public static def dealloc[T](o:T){ T isref } :void {}

    @Native("c++", "x10::lang::UnsafeNatives::getCongruentSibling(#r, #dst)")
    private static def getCongruentSibling[T](r:Rail[T]{self!=null}, dst:long):Rail[T]{self.size==r.size,self!=null} {
        throw new UnsupportedOperationException("Congruent memory not available on Managed X10");
    }

/*
    @Native("java", "null")
    @Native("c++", "x10::lang::GlobalRef((x10aux::place)((#p).FMGL(id)), (x10_ulong)(#t)")
    private static native def fabricateGlobalRef[T](t:T, p:Place):GlobalRef[T]{self.home==p};

    public static def getCongruentSibling[T](r:Rail[T]{self!=null}, dst:Place):GlobalRail[T]{self.size==r.size,self.home()==dst} {
        val remoteRail = getCongruentSibling(r, dst.id);
        val globalRef = fabricateGlobalRef[Rail[T]{self!=null}](remoteRail, dst);
        val globalRail = GlobalRail[T](r.size, globalRef);
        return globalRail;
    }
*/
}
