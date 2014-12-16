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
 * Cannot register a child with a clock
 * I am not registered with.
 *
 * NEW SEMANTICS: Clock Use Exception such as
 *
 * 'Transmission of c (to a child) requires that I am registered with c'
 *
 * 'Transmission of c requires that I am not between c.resume() and a next'
 *
 * 'The immediate body of finish  can never transmit any clocks'
 *
 * are now caught at run time. The compiler
 * can remove the run time checks using static techniques,
 * and can issue warnings when it is statically detected that
 * clock use exceptions will
 * definitely occur, or will likely occur.
 *
 * @author kemal 4/2005
 */
public class ClockTest11 extends x10Test {

	public def run(): boolean = {
		try {
		    finish async {
			val c = Clock.make();
			val d = Clock.make();
			async clocked(d) {
			    async clocked(c) { x10.io.Console.OUT.println("hello"); }
			}
		    }
		    return false;
		} catch (var e: MultipleExceptions) {
            return (e.exceptions.size == 1L && e.exceptions(0) instanceof ClockUseException);
		}
	}

	public static def main(var args: Rail[String]): void = {
		new ClockTest11().execute();
	}
}
