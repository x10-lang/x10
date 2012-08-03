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

public class GenericCast07 extends GenericTest {

    interface I[T] {
        def m(T):int;
    }

    interface J[T] {
        def m(T):int;
    }

    class A[T] implements I[T], J[T] {
        public def m(T) = 0;
    }

    public def run() = {
        
        var a:Any = new A[int]();
        var i:I[int] = a as I[int]; // ERR: Warning: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
        var j:J[int] = a as J[int]; // ERR: Warning: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.

        check("i.m(0)", i.m(0), 0);
        check("j.m(0)", j.m(0), 0);

        return result;
    }

    public static def main(var args: Array[String](1)): void = {
        new GenericCast07().execute();
    }
}
