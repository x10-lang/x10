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
 
package x10.compiler;

@NativeRep("java", "x10.core.LocalVar<#1>", null, "new x10.rtt.ParameterizedType(x10.core.LocalVar.$RTT, #2)")
public class LocalVar[T] {

    public def this(local:T):LocalVar[T] {}

    @Native("java", "(#0).$apply$G()")
    public operator this():T = null as Any as T;
    
    @Native("java", "(#0).get$G()")
    public def get():T = null as Any as T;
    
    @Native("java", "(#0).set$G(#1)")
    public operator this(local:T):T {return local;}
}