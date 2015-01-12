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

// NUM_PLACES: 3
// RESILIENT_X10_ONLY

/**
 * Test dead place blocks its child's exception
 *
 * @author Murata 10/2014
 */
public class TestDeadPlaceBlockChildException extends x10Test  {
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
		    finish at (place1) async {
		    	try {
		    		finish at (place2) async {
		    			throw new Exception("Test for blocked");
		    		}
		    	} catch (e:Exception) {
		    		Console.OUT.println("Exception from " + place2);
		    	}
			    async {
				    Console.OUT.println("Killing " + here); Console.OUT.flush();
				    System.killHere();
			    }
		    }
	    } catch (e:Exception) {
		   ret = processException(e);
	    }
	    Console.OUT.println("Test finished"); Console.OUT.flush();
	    return ret;
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
	    new TestDeadPlaceBlockChildException().execute();
    }
}
