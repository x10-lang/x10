/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Remote accesses must be flagged by the compiler.
 *
 *
 * @author Kemal 4/2005
 */
public class AsyncTest3 extends x10Test {

	public def run() {
	    try {
		val A: Array[int](1) = Array.make[int](Dist.makeUnique());
		chk(Place.MAX_PLACES >= 2);
		chk(A.dist(0) == here);
		chk(A.dist(1) != here);
		val x= new X();

		finish async(here) { A(0) += 1; }
		A(0) += 1;
		finish async(here) { A(1) += 1; }
		A(1) += 1; //  remote communication
		x10.io.Console.OUT.println("1");
			
		finish async(here) { A(x.zero()) += 1; }
		A(x.zero()) += 1;

		finish async(here) { A(0) += A(x.one()); }
		A(0) += A(x.one());//  remote communication
		x10.io.Console.OUT.println("2");
		
		chk(A(0) == 8 && A(1) == 2);
		x10.io.Console.OUT.println("3");
		return false;
	    } catch (z:BadPlaceException) {
		return true;
	    }
	}

	public static def main(var args: Rail[String]) {
		new AsyncTest3().execute();
	}

	/**
	 * Dummy class to make static memory disambiguation difficult
	 * for a typical compiler
	 */
	static class X {
		public var z: Array[int](1) = [ 1, 0 ];
		def zero() = z(z(z(1))); 
		def one() = z(z(z(0))); 
		def modify() { z(0)++; }
	}
}
