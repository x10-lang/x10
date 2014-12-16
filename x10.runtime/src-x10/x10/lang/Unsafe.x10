/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;
import x10.compiler.Inline;

@NativeCPPInclude("x10/lang/UnsafeNatives.h")
public final class Unsafe {

    @Native("c++", "::x10::lang::Rail< #T >::_makeUnsafe(#size, false)")
    @Native("java", "x10.core.Rail.<#T$box>makeUnsafe(#T$rtt, #size, false)")
    public native static def allocRailUninitialized[T](size:Long):Rail[T]{self.size==size, self!=null};

    @Native("c++", "::x10::lang::Rail< #T >::_makeUnsafe(#size, true)")
    @Native("java", "x10.core.Rail.<#T$box>makeUnsafe(#T$rtt, #size, true)")
    public native static def allocRailZeroed[T](size:Long):Rail[T]{self.size==size,self!=null}; 

    @Native("c++", "(#x)->clear()")
    @Native("java", "(#x).clear()")
    public native static def clearRail[T](x:Rail[T]):void;

    @Native("c++", "(#x)->clear(#start, #numElems)")
    @Native("java", "(#x).clear(#start, #numElems)")
    public native static def clearRail[T](x:Rail[T], start:Long, numElems:Long):void;

    @Native("c++", "(#r)->unchecked_apply(#i)")
    public static @Inline def uncheckedRailApply[T](r:Rail[T], i:Long):T = r(i);

    @Native("c++", "(#r)->unchecked_set(#i, #v)")
    public static @Inline def uncheckedRailSet[T](r:Rail[T], i:Long, v:T):T {
        r(i) = v;
        return v;
    }

    @Native("c++", "::x10aux::dealloc(#o)")
    public static def dealloc[T](o:T){ T isref } :void {}

    @Native("c++", "::x10::lang::UnsafeNatives::getCongruentSibling(#r, #dst)")
    private static def getCongruentSibling[T](r:Rail[T]{self!=null}, dst:Long):Rail[T]{self.size==r.size,self!=null} {
        throw new UnsupportedOperationException("Congruent memory not available on Managed X10");
    }

    @Native("java", "null")
    @Native("c++", "::x10::lang::GlobalRef< #T >((::x10aux::place)((#p).FMGL(id)), (x10_ulong)(#t))")
    private static native def fabricateGlobalRef[T](t:T, p:Place){T isref}:GlobalRef[T]{self.home==p};

    public static @Inline def getCongruentSibling[T](r:Rail[T]{self!=null}, dst:Place):GlobalRail[T]{self.size==r.size,self.home()==dst} {
        val remoteRail:Rail[T]{self!=null} = getCongruentSibling(r, dst.id); // XTENLANG-3360, need explicit type.
        val globalRef = fabricateGlobalRef[Rail[T]{self!=null}](remoteRail, dst);
        val globalRail = GlobalRail[T](r.size, globalRef);
        return globalRail;
    }
}
