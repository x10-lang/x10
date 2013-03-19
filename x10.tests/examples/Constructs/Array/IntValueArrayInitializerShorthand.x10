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
import x10.array.*;

/**
 * Test the shorthand syntax for a value array initializer.
 */

public class IntValueArrayInitializerShorthand extends x10Test {

    public def run(): boolean = {

        val ia = new Array[int]((1..10)*(1..10), ([i,j]:Point) => i+j);

        for (p[i,j]:Point(2) in (1..10)*(1..10)) chk(ia(p) == i+j);

        return true;
    }

    public static def main(Rail[String])  {
        new IntValueArrayInitializerShorthand().execute();
    }
}
