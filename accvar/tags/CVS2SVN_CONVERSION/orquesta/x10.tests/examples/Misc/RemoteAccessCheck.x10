/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
import harness.x10Test;;

/**
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
public class RemoteAccessCheck extends x10Test {

	public def run(): boolean = {
		var d: dist = distmakeUnique(place.places);
		if (d.region.size() < 2) {
			System.out.println("RemoteAccessCheck requires 2 or more places.");
			return false;
		}
		final val a: A = future(d(0)) { new A() }.force();
		var error: int = future(d(1)) { checkField(a) }.force();
		if (error != 0)
			System.out.println(error);
		var error2: int = future(d(1)) { checkMethod(a) }.force();
		if (error2 != 0)
			System.out.println(error2);
			System.out.println("error=" + error + " error2=" + error2);
		return error == 0 && error2 == 0;
	}

	public static def main(var args: Rail[String]): void = {
		new RemoteAccessCheck().execute();
	}

	static def checkField(var a: A): int = {
		try {
			a.f.a = a;
			return 2;
		} catch (var bpe: BadPlaceException) {
		}
		return 0;
	}

	static def checkMethod(var a: A): int = {
		try {
			a.f.m();
			return 3;
		} catch (var bpe: BadPlaceException) {
		}

		return 0;
	}

    static value A extends Object {
	var f: F;
	var a: Array[int];
	def this(): A = {
	    f = new F();
	    a = new Array[int](Dist.makeConstant([0..4], here), 
			       (var i: point): int => { return 3; });
		}
	}

	static class F {
		var a: A;
		def m(): void = { }
	}
}
