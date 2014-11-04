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

// NUM_PLACES: 2
// RESILIENT_X10_ONLY

/**
 * Test exceptions in at and in at-async are delivered correctly
 * 
 * @author Murata 11/2014
 */
public class RunAtExceptionTest extends x10Test  {

    static val bad_counter = new Cell[Long](0);
    static val good_counter = new Cell[Long](4);

    static def bad_inc() {
        at (Place.FIRST_PLACE) {
            atomic {
                bad_counter(bad_counter()+1);
                Console.OUT.println("bad_counter() = " + bad_counter());
            }
        }
    }

    static def good_dec() {
        at (Place.FIRST_PLACE) {
            atomic {
                good_counter(good_counter()-1);
                Console.OUT.println("good_counter() = " + good_counter());
            }
        }
    }

    public def run() {
        if (Place.numPlaces() < 2) {
            Console.OUT.println("2 places are necessary for this test");
            return false;
        }
        val p0 = here;
        val p1 = Place.places().next(p0);

        try {
            finish {
                try {
                    at (Place(1)) {
                        throw new Exception("test1"); // should be delivered to "at"
                    }
                } catch (e:Exception) {
                    Console.OUT.println("TEST1 SUCCESS: should come here");
                    good_dec();
                }
            } // end of finish
        } catch (e:Exception) {
            Console.OUT.println("TEST1 FAILED: should not come here");
            bad_inc();
        }

        good_dec();
        
        try {
            finish {
                try {
                    at (Place(1)) {
                        async { throw new Exception("test2"); } // should be delivered to "finish" rather than "at"
                    }
                } catch (e:Exception) {
                    Console.OUT.println("TEST2 FAILED: should not come here");
                    bad_inc();
                }
            } // end of finish
        } catch (e:Exception) {
            Console.OUT.println("TEST2 SUCCESS: should come here");
            good_dec();
        }

        good_dec();
        
        if (bad_counter() == 0 && good_counter() == 0) return true;
        else return false;
    }

    public static def main(Rail[String]) {
	    new RunAtExceptionTest().execute();
    }
}
