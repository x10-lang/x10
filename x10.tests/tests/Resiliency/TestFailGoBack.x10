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
import x10.xrx.Runtime;

// NUM_PLACES: 2
// RESILIENT_X10_ONLY

/**
 * Test fail go back (kill place1 then go back to place1) handled correctly
 * 
 * @author Murata 7/2014
 */
public class TestFailGoBack extends x10Test  {

    static val bad_counter = new Cell[Long](0);
    static val good_counter = new Cell[Long](4);

    static def bad_inc() {
        at (Place.FIRST_PLACE) {
            atomic {
                bad_counter(bad_counter()+1);
            }
        }
    }

    static def good_dec() {
        at (Place.FIRST_PLACE) {
            atomic {
                good_counter(good_counter()-1);
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
                at (p1) {
                    System.sleep(1000);
                    System.killHere();
                }
            }

            Runtime.println("End of finish loop (should not happen due to exception)");
            bad_inc();

        } catch (e:MultipleExceptions) {

            val dpes = e.getExceptionsOfType[DeadPlaceException]();
            assert dpes.size >= 1;
            for (dpe in dpes) {
                assert dpe.place == p1 : dpe.place;
            }

            good_dec();

        }

        good_dec();

        try {

            finish {
                at (p1) {
                    Runtime.println("Place 1 should be dead");
                    bad_inc();
                }
            }

            Runtime.println("End of finish loop (should not happen due to exception)");
            bad_inc();

        } catch (e:MultipleExceptions) {

            val dpes = e.getExceptionsOfType[DeadPlaceException]();
            assert dpes.size >= 1;
            for (dpe in dpes) {
                assert dpe.place == p1 : dpe.place;
            }

            good_dec();

        }

        good_dec();
	    
        if (bad_counter() == 0l && good_counter() == 0l) return true;
        else return false;
    }

    public static def main(Rail[String]) {
	    new TestFailGoBack().execute();
    }
}
