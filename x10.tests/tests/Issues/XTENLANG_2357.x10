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

//OPTIONS: -STATIC_CHECKS

import x10.compiler.tests.*; // err markers
import harness.x10Test;

/**
 * See XTENLANG_2357 -- The 'as'es defined here ought to compile, but didn't (for me)
 * @author bardb 1/2011
 */
public class XTENLANG_2357(x:Int) extends x10Test {

    public def run() = true;

    public static def main(Rail[String]) {
        new XTENLANG_2357(5n).execute();
    }

    static class Bee2357 {
        public static operator (x:Int) as Bee2357 = new Bee2357();
        def example() {
            val b:Bee2357 = 2n as Bee2357;
        }
    }
}
