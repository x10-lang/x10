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

/**
 * It is not allowed to transmit a clock c to a subactivity,
 * after the current activity has resumed the clock c,
 * until the current activity has executed next.
 * Doing so will result in a run-time error.
 * TODO: rename to ClockTest18 and check for exception
 *
 * @author kemal, 6/2005
 */
public class ClockTest18 extends x10Test {

	public def run(): boolean {
		try {
		    finish{
		         /*A0*/
		        val c0: Clock = Clock.make();
		        val  x = new X();
		        // f0 does not transmit clocks to subactivity
		        val  f0:foo = new foo() {
			        public operator this(): void {
				        /* Activity A3 */
				        async {
					        x10.io.Console.OUT.println("#A3: hello from A3");
				        }
			        }
		         };
		        // f1 transmits clock c0 to subactivity
		        val f1:foo  = new foo() {
			        public operator this(): void {
				         /*Activity A2*/
				         async clocked(c0) {
					        x10.io.Console.OUT.println("#A2 before resume");
					        c0.resume();
					        x10.io.Console.OUT.println("#A2 before next");
					        Clock.advanceAll();
					        x10.io.Console.OUT.println("#A2 after next");
				         }
			         }
		         };

		val fooArray = [f0,f1];
		x10.io.Console.OUT.println("#A0 before resume");
		c0.resume();
		x10.io.Console.OUT.println("#A0 before spawning A3");
		Y.test(fooArray(x.zero()));
		x10.io.Console.OUT.println("#A0 before spawning A2");
		Y.test(fooArray(x.one()));
		x10.io.Console.OUT.println("#A0 before spawning A1");
		async clocked(c0) { x10.io.Console.OUT.println("#A1: hello from A1"); }
		x10.io.Console.OUT.println("#A0 before next");
		Clock.advanceAll();
		x10.io.Console.OUT.println("#A0 after next");
		}
		} catch (e: MultipleExceptions) {
			x10.io.Console.OUT.println("MultipleExceptions");
			return false;
		}
		return true;
	}

	public static def main(Rail[String]){
		new ClockTest18().execute();
	}

	/**
	 * A class to invoke a 'function pointer'
	 */
	static class Y {
		static def test(f: foo): void {
			f(); // it is hard to determine what f does at compile time
		}
	}

	/**
	 * An interface to use like a simple 'function pointer'
	 *
	 * foo f1 = new foo() { public void apply() S1 }; //assign body S1 to f1
	 *
	 * // values of free final variables of S1 are also captured in f1
	 *
	 * f1(); // invoke S1 indirectly using its captured
	 *
	 * // free variables
	 */
	static interface foo {
		public  operator this(): void;
	}

	/**
	 * Dummy class to make static memory disambiguation difficult
	 * for a typical compiler
	 */
	static class X {
		public val z = [1,0];
		def zero(): long { return z(z(z(1))); /* that is a 0 */ }
		def one(): long { return z(z(z(0))); /* that is a 1 */ }
		def modify(): void { z(0) += 1; }
	}
}
