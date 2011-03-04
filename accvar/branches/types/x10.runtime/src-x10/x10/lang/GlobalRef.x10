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

import x10.compiler.NativeRep;
import x10.compiler.Native;
/**
 * Create a global reference encapsulating a given object.  The ref has
 * the property home specifying the place at which it was
 * created. Besides that, the ref offers only the operations of Any at a
 * place other than the one where it was created (its home place).  Two
 * such refs are == if and only if they were created at the same place
 * and at that place the objects they encapsulate are ==.  
 *
 * <p> At its home place, the value when applied to the empty list of
 * arguments returns its encapsulated value.
 */
@NativeRep("java", "x10.core.GlobalRef<#1>", null, "new x10.rtt.ParameterizedType(x10.core.GlobalRef._RTT, #2)")
@NativeRep("c++", "x10::lang::GlobalRef<#1 >", "x10::lang::GlobalRef<#1 >", null)
public struct GlobalRef[+T](
    @Native("java", "(#0).home")
    @Native("c++", "x10::lang::Place_methods::place((#0)->location)")
    home:Place) {T <: Object} { 

   /** 
    * Create a value encapsulating the given object of type T.
    */
   @Native("c++", "x10::lang::GlobalRef(#0)")
   public native def this(t:T):GlobalRef[T]{self.home==here}; 

   /** 
    * Can only be invoked at the place at which the value was
    * created. Returns the object encapsulated in the value.
    */
   @Native("java", "(#0).apply$G()")
   @Native("c++", "(#0)->apply()")
   public native operator this(){here == this.home}:T;

   /*
    * @Native methods from Any because the handwritten C++ code doesn't 100% match 
    * what the compiler would have generated.
    */

    @Native("java", "(#0).toString()")
    @Native("c++", "(#0)->toString()")
    public native def  toString():String;

    @Native("java", "(#0).equals(#1)")
    @Native("c++", "(#0)->equals(#1)")
    public native def equals(that:Any):Boolean;

    @Native("java", "(#0).hashCode()")
    @Native("c++", "(#0)->hashCode()")
    public native def  hashCode():Int;
}
