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

public class GenericInstanceof03 extends GenericTest {

    interface I[T] {
        def m(T):long;
    }

    class A[T] implements I[T] {
        public def m(T) = 0;
    }

    public def run() = {

        var a:Any = new A[long]();

        return a instanceof I[long];
    }

    public static def main(var args: Rail[String]): void = {
        new GenericInstanceof03().execute();
    }
}
