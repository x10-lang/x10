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
 * Array Initializer test.
 */

public class ArrayInitializer1a extends x10Test {

    public def run(): boolean = {

        val e = 0..9;
        val r = e*e*e;
        val ia = new Array[Int](r, ([i,j,k]: Point)=> i);

        for (val p[i,j,k] in ia.region) chk(ia(p) == i); // should infer p:Point(3)

        return true;
    }

    public static def main(Array[String](1)): Void = {
        new ArrayInitializer1a().execute();
    }
}
