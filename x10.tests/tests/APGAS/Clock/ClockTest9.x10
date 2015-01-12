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
 * Nested barriers test.
 *
 * N outer activities each
 * create M inner activities that do
 * barrier syncs. Then the outer activities do a barrier sync
 * and check the results.
 *
 * @author kemal 4/2005
 */
public class ClockTest9 extends x10Test {

	public static N: long = 8;
	public static M: long = 8;
	val v  = new Rail[long](N, 0);

	public def run(): boolean = {
		finish async {
			val c: Clock = Clock.make();

			// outer barrier loop
			for (i in 0..(N-1)) async clocked(c) {
				foreachBody(i, c);
			}
		}
		return true;
	}

	def foreachBody(i: long, c: Clock): void = {
		async clocked(c) finish async  {
			val d: Clock = Clock.make();

			// inner barrier loop
			for (j in 0..(M-1)) async clocked(d) {
				foreachBodyInner(i, j, d);
			}
		}
		x10.io.Console.OUT.println("#0a i = "+i);
		Clock.advanceAll();
		// at this point each val[k] must be 0
		async clocked(c) finish async for (k in 0..(N-1)) chk(v(k) == 0);
		x10.io.Console.OUT.println("#0b i = "+i);
		Clock.advanceAll();
	}

	def foreachBodyInner(i: long, j: long, d: Clock): void = {
		// activity i, j increments val[i] by j
		async clocked(d) finish async { atomic v(i) += j; }
		x10.io.Console.OUT.println("#1 i = "+i+" j = "+j);
		Clock.advanceAll();
		// val[i] must now be SUM(j = 0 to M-1)(j)
		async clocked(d) finish async  { var tmp: long; atomic tmp = v(i); chk(tmp == M*(M-1)/2); }
		x10.io.Console.OUT.println("#2 i = "+i+" j = "+j);
		Clock.advanceAll();
		// decrement val[i] by the same amount
		async clocked(d) finish async  { atomic v(i) -= j; }
		x10.io.Console.OUT.println("#3 i = "+i+" j = "+j);
		Clock.advanceAll();
		// val[i] should be 0 by now
	}

	public static def main(Rail[String])  {
		new ClockTest9().executeAsync();
	}
}
