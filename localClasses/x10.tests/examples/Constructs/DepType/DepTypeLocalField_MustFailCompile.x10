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
 * Tests that a field not known to be local accessed in DepType
 * causes a compilation failure.
 *
 * @author igor
 */
public class DepTypeLocalField_MustFailCompile extends x10Test {
    val j:int;
    /* free variable j is not local */
    class Tester(i:int){j == 2} {
        public def this(arg:int):Tester { property(arg); }
    }

    public def this() { j = 2; }

    public def run() = true;

    public static def main(var args: Rail[String]): void = {
        new DepTypeLocalField_MustFailCompile().execute();
    }
}
