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
 * Resilient X10 (r26983) sometimes throws
 * ClassCastException for value-returning
 * at(p) when the place dies
 * Fixed by r26985 
 * 
 * @author Murata 3/2014
 */
public class XTENLANG_3324  extends x10Test  {
	public def run() {
		if (Place.numPlaces() < 3) {
			Console.OUT.println("3 places are necessary for this test");
			return false;
		}
		val place0 = here;
	        val place1 = Place.places().next(place0);
	        val place2 = Place.places().next(place1);
		var ret:Boolean = true;
		val timesOfTests = 20;
		
		for (j in 1..timesOfTests) {
		    async try { at (place2) { // set time bomb
			    System.sleep(5000);
			    Console.OUT.println("Killing " + here); Console.OUT.flush();
			    System.killHere();
		    } } catch (e:Exception) { /* ignore */ }

		    try {
			    at (place1) {
				    var x:Long = 0;
				    for (i in 1..100000) x += at (place2) here.id;
			    }
		    } catch (e:Exception) {
			    ret = processException(e);
		    }
		    if (!ret) return ret;
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
		new XTENLANG_3324().execute();
	}
}
