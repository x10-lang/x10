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
import x10.compiler.Mutable;

@NativeRep("c++", "x10::util::NativeVec<#T, ##size#-1#>", "x10::util::NativeVec<#T, ##size#-1#>", null)
@NativeRep("java", "x10.core.Vec<#1>", null, "new x10.rtt.ParameterizedType(x10.core.Vec.$RTT, #2)")
@Mutable public struct Vec[T] (@Native("c++","#this.size()")size:Int) {

    private backing : Array[T]{self.rank==1, self.size==this.size};
    private def this(s:Int) {T haszero} : Vec[T]{self.size==s} {
        property(s);
        backing = new Array[T](size);
    }

    @Native("c++", "x10::util::NativeVec<#U, #s>(#s)")
    @Native("java", "new x10.core.Vec<#2>(#3, #4)")
    public static def make[U](s:Int) {U haszero} = new Vec[U](s);

    @Native("c++", "#this.get(#i)")
    @Native("java", "#0.get(#1)")
    public operator this(i:Int) : T = backing(i);

    @Native("c++", "#this.set(#v,#i)")
    @Native("java", "#0.set(#1,#2)")
    public operator this(i:Int) = (v:T) : T = backing(i) = v;
}

