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

	public static N: int = 8n;
	public static OUTOFRANGE: int = 99n;

	/**
	 * Spawns subactivities that cause delayed side-effects.
	 */
	def m1(val A: Rail[int], val K: long): int {
		for (i in A.range) async {
			System.sleep(3000);
			atomic A(i) += 1n;
		}
		var t: int;
		atomic t = A(K); //returns old value
		return t;
	}

	/**
	 * Spawns subactivities that cause delayed side-effects
	 * and exceptions.
	 */
	def m2(val A: Rail[int], val K: long): int {
		for (i in A.range) async {
			System.sleep(3000);
			atomic A(i) += 1n;
			atomic A(OUTOFRANGE) = -1n;
		}
		var t: int;
		atomic t = A(K); //returns old value
		return t;
	}

	/**
	 * testing future with subactivities with
	 * side effects and exceptions.
	 */
	public def run(): boolean {
		val A = new Rail[int](N);
		val K:Long = 3;
		var gotException: boolean;

		// side effect in expression

		// (need atomic here if there is sharing. x10 should support atomic { expression } )
		var r1: int = Future.make( () => { return A(K) += 1n;} ).force();
		x10.io.Console.OUT.println("1");
		atomic chk(A(K) == 1n);
		chk(r1 == 1n);

		// exception in expression
		var r2: int = -1n;
		gotException = false;
		try {
			r2 = Future.make( () => { return A(OUTOFRANGE) += 1n; } ) .force();
		} catch (var e: Exception) {
			gotException = true;
		}
		x10.io.Console.OUT.println("2");
		chk(r2 == -1n && gotException);

		//subactivities of e must be finished
		//when future e .force() returns
		var r3: int = -1n;
		gotException = false;
		try {
			r3 = Future.make( () =>  m1(A, K)) .force();
		} catch (var e: Exception) {
			gotException = true;
		}
		x10.io.Console.OUT.println("3");
		chk(r3 == 1n && !gotException);
		// must read new values of A here
		for (i in A.range) x10.io.Console.OUT.println("A["+i+"] = "+A(i));
		chk(A(K) == 2n);
		for (i in A.range) atomic chk(imp(i != K, A(i) == 1n));

		//future { e }.force() must throw
		//exceptions from subactivities of e
		var r4: int = -1n;
		gotException = false;
		try {
			r4 = Future.make( () => m2(A, K)) .force();
		} catch (var e: Exception) {
			gotException = true;
		}
		x10.io.Console.OUT.println("4" + gotException + " r4 = " + r4);
		chk(r4 ==-1n && gotException);
		// must read new values of A here
		for (i in A.range) x10.io.Console.OUT.println("A["+i+"] = "+A(i));
		atomic chk(A(K) == 3n);
		for (i in A.range) atomic chk(imp(i != K, A(i) == 2n));

		//Only force() throws the exception,
		//a plain future call just spawns the expression
		val fr5  = Future.make( () => m2(A, K) );
		x10.io.Console.OUT.println("5");
		// must read old values of A here
		//atomic chk(A(K) == 3);
		//for (i in A.range) atomic chk(imp(i != K, A(i) == 2));
		var r5: int = -1n;
		gotException = false;
		try {
			r5 = fr5.force();
		} catch (var e: Exception) {
			gotException = true;
		}
		chk(r5 ==-1n && gotException);
		// must read new values of A here
		for (i in A.range) x10.io.Console.OUT.println("A["+i+"] = "+A(i));
		atomic chk(A(K) == 4n);
		for (i in A.range) atomic chk(imp(i != K, A(i) == 3n));

		return true;
	}

	/**
	 * True iff x logically implies y
	 */
	static def imp(x: boolean, y: boolean): boolean {
		return (!x||y);
	}

	public static def main(Rail[String]) {
		new FutureTest3().execute();
	}
}
