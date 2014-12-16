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

public class ArrayPlusEqual extends x10Test {

    val v = new Rail[int](2, 0n);

    public def run() {
        for (i in 0..1) v(i) += 5n;
        for (i in 0..1) chk(v(i) == 5n);
        return true;
    }

    public static def main(Rail[String]) {
        new ArrayPlusEqual().execute();
    }
}
