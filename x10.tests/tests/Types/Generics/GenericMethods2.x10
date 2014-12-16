/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;



/**
 * @author bdlucas 8/2008
 */

public class GenericMethods2 extends GenericTest {

    public def run() = {

        class A {
            def m[T](t:T) = t;
        }

        val a = new A();

        genericCheck("a.m[long](1)", a.m[long](1), 1);
        genericCheck("a.m[String](\"1\")", a.m[String]("1"), "1");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericMethods2().execute();
    }
}
