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

// NUM_PLACES: 2

/**
 * Hang up in Resilient X10 even without Place failure
 *
 * @author Murata 1/2014
 */
public class XTENLANG_3305 extends x10Test {

    public def run() {
        if (Place.numPlaces() < 2) {
            Console.OUT.println("2 places are necessary for this test");
            return false;
        }
        val place0 = here, place1 = Place.places().next(here);
        async {
            Console.OUT.println("Sleep 5 sec"); Console.OUT.flush();
            System.sleep(5000);
            Console.OUT.println("Woken up"); Console.OUT.flush();
        }

        Console.OUT.println("Main 10"); Console.OUT.flush();
        at (place1) {
            Console.OUT.println("Main 20"); Console.OUT.flush();
            val x = at (place0) {
                Console.OUT.println("Main 30"); Console.OUT.flush();
                1234 /* return value of at */
            };
            Console.OUT.println("Main 40, x=" + x); Console.OUT.flush();
        }
        Console.OUT.println("Main 50"); Console.OUT.flush();
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_3305().execute();
    }
}
