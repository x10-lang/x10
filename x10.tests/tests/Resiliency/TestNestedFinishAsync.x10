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
 * Test nested finish at async (fail at third place in three places) handled correctly
 * 
 * @author Murata 8/2014
 */
public class TestNestedFinishAsync extends x10Test  {

    static val bad_counter = new Cell[Long](0);
    static val good_counter = new Cell[Long](5);

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
        if (Place.numPlaces() < 3) {
            Console.OUT.println("3 places are necessary for this test");
            return false;
        }
        val p0 = here;
        val p1 = Place.places().next(p0);
        val p2 = Place.places().next(p1);

        try {
	    
            finish {
                good_dec();
                at (p1) async {
                    good_dec();
                    finish {
                        at (p2) async {
                            good_dec();
                            System.killHere();
                            bad_inc();
                            Runtime.println("Should be dead by now");
                        }
                        Runtime.println("End of inner finish block (should not happen doe to exception)");
                        bad_inc();
                    }
                }
            }
	        
            Runtime.println("End of finish block (should not happen due to exception)");
            bad_inc();
	        
        } catch (e:MultipleExceptions) {
	    
            val dpes = e.getExceptionsOfType[DeadPlaceException]();
            assert dpes.size >= 1;
            for (dpe in dpes) {
                assert dpe.place == p2 : dpe.place;
            }

            good_dec();
        }
	    
        good_dec();
	    
        if (bad_counter() == 0 && good_counter() == 0) return true;
        else return false;
    }

    public static def main(Rail[String]) {
	    new TestNestedFinish().execute();
    }
}
