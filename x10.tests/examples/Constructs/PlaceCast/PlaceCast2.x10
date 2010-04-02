/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;
import x10.array.Dist;
import x10.array.Array;

// vj Nov 9, 2009. This will throw a compiler error until we fix
// the XTerm translator so it can accept d(p) as a term, where d is
// a distribution and p a place.

public class PlaceCast2 extends x10Test {
	var nplaces: int = 0;
	public def run(): boolean = {
			val d = Dist.makeUnique();
		x10.io.Console.OUT.println("num places = " + Place.MAX_PLACES);
		val disagree = Array.make[BoxedBoolean](d, ((p): Point): BoxedBoolean => {
				x10.io.Console.OUT.println("The currentplace is:" + here);
				return new BoxedBoolean();
			});
		finish ateach (p  in d) {
			// remember if here and d[p] disagree
			// at any activity at any place
			try {
				// FIX: d(p) currently causes the compiler to assert an error.
				val x  = disagree(p) as BoxedBoolean!d(p);
				at (this) atomic  nplaces++; 
			} catch (x: BadPlaceException)  {
				x10.io.Console.OUT.println("Caught bad place exception for " + p);
			 } catch (x: ClassCastException) {
		    	Console.OUT.println("Caught class cast exception for " + p);
		      }
		}
		x10.io.Console.OUT.println("nplaces == " + nplaces);
		return nplaces == Place.places.size();
	}

	public static def main(Rail[String]) {
		new PlaceCast2().execute();
	}

	static class BoxedBoolean {
		var v: boolean = false;
	}
}
