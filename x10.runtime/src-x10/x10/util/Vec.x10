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

@NativeRep("java", "x10.core.Vec<#T$box>", null, "x10.rtt.ParameterizedType.make(x10.core.Vec.$RTT, #T$rtt)")
@NativeRep("c++", "x10::util::NativeVec<#T, ##size#-1#>", "x10::util::NativeVec<#T, ##size#-1#>", null)
@Mutable public struct Vec[T] (@Native("c++","#this.size()")size:Int) {

    private backing : Rail[T];
    private def this(s:Int) {T haszero} : Vec[T]{self.size==s} {
        property(s);
        backing = new Rail[T](size);
    }

    @Native("java", "new x10.core.Vec<#U$box>(#U$rtt, #s)")
    @Native("c++", "x10::util::NativeVec<#U, #s>(#s)")
    public static def make[U](s:Int) {U haszero}: Vec[U]{self.size==s} = new Vec[U](s);

    @Native("java", "#this.get(#i)")
    @Native("c++", "#this.get(#i)")
    public operator this(i:Int) : T = backing(i);

    @Native("java", "#this.set(#i,#v)")
    @Native("c++", "#this.set(#v,#i)")
    public operator this(i:Int) = (v:T) : T = backing(i) = v;

    @Native("java", "(#this).toString()")
    @Native("c++", "(#this)->toString()")
    public native def toString():String;

    @Native("java", "(#this).equals(#that)")
    @Native("c++", "(#this)->equals(#that)")
    public native def equals(that:Any):Boolean;

    @Native("java", "(#this).hashCode()")
    @Native("c++", "(#this)->hashCode()")
    public native def hashCode():Int;
}

