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
 * Clock test for barrier functions
 * @author kemal 3/2005
 */
public class ClockTest3a extends x10Test {

	var value: long = 0;
	static val N: long = 32;

	public def run(): boolean = {
		clocked finish {

			for  (i in 0..(N-1)) clocked async {
				clocked async  finish async { atomic value++; }
				Clock.advanceAll();
				if (value != N) {
					throw new Exception();
				}
				Clock.advanceAll();
				clocked async  finish async { atomic value++; }
				Clock.advanceAll();
			}
			Clock.advanceAll(); Clock.advanceAll(); Clock.advanceAll();
			if (value != 2*N) {
				throw new Exception();
			}
		}
		return true;
}

public static def main(Rail[String]) {
	new ClockTest3a().executeAsync();
}
}
