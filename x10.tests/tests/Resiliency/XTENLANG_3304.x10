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

import x10.util.Timer;

// NUM_PLACES: 2

/**
 * "Main 30" will not be displayed until sleep is woken up
 *
 * @author Murata 1/2014
 */
public class XTENLANG_3304 extends x10Test {

    public def run() {
        if (Place.numPlaces() < 2) {
            Console.OUT.println("2 places are necessary for this test");
            return false;
        }
        val place1 = Place.places().next(here);
        val t = new Timer();
        val wokenupTime = GlobalCell.make[Long](0);
        async {
            Console.OUT.println("Sleep 5 sec"); Console.OUT.flush();
            System.sleep(5000);
            wokenupTime(t.nanoTime());
            Console.OUT.println("Woken up"); Console.OUT.flush();
        }

        Console.OUT.println("Main 10"); Console.OUT.flush();
        at (place1) {
            Console.OUT.println("Main 20"); Console.OUT.flush();
        }
        Console.OUT.println("Main 30"); Console.OUT.flush();
        if (wokenupTime() == 0) return true;
        else return false;
    }

    public static def main(Rail[String]) {
        new XTENLANG_3304().execute();
    }
}
