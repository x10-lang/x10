/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright ANU 2012
 *  (C) Copyright IBM Corporation 2012.
 */

import harness.x10Test;

import x10.util.Pair;

/**
 * Declare an instance field that is a generic struct, that is in 
 * turn instantiated on a struct.
 * Test for C++ codegen to make sure header files are included.
 */
public class TestStructPair extends x10Test {
    public static struct A { };
    public static class B { };

    val x:Pair[A,B] = new Pair[A,B](new A(), new B());

    public def run() {
        // testing codegen/compilation; no runtime test needed.
        return true;
    }

    public static def main(args: Rail[String]): void = {
        new TestStructPair().execute();
    }
}
