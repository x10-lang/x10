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
 * Test that loops forever (or until it times out).
 * Useful for testing the time limit feature.
 * This test is supposed to fail after the time limit elapses.
 */
public class InfiniteLoopTest_MustFailTimeout extends x10Test {

	var flag: boolean = true;
	public def run(): boolean {
		while (flag) ;
		return true;
	}

	public static def main(var args: Rail[String]): void {
		new InfiniteLoopTest_MustFailTimeout().execute();
	}
}
