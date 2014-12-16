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
 * Clock test for multiple clocks.
 * Testing semantics of next with multiple clocks.
 *
 * For a clock c: I cannot advance to my next phase
 * until all activities registered with me have executed
 * resume on me for the current phase,
 * and all activities scheduled for completion
 * in my current phase (with now(c)) have globally finished.
 *
 * My phase zero starts when I am declared/created.
 *
 * For an activity a: My next cannot advance to the
 * following statement, until all clocks
 * that I am currently registered with have advanced to
 * their next phase.
 *
 * next will do an implicit resume first on each of the clocks
 * I am registered with.
 *
 * I get registered with a clock c by creating/declaring c,
 * or by being enclosed in a clocked(...,c,...) statement.
 *
 * I can register a child activity of mine with some of the clocks
 * I am already registered with by
 * async(P) clocked(c1,..,cn) S
 *
 * I can post a child activity for global completion during the
 * current phase of some of the clocks I am registered with,
 * by now(c1,...,cn) S
 *
 * I can deregister with the clocks I am registered with, by terminating
 *
 * Expected result of this test: should not deadlock
 * and should not throw Error
 *
 * Like ClockTest5, but
 * when the parent terminates, it is as if
 * it executed a next on the clocks it is still registered with.
 * Children should be able to proceed with their next statements
 * after the parent terminates.
 *
 * This test uses multiple clocks but also uses a common
 * clock for all activities. The common clock forces
 * the next to behave like ordinary lock-step barriers (not
 * guaranteed in general, see ClockTest15).
 *
 * @author kemal 4/2005
 */
public class ClockTest6 extends x10Test {

	public static N_INSTANCES: int = 8n; //number of instances of each async activity kind
	public static N_NEXTS: int = 4n; //number of next pairs in each async activity
	public static N_KINDS: int = 4n; // number of kinds of async activities
	var globalCounter: int = 0n;

        public var quiet:boolean = false;

	public def run(): boolean = {
		finish async {
			// create and register with multiple clocks
			val c: Clock = Clock.make();
			val d: Clock = Clock.make();
			val e: Clock = Clock.make();
			// Spawn subactivities using different subset of the clocks
			// The subactivities will perform N_NEXTS next pairs each
			for (i in 1n..N_INSTANCES) {
				/*Activity kind: 1 clocks = (c)*/
				async clocked(c)
					for (tick in 0n..(N_NEXTS-1n)) {
						// do work
						doWork("1_", i, "(c)", tick);
						Clock.advanceAll(); //barrier
						// verify that work in prior phase is correct
						verify("1_", i, tick);
						Clock.advanceAll(); //barrier
					}
				/*Activity kind: 2 clocks = (c, d)*/
				async clocked(c, d)
					for (tick in 0n..(N_NEXTS-1n)) {
						// do work
						doWork("2_", i, "(c, d)", tick);
						Clock.advanceAll(); //barrier
						// verify that work in prior phase is correct
						verify("2_", i, tick);
						Clock.advanceAll(); //barrier
					}
				/*Activity kind: 3 clocks = (c, e)*/
				async  clocked(c, e)
					for (tick in 0n..(N_NEXTS-1n)) {
						// do work
						doWork("3_", i, "(c, e)", tick);
						Clock.advanceAll(); //barrier
						// verify that work in prior phase is correct
						verify("3_", i, tick);
						Clock.advanceAll(); //barrier
					}
				/*Activity kind: 4 clocks = (c, d, e)*/
				async clocked(c, d, e)
					for (tick in 0n..(N_NEXTS-1n)) {
						// do work
						doWork("4_", i, "(c, d, e)", tick);
						Clock.advanceAll(); //barrier
						// verify that work in prior phase is correct
						verify("4_", i, tick);
						Clock.advanceAll(); //barrier
					}
			}
			// Here all children have registered with
			// their clocks, but have not advanced beyond their first next
			// Parent terminates so children can proceed.
		}
		// Wait until all activities are finished
		return true;
	}

	/**
	 * Each activity increments a global counter and prints a message
	 */
	def doWork(var kind: String, var instance: int, var clocks: String, var tick: int): void = {
		atomic globalCounter++;
		if (!quiet) x10.io.Console.OUT.println("Activity "+kind+instance+" in phase "+tick+" of clocks "+clocks);
	}

	/**
	 * This verifies that in the prior phase all
	 * activities have incremented the counter
	 */
	def verify(var kind: String, var instance: int, var tick: int): void = {
		var tmp: int;
		atomic { tmp = globalCounter; }
		chk((tick+1n) * N_KINDS * N_INSTANCES == tmp);
	}

	public static def main(var args: Rail[String]): void = {
		new ClockTest6().executeAsync();
	}
}
