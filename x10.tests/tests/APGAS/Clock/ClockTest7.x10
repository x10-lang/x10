/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

/**
 * Combination of finish and clocks.
 * Finish cannot pass any clock to a subactivity.
 * TODO: rename to ClockTest7 and check for exception
 *
 * OLD SEMANTICS:
 * Language clarification needed:
 * (what if async clocked(c) S occurs inside a library method
 * or is invoked via an indirect function?
 * Compiler analysis may be difficult).
 * This test currently causes a deadlock at run time.
 * How should it behave in the ultimate x10 definition?
 *
 * Temporarily declaring that this is an error that should be
 * caught at compile time (this may not be possible).
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
 * @author kemal 3/2005
 */
public class ClockTest7 extends x10Test {

	var value: long = 0;
	static val N: long = 16;

	public def run(): boolean = {
		try {
		val c: Clock = Clock.make();

		finish for (i in 0..(N-1)) async {
			atomic value++;
			x10.io.Console.OUT.println("Activity "+i+" phase 0");
			Clock.advanceAll();
			atomic chk(value == N);
			x10.io.Console.OUT.println("Activity "+i+" phase 1");
			Clock.advanceAll();
			atomic value++;
			x10.io.Console.OUT.println("Activity "+i+" phase 2");
			c.advance();
		}

		Clock.advanceAll(); Clock.advanceAll(); Clock.advanceAll();

		atomic chk(value == 2*N);

		} catch (e: MultipleExceptions) {
            // [DC] I believe that the async should never throw any exception.
			x10.io.Console.OUT.println("MultipleExceptions");
			return true;
		} catch (e: Error) {
			x10.io.Console.OUT.println("Error");
			return true;
		}

		return false;
	}

	public static def main(Rail[String]) {
		new ClockTest7().executeAsync();
	}
}
