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

// NUM_PLACES: 3
// RESILIENT_X10_ONLY

/**
 * Test middle fail (at second place in three places) handled correctly
 *
 * @author Murata 7/2014
 */
public class TestMiddleFail extends x10Test {

    static val bad_counter = new Cell[Long](0);
    static val good_counter = new Cell[Long](6);

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
        if (Place.numPlaces() != 3) return false;
        val p1 = Place.places().next(here);
        val p2 = Place.places().next(p1);

        try {

            finish {
                at (p1) async {
                    good_dec();
                    at (p2) async {
                        good_dec();
                        System.sleep(1000);
                        good_dec();
                    }
                    good_dec();
                    System.killHere();
                }
            }

            bad_inc();
            Runtime.println("End of finish loop (should not happen due to exception)");

        } catch (e:MultipleExceptions) {

            assert e.exceptions.size == 1l : e.exceptions;

            val e2 = e.exceptions(0);

            val e3 = e2 as DeadPlaceException;

            assert e3.place == p1 : e3.place;

            good_dec();

        }

        good_dec();

        if (bad_counter() == 0 && good_counter() == 0) return true;
        else return false;
    }

    public static def main(Rail[String]) {
        new TestMiddleFail().execute();
    }
}
