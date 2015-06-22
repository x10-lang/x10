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

/**
 * Atomic return test
 */
public class AtomicReturn extends x10Test {
    var a: long = 0;
    static N: long = 100;

    def update1(): long {
        atomic {
            a++;
            return a;
        }
    }

    def update3(): long {
        atomic {
            return a++;
        }
    }

    public def run(): boolean {
        update1();
        update3();
        //x10.io.Console.OUT.println(a);
        return a == 2;
    }

    public static def main(Rail[String]) {
        new AtomicReturn().execute();
    }
}
