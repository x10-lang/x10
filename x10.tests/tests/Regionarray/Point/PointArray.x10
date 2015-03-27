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
import x10.regionarray.*;

/**
 * Creating an array of points and assigning to its elements should work.
 *
 * @author igor, 1/2006
 */

public class PointArray extends x10Test {

    public def run(): boolean {

        var p: Rail[Point] = new Rail[Point](1, (long)=>Point.make(0));
        p(0) = Point.make(1, 2);

        return (p(0)(0) == 1L && p(0)(1) == 2L);
    }

    public static def main(var args: Rail[String]): void {
        new PointArray().execute();
    }
}
