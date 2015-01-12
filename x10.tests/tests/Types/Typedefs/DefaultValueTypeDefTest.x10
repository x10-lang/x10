/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;
import x10.util.Box;

/**
 * 
 *
 * @author vj 09/2008
 */

public class DefaultValueTypeDefTest extends x10Test {

    static struct Foo[T](n:int, s:Box[T]) {
       def this(n:int, s:Box[T]):Foo[T]{self.n==n,self.s==s} {
         property(n,s);
       }
    }
    public def run() = {
        val b:Box[String] = new Box[String]("a");
        val x:Foo[String]{self.n==2n && self.s==b} = Foo[String](2n,b);
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new DefaultValueTypeDefTest().execute();
    }
}
