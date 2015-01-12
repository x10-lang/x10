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
 * Simple unsigned test.
 */
public class Unsigned5 extends x10Test {

    public def run(): boolean = {
        val b = 0xffffffffu < 0u;
        return ! b;
    }

    public static def main(Rail[String]) = {
        new Unsigned5().execute();
    }
}
