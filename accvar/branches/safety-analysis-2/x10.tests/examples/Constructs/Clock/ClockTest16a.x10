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

/**
 * OLD SEMANTICS:
 * The x10 compiler must conservatively check if an activity can
 * potentially pass a clock it already dropped, to a subactivity.
 * If so, it must report a compile time error.
 * TODO: rename to ClockTest16a and check for exception
 *
 * Language clarification needed on disambiguation
 * algorithm to use.
 * Compiler analysis may not be possible in many cases, such
 * as async's invoked in library methods and indirectly called methods.
 *
 * NEW SEMANTICS: Clock Use Exception such as
 *
 *  'Transmission of c (to a child) requires that I am registered with c'
 *  'Transmission of c requires that I am not between c.resume() and a next'
 *  'The immediate body of finish  can never transmit any clocks'
 *
 * are now caught at run time. The compiler
 * can remove the run time checks using static techniques,
 * and can issue warnings when it is statically detected that
 * clock use exceptions will
 * definitely occur, or will likely occur.
 *
 * Hence this file is renamed as *MustFailRun.x10
 *
 * @author kemal 5/2005
 */
public class ClockTest16a extends x10Test {

	public def run(): boolean = {
		try {
		val x: X! = new X();
		finish async {
			val c0 = Clock.make();
			val c1 = Clock.make();
			val ca: Rail[Clock]! = [c0,c1];
			(ca(0)).drop();

			// Question:
			// Can an activity ever pass a dropped clock to a
			// subactivity of itself, in statement async(cx) S?

			// Compiler answer: NO, actual runtime answer: NO
			// no compiler error
			{
				val cx: Clock = ca(1);
				async clocked(cx) { // no clock use error
					next;
				}
			}

			// Compiler: MAYBE, actual: NO
			// must have a compiler error
			{
				val cx: Clock = ca(x.one());
				async clocked(cx) { //no clock use error
					next;
				}
			}

			var f0: foo! = new foo() {
				public def apply(): void = {
					val cx: Clock = ca(x.zero());
					async clocked(cx) { //clock use error
						next;
					}
				}
			};

			var f1: foo! = new foo() {
				public def apply(): void = {
					val cx: Clock = ca(x.one());
					async clocked(cx) { //no clock use error
						next;
					}
				}
			};

			val fooArray: Rail[foo]! = [f0,f1];  // FIXME: should be Rail[foo!]!

			// Compiler: MAYBE, actual: NO
			// must have a compiler error
			Y.test(fooArray(x.one()) as foo!);

			x10.io.Console.OUT.println("point #1");
			// Compiler: MAYBE, actual: YES
			// must have a compiler error
			Y.test(fooArray(x.zero()) as foo!);

			x10.io.Console.OUT.println("point #2");
			// Compiler: MAYBE, actual: YES
			// must have a compiler error
			{
				val cx: Clock = ca(x.zero());
				async clocked(cx) { // clock use error
					next;
				}
			}

			x10.io.Console.OUT.println("point #3");
			// Compiler: YES, actual: YES
			// must have a compiler error
			{
				val cx: Clock = ca(0);
				async clocked(cx) { // clock use error
					next;
				}
			}
		}
		} catch (e: ClockUseException) {
			return true;
		}
		return false;
	}

	public static def main(var args: Rail[String]): void = {
		new ClockTest16a().execute();
	}

	/**
	 * A class to invoke a 'function pointer' that may do an async
	 */
	static class Y {
		static def test(val f: foo!): void = {
			{
				f.apply(); // it is hard to determine f does an async clocked(c) S,
				//where the current activity is not registered on c
				next;
			}
		}
	}

	/**
	 * An interface to use like a simple 'function pointer'
	 *
	 * foo f1 = new foo() { public void apply() S1 }; //assign body S1 to f1
	 *
	 * // values of free final variables of S1 are also captured in f1
	 *
	 * f1.apply(); // invoke S1 indirectly using its captured
	 *
	 * // free variables
	 */
	static interface foo {
		public  def apply(): void;
	}

	/**
	 * Dummy class to make static memory disambiguation difficult
	 * for a typical compiler
	 */
	static class X {
		public var z: Rail[int]! = [1,0];
		def zero(): int = { return z(z(z(1))); }
		def one(): int = { return z(z(z(0))); }
		def modify(): void = { z(0) += 1; }
	}
}
