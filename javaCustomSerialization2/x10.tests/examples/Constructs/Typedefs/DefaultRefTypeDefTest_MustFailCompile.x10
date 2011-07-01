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

import harness.x10Test;

/**
 * A class with properties does NOT automatically define a typedef.
 *
 * @author vj 09/2008
 */
public class DefaultRefTypeDefTest_MustFailCompile extends x10Test {

    static class Foo(n:int, s:String) {
       def this(n:int, s:String):Foo(n,s) = { // ERR ERR: Could not find type "Foo(x10.lang.Int,T)". Constructor return type is not a subtype of the containing class.
         property(n,s);
       }
    }
    public def run() = {
        x:Foo(2,"a") = new Foo(2,"a"); // ERR ERR: Could not find type "Foo(x10.lang.Int,T)". Cannot assign expression to target.
        true
    }

    public static def main(var args: Array[String](1)): void = {
        new DefaultRefTypeDefTest_MustFailCompile().execute();
    }
}
