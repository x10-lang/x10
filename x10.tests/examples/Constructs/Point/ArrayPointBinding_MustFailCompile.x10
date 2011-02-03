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
 * Cannot bind point components in array declaration.
 *
 * @author igor, 1/2006
 */

public class ArrayPointBinding_MustFailCompile extends x10Test {

    public def run(): boolean = {

        val p[i,j]: Array[Point](1) = new Array[Point](2); // ERR ERR exploded elements are always assumed to be Int
        p(0) = [1,2];
        p(1) = [3,4];

        return (i(0) == 1 && j(1) == 4);  // ERR ERR
    }

    public static def main(args: Array[String](1)): void = {
        new ArrayPointBinding_MustFailCompile().execute();
    }
}

