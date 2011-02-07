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

package x10.util;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("c++", "NativeVec<#1, 8>", "NativeVec<#1, 8>", null)
public struct Vec[T] (@Native("c++","8")size:Int) {

    private backing : Array[T]{self.rank==1, self.size==this.size};
    private def this(s:Int) {T haszero} : Vec[T]{self.size==s} {
        property(s);
        backing = new Array[T](size);
    }

    @Native("c++", "NativeVec<#1, #4>()")
    public static def make[U](s:Int) {U haszero} = new Vec[U](s);

    @Native("c++", "#0.get(#1)")
    public operator this(i:Int) : T = backing(i);

    @Native("c++", "#0.set(#1,#2)")
    public operator this(i:Int) = (v:T) : T = backing(i) = v;
}

