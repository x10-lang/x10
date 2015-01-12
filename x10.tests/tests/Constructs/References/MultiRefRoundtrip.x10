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
 * Sending two remote references back to their home place should work.
 * @author igor 03/2010
 */
class MultiRefRoundtrip extends x10Test {

    public def run(): boolean {
        chk(Place.numPlaces() > 1, "This test must be run with multiple places");
	val obj = new Empty();
        val local_ = GlobalRef[Empty](obj);
        val second = GlobalRef[Empty](obj);
        at (Place.places().next(here)) {
            at (local_) {
                Console.OUT.println(local_ == second);
            }
        }
        return at (Place.places().next(here)) at (local_) second == local_;
    }

    public static def main(Rail[String]) {
        new MultiRefRoundtrip().execute();
    }
}
