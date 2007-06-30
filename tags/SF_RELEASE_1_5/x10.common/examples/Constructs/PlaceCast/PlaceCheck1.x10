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
import harness.x10Test;

public class PlaceCheck1 extends x10Test {

	public boolean run() {
		boolean ret;
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

	public static place getNotHere() {
		nullable place ret = null; // here.next(); -- does not work
		for (Iterator it = place.places.iterator(); it.hasNext(); ) {
			place p = (place) it.next();
			if (p != here) {
				ret = p;
				break;
			}
		}
		return (place) ret;
	}

	public int foo;
	public int foo_method() {
		return 42;
	}

	public static boolean checkFieldAccess() {
		boolean ret = false;
		try {
			final PlaceCheck1 obj_here = new PlaceCheck1();
			obj_here.foo = 123;
			// System.out.println("DEBUG - creating object in place p = " + here);
			place other_place = getNotHere();
			finish async (other_place) {
				int xxx;
				atomic { xxx = obj_here.foo; }
				if (xxx != 123)
					System.out.println(xxx);
			};
			System.out.println("WARN - expected exception/error for remote field read in atomic");
		} catch (BadPlaceException e) {
			System.out.println("OK - got BadPlaceException in field access");
			ret = true;
		}
		return ret;
	}

	public static boolean checkFieldAssign() {
		boolean ret = false;
		try {
			final PlaceCheck1 obj_here = new PlaceCheck1();
			place other_place = getNotHere();
			finish async (other_place) {
				atomic { obj_here.foo = 123; }
			};
			System.out.println("WARN - expected exception/error for remote field write in atomic");
		} catch (BadPlaceException e) {
			System.out.println("OK - got BadPlaceException for in field assign");
			ret = true;
		}
		return ret;
	}

	public static boolean checkMethodCall() {
		boolean ret = false;
		try {
			final PlaceCheck1 obj_here = new PlaceCheck1();
			place other_place = getNotHere();
			finish async (other_place) {
				atomic { obj_here.foo_method(); }
			};
			System.out.println("WARN - expected exception/error for remote method call in atomic");
		} catch (BadPlaceException e) {
			System.out.println("OK - got BadPlaceException for method call");
			ret = true;
		}
		return ret;
	}

	public static boolean checkArrayAccess() {
		boolean ret = false;
		final dist d = dist.factory.unique(place.places);
		final int[d] arr = new int[d] (point [p]) { return 123; };
		try {
			place other_place = getNotHere();
			atomic { arr[other_place.id] = 123; }
			System.out.println("WARN - expected exception/error for remote array element write in atomic");
		} catch (BadPlaceException e) {
			System.out.println("OK - got BadPlaceException in array access");
			ret = true;
		}
		return ret;
	}

	public static boolean checkArrayAssign() {
		boolean ret = false;
		final dist d = dist.factory.unique(place.places);
		final int[d] arr = new int[d] (point [p]) { return 123; };
		try {
			place other_place = getNotHere();
			int xxx;
			atomic { xxx = arr[other_place.id];}
			if (xxx != 123)
				System.out.println(xxx);
			System.out.println("WARN - expected exception/error for remote array element read in atomic");
		} catch (BadPlaceException e) {
			System.out.println("OK - got BadPlaceException in array access");
			ret = true;
		}
		return ret;
	}

	public static void main(String[] args) {
		new PlaceCheck1().execute();
	}
}

