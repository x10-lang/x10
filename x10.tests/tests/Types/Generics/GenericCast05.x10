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

public class GenericCast05 extends GenericTest {

    interface I[T] {
        def m(T):long;
        def n(T):long;
    }

    interface J[T] {
        def m(T):long;
        def o(T):long;
    }

    class A implements I[long], J[long] {
        public def m(long) = 0;
        public def n(long) = 1;
        public def o(long) = 2;
    }

    public def run() {

        var a:Any = new A();
        var i:I[long] = a as I[long]; // ERR: Warning: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
        var j:J[long] = a as J[long]; // ERR: Warning: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.

        genericCheck("i.m(0)", i.m(0), 0);
        genericCheck("i.n(0)", i.n(0), 1);

        genericCheck("j.m(0)", j.m(0), 0);
        genericCheck("j.o(0)", j.o(0), 2);

        return result;
    }

    public static def main(var args: Rail[String]): void {
        new GenericCast05().execute();
    }
}
