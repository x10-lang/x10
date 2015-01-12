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

//LIMITATION:

import harness.x10Test;



/**
 * @author bdlucas 8/2008
 */

public class GenericInheritance08 extends GenericTest {

    interface I[T] {
        def m(T):long;
    }

    interface J[T] {
        def m(T):long;
    }

    class A implements I[long], J[String] {
        public def m(long) = 0;
        public def m(String) = 1;
    }

    public def run() = {
        
        val a = new A();
        val i:I[long] = a;
        val j:J[String] = a;

        genericCheck("a.m(0)", a.m(0), 0);
        genericCheck("a.m(\"0\")", a.m("0"), 1);
        genericCheck("i.m(0)", i.m(0), 0);
        genericCheck("j.m(0)", j.m("0"), 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericInheritance08().execute();
    }
}
