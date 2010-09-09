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
 * 
 *
 * @author vj 09/2008
 */
public class DefaultRefTypeDefTest extends x10Test {

    static class Foo[T](n:int, s:T) {
       def this(n:int, s:T):Foo[T](n,s) = {
         property(n,s);
       }
    }
    public def run() = {
        x:Foo[String](2,"a") = new Foo[String](2,"a");
        true
    }

    public static def main(var args: Rail[String]): void = {
        new DefaultRefTypeDefTest().execute();
    }
}
