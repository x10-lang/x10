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
 * Test nested at-async with double failes
 * (at the second and third places in three places)
 * handled correctly
 *
 * @author Murata 11/2014
 */
public class TestNestedAtAsyncDoubleFails extends x10Test  {

    static val bad_counter = new Cell[Long](0);
    static val good_counter = new Cell[Long](8);

    static def bad_inc() {
        at (Place.FIRST_PLACE) {
            atomic {
                bad_counter(bad_counter()+1);
                Console.OUT.println("bad_counter = " + bad_counter());
            }
        }
    }

    static def good_dec() {
        at (Place.FIRST_PLACE) {
            atomic {
                good_counter(good_counter()-1);
                Console.OUT.println("good_counter = " + good_counter());
            }
        }
    }

    public def run() {
	    if (Place.numPlaces() < 3) {
		    Console.OUT.println("3 places are necessary for this test");
		    return false;
	    }
	    val place0 = here;
	    val place1 = Place.places().next(place0);
	    val place2 = Place.places().next(place1);
	    var ret:Boolean = true;
	    try {
	        val flag = new Rail[Boolean](2);
	        flag(0) = false; flag(1) = false;
	        val flagGR = GlobalRef(flag);
	        good_dec();
	        finish {
		        at (place1) async {
		            good_dec();
		    		at (place2) async {
		                good_dec();
		                at (flagGR) async { atomic { flagGR()(1) = true; } }
		                System.sleep(1000);
		                bad_inc();
		    		}
		            good_dec();
		            at (flagGR) async { atomic { flagGR()(0) = true; } }
		            System.sleep(1000);
		            bad_inc();
		        }
		        good_dec();
		        when(flag(0) && flag(1)) {
		            System.killThere(place1);
		            System.killThere(place2);
		        }
		        good_dec();
	        }
		} catch (e:Exception) {
		    ret = processException(e);
		    good_dec();
	    }
	    good_dec();
	    if (bad_counter() == 0 && good_counter() == 0 && ret) {
	        Console.OUT.println("Test finished"); Console.OUT.flush();
	        return true;
	    } else {
	        Console.OUT.println("Test failed"); Console.OUT.flush();
	        return false;
	    }
    }

    private static def processException(e:Exception):Boolean {
	    if (e instanceof DeadPlaceException) {
		    val deadPlace = (e as DeadPlaceException).place;
		    Console.OUT.println("DeadPlaceException from " + deadPlace);
	    } else if (e instanceof MultipleExceptions) {
            val exceptions = (e as MultipleExceptions).exceptions();
 		    Console.OUT.println("MultipleExceptions size=" + exceptions.size);
            val deadPlaceExceptions = (e as MultipleExceptions).getExceptionsOfType[DeadPlaceException]();
            for (dpe in deadPlaceExceptions) {
                processException(dpe);
            }
            val filtered = (e as MultipleExceptions).filterExceptionsOfType[DeadPlaceException]();
            if (filtered != null) {
                Console.OUT.println("Unexpected exception!!!!");
                return false;
            }
	    }
	    return true;
    }

    public static def main(Rail[String]) {
	    new TestNestedAtAsyncDoubleFails().execute();
    }
}
