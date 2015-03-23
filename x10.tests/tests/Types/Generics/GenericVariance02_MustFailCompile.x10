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

public class GenericVariance02_MustFailCompile extends GenericTest {

    class X {}
    class Y extends X {}
    class Z extends Y {}

    class A[T] {}

    public def run() {
        val ax:A[X] = new A[Y](); // ERR
        return result;
    }

    public static def main(var args: Rail[String]): void {
        new GenericVariance02_MustFailCompile().execute();
    }
}
