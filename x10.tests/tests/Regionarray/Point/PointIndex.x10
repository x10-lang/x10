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
 * Accessing p[3] in a 2D point should cause an array
 * index out of  bounds exception.
 *
 * @author kemal, 6/2005
 */
public class PointIndex extends x10Test {

    public def run(): boolean {

        var sum:long = 0;
        var gotException: boolean;
        var p: Point = [1, 2] as Point;

        gotException = false;
        try {
            sum += p(-1);
        } catch (var e: ArrayIndexOutOfBoundsException) {
            gotException = true;
        }
        x10.io.Console.OUT.println("1: sum = "+sum+" gotException = "+gotException);
        if (!(sum == 0 && gotException)) return false;

        gotException = false;
        try {
            sum += p(3);
        } catch (var e: ArrayIndexOutOfBoundsException) {
            gotException = true;
        }
        x10.io.Console.OUT.println("2: sum = "+sum+" gotException = "+gotException);
        return sum == 0 && gotException;
    }

    public static def main(var args: Rail[String]): void {
        new PointIndex().execute();
    }
}
