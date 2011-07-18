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
 * @author bdlucas 8/2008
 */

public class GenericMethods6 extends GenericTest {

    public def run() = {

        class A[U] {
            def m[T](u:U,t:T) = t;
        }

        val a1 = new A[String]();
        check("a1.m[int](\"1\",1)", a1.m[int]("1",1), 1);

        val a2 = new A[int]();
        check("a2.m[String](1,\"1\")", a2.m[String](1,"1"), "1");

        return result;
    }

    public static def main(var args: Array[String](1)): void = {
        new GenericMethods6().execute();
    }
}
