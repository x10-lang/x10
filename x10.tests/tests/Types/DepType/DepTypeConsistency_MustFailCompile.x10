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

/**
 * Tests that a free variable in DepType that is not parametrically consistent
 * causes a compilation failure.
 *
 * @author pvarma
 */
public class DepTypeConsistency_MustFailCompile extends x10Test {
    static val j:int = 3n;
    /* free variable j is not parametrically consistent */
    class Tester(i:int){j == 2n} { // ERR ERR ERR ERR
        public def this(arg:int):Tester { property(arg); }  // ERR
    }

    public def run() = true;

    public static def main(var args: Rail[String]): void = {
        new DepTypeConsistency_MustFailCompile().execute();
    }
}
