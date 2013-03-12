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

public final class Unsafe {

    @Native("c++", "x10::lang::Rail<#T >::_makeUnsafe(#size, false)")
    @Native("java", "x10.core.Rail.<#T$box>makeUnsafe(#T$rtt, #size, false)")
    public native static def allocRailUninitialized[T](size:Long):Rail[T]{self.size==size};

    @Native("c++", "x10::lang::Rail<#T >::_makeUnsafe(#size, false)")
    @Native("java", "x10.core.Rail.<#T$box>makeUnsafe(#T$rtt, #size, false)")
    public native static def allocRailUninitialized[T](size:Int):Rail[T];

    @Native("c++", "x10::lang::Rail<#T >::_makeUnsafe(#size, true)")
    @Native("java", "x10.core.Rail.<#T$box>makeUnsafe(#T$rtt, #size, true)")
    public native static def allocRailZeroed[T](size:Long):Rail[T]{self.size==size}; 

    @Native("c++", "x10::lang::Rail<#T >::_makeUnsafe(#size, true)")
    @Native("java", "x10.core.Rail.<#T$box>makeUnsafe(#T$rtt, #size, true)")
    public native static def allocRailZeroed[T](size:Int):Rail[T];

    @Native("c++", "(#x)->clear()")
    @Native("java", "(#x).clear()")
    public native static def clearRail[T](x:Rail[T]):void;

    @Native("c++", "(#x)->clear(#start, #numElems)")
    @Native("java", "(#x).clear(#start, #numElems)")
    public native static def clearRail[T](x:Rail[T], start:long, numElems:long):void;

    @Native("c++", "(#x)->clear(#start, #numElems)")
    @Native("java", "(#x).clear(#start, #numElems)")
    public native static def clearRail[T](x:Rail[T], start:int, numElems:int):void;

    @Native("c++", "x10aux::dealloc(#o)")
    public static def dealloc[T](o:T){ T isref } :void {}
}
