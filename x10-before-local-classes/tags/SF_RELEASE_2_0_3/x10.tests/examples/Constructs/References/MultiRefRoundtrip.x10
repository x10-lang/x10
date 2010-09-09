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

/**
 * Sending two remote references back to their home place should work.
 * @author igor 03/2010
 */
class MultiRefRoundtrip extends x10Test {

    public def run(): boolean {
        chk(Place.places.length > 1, "This test must be run with multiple places");
        val local = new MultiRefRoundtrip();
        val second = local;
        at (here.next()) {
            at (local) {
                Console.OUT.println(local == second);
            }
        }
        return at (here.next()) at (local) second == local;
    }

    public static def main(Rail[String]) {
        new MultiRefRoundtrip().execute();
    }
}
