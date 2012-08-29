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
import x10.util.concurrent.Future;

import x10.util.concurrent.Future;
/**
 * Tests exceptions and side effects
 * within activities spawned by a future expression.
 *
 * Checks that future { e }.force() acts as a global finish
 * on e, and throws e's  exceptions
 *
 * Events are choreographed using a sleep timer with
 * second granularity.
 *
 * @author kemal 4/2005
 */
public class FutureTest3 extends x10Test {

	public static N: int = 8;
	public static OUTOFRANGE: int = 99;

	/**
	 * Spawns subactivities that cause delayed side-effects.
	 */
	def m1(val A: Array[int](1), val K: int): int = {
		for (val [i]: Point in A) async {
			System.sleep(3000);
			atomic A(i) += 1;
		}
		var t: int;
		atomic t = A(K); //returns old value
		return t;
	}

	/**
	 * Spawns subactivities that cause delayed side-effects
	 * and exceptions.
	 */
	def m2(val A: Array[int](1), val K: int): int = {
		for (val p[i]: Point in A) async {
			System.sleep(3000);
			atomic A(i) += 1;
			atomic A(OUTOFRANGE) = -1;
		}
		var t: int;
		atomic t = A(K); //returns old value
		return t;
	}

	/**
	 * testing future with subactivities with
	 * side effects and exceptions.
	 */
	public def run(): boolean = {
		val A = new Array[int](0..(N-1), (Point)=>0);
		val K: int = 3;
		var gotException: boolean;

		// side effect in expression

		// (need atomic here if there is sharing. x10 should support atomic { expression } )
		var r1: int = Future.make( () => { return A(K) += 1;} ).force();
		x10.io.Console.OUT.println("1");
		atomic chk(A(K) == 1);
		chk(r1 == 1);

		// exception in expression
		var r2: int = -1;
		gotException = false;
		try {
			r2 = Future.make( () => { return A(OUTOFRANGE) += 1; } ) .force();
		} catch (var e: Exception) {
			gotException = true;
		}
		x10.io.Console.OUT.println("2");
		chk(r2 == -1 && gotException);

		//subactivities of e must be finished
		//when future e .force() returns
		var r3: int = -1;
		gotException = false;
		try {
			r3 = Future.make( () =>  m1(A, K)) .force();
		} catch (var e: Exception) {
			gotException = true;
		}
		x10.io.Console.OUT.println("3");
		chk(r3 == 1 && !gotException);
		// must read new values of A here
		for (val [i]: Point in A) x10.io.Console.OUT.println("A["+i+"] = "+A(i));
		chk(A(K) == 2);
		for (val [i]: Point in A) atomic chk(imp(i != K, A(i) == 1));

		//future { e }.force() must throw
		//exceptions from subactivities of e
		var r4: int = -1;
		gotException = false;
		try {
			r4 = Future.make( () => m2(A, K)) .force();
		} catch (var e: Exception) {
			gotException = true;
		}
		x10.io.Console.OUT.println("4" + gotException + " r4 = " + r4);
		chk(r4 ==-1 && gotException);
		// must read new values of A here
		for (val [i]: Point in A) x10.io.Console.OUT.println("A["+i+"] = "+A(i));
		atomic chk(A(K) == 3);
		for (val [i]: Point in A) atomic chk(imp(i != K, A(i) == 2));

		//Only force() throws the exception,
		//a plain future call just spawns the expression
		val fr5  = Future.make( () => m2(A, K) );
		x10.io.Console.OUT.println("5");
		// must read old values of A here
		//atomic chk(A(K) == 3);
		//for (val (i): Point in A) atomic chk(imp(i != K, A(i) == 2));
		var r5: int = -1;
		gotException = false;
		try {
			r5 = fr5.force();
		} catch (var e: Exception) {
			gotException = true;
		}
		chk(r5 ==-1 && gotException);
		// must read new values of A here
		for (val [i]: Point in A) x10.io.Console.OUT.println("A["+i+"] = "+A(i));
		atomic chk(A(K) == 4);
		for (val [i]: Point in A) atomic chk(imp(i != K, A(i) == 3));

		return true;
	}

	/**
	 * True iff x logically implies y
	 */
	static def imp(x: boolean, y: boolean): boolean = {
		return (!x||y);
	}

	public static def main(Array[String](1)) {
		new FutureTest3().execute();
	}
}
