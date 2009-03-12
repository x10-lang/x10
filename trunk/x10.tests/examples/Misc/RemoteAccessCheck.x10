/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
import harness.x10Test;

/**
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
public class RemoteAccessCheck extends x10Test {

	public def run()  {
		val d = Dist.makeUnique(Place.places);
		if (d.region.size() < 2) {
			x10.io.Console.OUT.println("RemoteAccessCheck requires 2 or more places.");
			return false;
		}
		val a = (future(d(0)) new A()).force();
		val error = (future(d(1)) checkField(a) ).force();
		if (error != 0) x10.io.Console.OUT.println(error);
		val error2 = (future(d(1)) checkMethod(a)).force();
		if (error2 != 0)
			x10.io.Console.OUT.println(error2);
			x10.io.Console.OUT.println("error=" + error + " error2=" + error2);
		return error == 0 && error2 == 0;
	}

	public static def main(var args: Rail[String]) {
		new RemoteAccessCheck().execute();
	}

	static def checkField(a: A){
		try {
			a.f.a = a;
			return 2;
		} catch (BadPlaceException) {
		}
		return 0;
	}

	static def checkMethod(a: A) {
		try {
			a.f.m();
			return 3;
		} catch (BadPlaceException) {
		}

		return 0;
	}

    static value A extends Value {
	val f: F;
	val a: Array[int];
	def this(): A = {
	    f = new F();
	    a = Array.make[int](Dist.makeConstant(0..4, here), (Point)=>3);
		}
	}

	static class F {
		var a: A;
		def m() { }
	}
}
