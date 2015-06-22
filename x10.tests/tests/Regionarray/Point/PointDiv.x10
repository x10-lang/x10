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
 * Testing point div.
 *
 * @author vj 08/29/08
 */

public class PointDiv extends x10Test {

    public def run(): boolean {

        val p = [2 as long, 2, 2, 2, 2] as Point;
        val q = [1 as long, 1, 1, 1, 1] as Point;
    
        val a = p / q;
        return p.equals(a*q);
    }

    public static def main(var args: Rail[String]): void {
        new PointDiv().execute();
    }
}
