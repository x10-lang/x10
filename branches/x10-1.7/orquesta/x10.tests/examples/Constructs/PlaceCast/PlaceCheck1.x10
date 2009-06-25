****Error: examples/Constructs/PlaceCast/PlaceCheck1.x10:34:18:34:22:0:-1:9: ";" inserted to complete scope
/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.

import java.util.Iterator;
import harness.x10Test;;

public class PlaceCheck1 extends x10Test {

	public def run(): boolean = {
		var ret: boolean;
		System.out.println("num places = " + place.places);
		// this test only works with > 1 place
		if (place.places.size() <= 1) {
			System.out.println("This test requires at least 2 places.");
			ret = false;
		} else {
			ret = checkFieldAccess();
			ret = checkFieldAssign() & ret;
			ret = checkMethodCall() & ret;
			ret = checkArrayAccess() & ret;
			ret = checkArrayAssign() & ret;
		}
		return ret;
	}

	public static def getNotHere(): place = {
		var place: Box[ret] = null; // here.next(); -- does not work
		for (var it: Iterator[place] = place.places.iterator(); it.hasNext(); ) {
			var p: place = it.next();
			if (p != here) {
				ret = p;
				break;
			}
		}
		return ret to place;
	}

	public var foo: int;
	public def foo_method(): int = {
		return 42;
	}

	public static def checkFieldAccess(): boolean = {
		var ret: boolean = false;
		try {
			val obj_here: PlaceCheck1 = new PlaceCheck1();
			obj_here.foo = 123;
			// System.out.println("DEBUG - creating object in place p = " + here);
			var other_place: place = getNotHere();
			finish async (other_place) {
				var xxx: int;
				atomic { xxx = obj_here.foo; }
				if (xxx != 123)
					System.out.println(xxx);
			};
			System.out.println("WARN - expected exception/error for remote field read in atomic");
		} catch (e: BadPlaceException) {
			System.out.println("OK - got BadPlaceException in field access");
			ret = true;
		}
		return ret;
	}

	public static def checkFieldAssign(): boolean = {
		var ret: boolean = false;
		try {
			val obj_here: PlaceCheck1 = new PlaceCheck1();
			var other_place: place = getNotHere();
			finish async (other_place) {
				atomic { obj_here.foo = 123; }
			};
			System.out.println("WARN - expected exception/error for remote field write in atomic");
		} catch (e: BadPlaceException) {
			System.out.println("OK - got BadPlaceException for in field assign");
			ret = true;
		}
		return ret;
	}

	public static def checkMethodCall(): boolean = {
		var ret: boolean = false;
		try {
			val obj_here: PlaceCheck1 = new PlaceCheck1();
			var other_place: place = getNotHere();
			finish async (other_place) {
				atomic { obj_here.foo_method(); }
			};
			System.out.println("WARN - expected exception/error for remote method call in atomic");
		} catch (e: BadPlaceException) {
			System.out.println("OK - got BadPlaceException for method call");
			ret = true;
		}
		return ret;
	}

	public static def checkArrayAccess(): boolean = {
		var ret: boolean = false;
		val d: dist = dist.factory.unique(place.places);
		val arr: Array[int] = new Array[int](d, ((p): point): int => 123);
		try {
			var other_place: place = getNotHere();
			atomic { arr(other_place.id) = 123; }
			System.out.println("WARN - expected exception/error for remote array element write in atomic");
		} catch (var e: BadPlaceException) {
			System.out.println("OK - got BadPlaceException in array access");
			ret = true;
		}
		return ret;
	}

	public static def checkArrayAssign(): boolean = {
		var ret: boolean = false;
		val d: dist = dist.factory.unique(place.places);
		val arr: Array[int] = new Array[int](d, ((p): point): int => 123);
		try {
			var other_place: place = getNotHere();
			var xxx: int;
			atomic { xxx = arr(other_place.id);}
			if (xxx != 123)
				System.out.println(xxx);
			System.out.println("WARN - expected exception/error for remote array element read in atomic");
		} catch (var e: BadPlaceException) {
			System.out.println("OK - got BadPlaceException in array access");
			ret = true;
		}
		return ret;
	}

	public static def main(var args: Rail[String]): void = {
		new PlaceCheck1().execute();
	}
}
