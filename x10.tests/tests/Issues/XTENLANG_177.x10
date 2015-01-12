/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_177 extends x10Test {

    val x = 1.0;
    val y = "1";
    val s = String.format("%f %s", [x, y]);

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_177().execute();
    }
}
