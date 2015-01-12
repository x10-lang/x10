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
// RESILIENT_X10_ONLY

/**
 * Test remote fail handled correctly
 *
 * @author Murata 1/2014
 */
public class TestRemoteFail extends x10Test {

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
        val p1 = Place.places().next(here);

        try {

            finish {
                good_dec();
                at (p1) async {
                    good_dec();
                    System.sleep(1000);
                    System.killHere();
                }
            }

            Runtime.println("End of finish loop (should not happen due to exception)");

        } catch (e:MultipleExceptions) {

            val dpes = e.getExceptionsOfType[DeadPlaceException]();
            assert dpes.size >= 1;
            for (dpe in dpes) {
                assert dpe.place == p1 : dpe.place;
            }

            good_dec();

        }

        good_dec();

        if (bad_counter() == 0 && good_counter() == 0) return true;
        else return false;
    }

    public static def main(Rail[String]) {
        new TestRemoteFail().execute();
    }
}
