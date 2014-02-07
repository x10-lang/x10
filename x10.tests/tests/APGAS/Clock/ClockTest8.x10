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
 * Tests if we can assign a clock to an array element or
 * field.
 * Tests if clocks can be "aliased" per x10 manual terminology.
 * E.g.: Clock c1 = Clock.make(); clock c2 = c1;
 * //clocks c1 and c2 are "aliased"
 * ca[0] = c1; ca[1] = ca[0];
 * // ca[0] and ca[1] are "aliased"
 * The language definition needs to be cleared up, to define the behavior of this test.
 * TODO: rename to ClockTest8 and check for exception
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
 * @author kemal 4/2005
 */
public class ClockTest8 extends x10Test {

	public def run(): boolean = {
		try {
		finish async {
			var bc: BoxedClock = new BoxedClock(Clock.make());
			val ca = [Clock.make(), bc.value ];
			val c1: Clock = ca(1);
			val c2: Clock = c1; //aliased clocks c2 and c1
			val c3: Clock = ca(0);
			bc.value.drop();
			//TODO: the following line (arrays of clocks) does not parse

			//async clocked(ca[U.zero()])
			val c4: Clock = ca(U.zero());
			async clocked(c4) {
				async clocked(c2) { x10.io.Console.OUT.println("hello"); }
			}
		}
		} catch (e: MultipleExceptions) {
			x10.io.Console.OUT.println("MultipleExceptions");
			return e.exceptions.size == 1L && e.exceptions(0) instanceof ClockUseException;
		} catch (e: ClockUseException) {
			x10.io.Console.OUT.println("ClockUseException");
			return true;
		}
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ClockTest8().executeAsync();
	}

	static class BoxedClock {
		public var value: Clock;
		public def this(val x: Clock): BoxedClock = {
			value = x;
		}
	}

	static class U {
		public static def zero()=0;
	}
}
