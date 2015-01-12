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
 * @author igor 7/2009
 */

public class XTENLANG_477 extends x10Test {

    public def run(): boolean {
        val x = "3.14159265359";
        val d = Double.parse(x);
        return d == 3.14159265359;
    }

    public static def main(Rail[String]) {
        new XTENLANG_477().execute();
    }
}

