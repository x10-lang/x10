/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

import harness.x10Test;
import x10.xrx.Runtime;

// NUM_PLACES: 3
// RESILIENT_X10_ONLY

/**
 * Test nested at middle fail sync (at second place in three places) 
 * handled correctly using purely ats (vs. finish at (p) async
 * as tested in TestNestedMiddleFail2.x10).
 * 
 * @author Murata 10/2014
 */
public class TestNestedMiddleFailSync2 extends x10Test  {

    static val bad_counter = new Cell[Long](0);
    static val good_counter = new Cell[Long](6);

    static def bad_inc() {
        at (Place.FIRST_PLACE) {
            atomic {
                bad_counter(bad_counter()+1);
                Console.OUT.println("bad counter = "+bad_counter());
            }
        }
    }

    static def good_dec() {
        at (Place.FIRST_PLACE) {
            atomic {
                good_counter(good_counter()-1);
                Console.OUT.println("good counter = "+good_counter());
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
            at (p1) {
                good_dec();
                at (p2) {
                    good_dec();
                    try {
                        finish at (p1) async System.killHere();
                    } catch (e:MultipleExceptions) {
                        val dpes = e.getExceptionsOfType[DeadPlaceException]();
                        assert dpes.size >= 1;
                        for (dpe in dpes) {
                            assert dpe.place == p1 : dpe.place;
                        }
                        good_dec();
                    }           
                    // Even though place 1 is dead, Place 0 should still be 
                    // synchronously waiting for the activity in Place 2 to
                    // terminate before it sees the DPE for Place 1.
                    // So, we stall for a few seconds to make sure.
                    Runtime.println(p2+" about to stall...");
                    System.sleep(3000);
                    good_dec();
                    Runtime.println("good bye from "+p2);
               }
               Runtime.println("Executing in Place 1 after it is dead");
               bad_inc();
            }
	        
            Runtime.println("Executing non exceptional control flow in Place 0");
            bad_inc();
	        
        } catch (e:DeadPlaceException) {
            good_dec();
        }
	    
        Runtime.println(p0+" is continuing; this should be _after_ the 'good bye' from "+p2);

        good_dec();
	    
        if (bad_counter() == 0 && good_counter() == 0) return true;
        else return false;
    }

    public static def main(Rail[String]) {
	    new TestNestedMiddleFailSync2().execute();
    }
}
