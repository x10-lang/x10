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

//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.

import harness.x10Test;
import x10.regionarray.*;

public class PlaceCheck1 extends x10Test {

	public def run(): boolean = {
		var ret: boolean;
		x10.io.Console.OUT.println("num places = " + Place.numPlaces());
		// this test only works with > 1 place
		if (Place.numPlaces() <= 1) {
			x10.io.Console.OUT.println("This test requires at least 2 places.");
			ret = false;
		} else {
			ret = true;
			// In the 2.1 model, objects are copied. Hence checkFieldAccess, checkFieldAssign and checkMethodCall
			// will always "fail", i.e. they will operate on the local copy of the object.
			// These are not valid tests anymore.
			//ret = checkFieldAccess();
			//ret = checkFieldAssign() & ret;
			//ret = checkMethodCall() & ret;
			ret = checkArrayAccess() & ret;
			ret = checkArrayAssign() & ret;
		}
		return ret;
	}

	public static def getNotHere() = Place.places().next(here);
	public var foo: int;
	public def foo_method() = 42;

	public static def checkFieldAccess() {
		var ret: boolean = false;
		try {
			val obj  = new PlaceCheck1();
			obj.foo = 123n;
			// x10.io.Console.OUT.println("DEBUG - creating object in place p = " + here);
			val other_place = getNotHere();
			finish async at (other_place) {
				val o = obj as PlaceCheck1;
				val xxx = o.foo;
				if (xxx != 123n)
					x10.io.Console.OUT.println(xxx);
			};
			x10.io.Console.OUT.println("WARN - expected exception/error for remote field read.");
		} catch (e: BadPlaceException) {
			x10.io.Console.OUT.println("OK - got BadPlaceException in field access");
			ret = true;
		}  catch (e:ClassCastException) {
			x10.io.Console.OUT.println("OK - got ClassCastException in field access");
			ret = true;
		}
		return ret;
	}

	public static def checkFieldAssign() {
		var ret: boolean = false;
		try {
			val obj_here  = new PlaceCheck1();
			val other_place  = getNotHere();
			finish async at (other_place) {
				atomic { 
					val o = obj_here as PlaceCheck1;
					o.foo = 123n; 
					}
			};
			x10.io.Console.OUT.println("WARN - expected exception/error for remote field write in atomic");
		} catch (e: BadPlaceException) {
			x10.io.Console.OUT.println("OK - got BadPlaceException for in field assign");
			ret = true;
		} catch (e:ClassCastException) {
			x10.io.Console.OUT.println("OK - got ClassCastException in field access");
			ret = true;
		}
		
		return ret;
	}

	public static def checkMethodCall(): boolean = {
		var ret: boolean = false;
		try {
			val obj  = new PlaceCheck1();
			val other_place = getNotHere();
			finish async at (other_place) {
				atomic { 	
					val o = obj as PlaceCheck1;
				o.foo_method(); }
			};
			x10.io.Console.OUT.println("WARN - expected exception/error for remote method call in atomic");
		} catch (e: BadPlaceException) {
			x10.io.Console.OUT.println("OK - got BadPlaceException for method call");
			ret = true;
		} catch (e:ClassCastException) {
			x10.io.Console.OUT.println("OK - got ClassCastException in field access");
			ret = true;
		}
		return ret;
	}

	public static def checkArrayAccess(): boolean = {
		var ret: boolean = false;
		val d = Dist.makeUnique();
		val arr  =  DistArray.make[int](d, (Point) => 123n);
		try {
			var other_place: Place = getNotHere();
			atomic { arr(other_place.id) = 123n; }
			x10.io.Console.OUT.println("WARN - expected exception/error for remote array element write in atomic");
		} catch (var e: BadPlaceException) {
			x10.io.Console.OUT.println("OK - got BadPlaceException in array access");
			ret = true;
		}  catch (e:ClassCastException) {
			x10.io.Console.OUT.println("OK - got ClassCastException in field access");
			ret = true;
		}
		return ret;
	}

	public static def checkArrayAssign(): boolean = {
		var ret: boolean = false;
		val d  = Dist.makeUnique();
		val arr = DistArray.make[int](d, ([p]: Point): int => 123n);
		try {
			val other_place: Place = getNotHere();
			var xxx: int;
			atomic { xxx = arr(other_place.id);}
			if (xxx != 123n)
				x10.io.Console.OUT.println(xxx);
			x10.io.Console.OUT.println("WARN - expected exception/error for remote array element read in atomic");
		} catch (var e: BadPlaceException) {
			x10.io.Console.OUT.println("OK - got BadPlaceException in array access");
			ret = true;
		}  catch (e:ClassCastException) {
			x10.io.Console.OUT.println("OK - got ClassCastException in field access");
			ret = true;
		}
		return ret;
	}

	public static def main(Rail[String]): void = {
		new PlaceCheck1().execute();
	}
}
