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
import x10.array.Dist;
import x10.array.Array;

/**
 * Test the shorthand syntax for object array initializer.
 *
 * @author igor, 12/2005
 */

public class ObjectArrayInitializerShorthand extends x10Test {

    public def run(): boolean = {
        val d  = Dist.makeConstant([1..10, 1..10], here);
        val ia = Array.make[Dist](d, ((i,j): Point) => d);
        for (val (i,j): Point(2) in ia.region) chk(ia(i, j) == d);
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ObjectArrayInitializerShorthand().execute();
    }
}
