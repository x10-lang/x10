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
 * Returns from all branches of a "when" should work.
 * Converted to single-branched when. vj 9/2010
 * @author igor, 2/2006
 */
public class WhenReturnAll extends x10Test {

	def a():boolean = false;
	def test(): long {
		var ret: long = 0;
		when (X.t()) {
			if (a()) {
			  return 1;
			} else {
				return 2;
			}
		}
	}

	public def run(): boolean {
		var x: long = test();
		return true;
	}

	public static def main(var args: Rail[String]): void {
		new WhenReturnAll().execute();
	}

	static class X {
		static def t(): boolean { return true; }
	}
}
