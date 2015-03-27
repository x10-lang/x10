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
 * Expected result of this test: should not deadlock.
 *
 * Important: The next's do not go in lock step in this test case!
 *
 * For example: activities using (e) may pass their nexts
 * as soon as all other activities using e have arrived
 * at their nexts: (c,e), (d,e) (c,d,e), although (c,d),(c),(d)
 * have not yet arrived at their nexts.
 *
 * Also see ClockTest15.
 *
 * @author kemal 4/2005
 */
public class ClockTest5 extends x10Test {

	public def run(): boolean {
		val c: Clock = Clock.make();
		val d: Clock = Clock.make();
		val e: Clock = Clock.make();
		/*Activity_1A*/async clocked(c) { m("1A", "(c)", 0); Clock.advanceAll(); m("1A", "(c)", 1); Clock.advanceAll(); }
		/*Activity_1B*/async clocked(c) { m("1B", "(c)", 0); Clock.advanceAll(); m("1B", "(c)", 1); Clock.advanceAll(); }
		/*Activity_2A*/async clocked(d) { m("2A", "(d)", 0); Clock.advanceAll(); m("2A", "(d)", 1); Clock.advanceAll(); }
		/*Activity_2B*/async clocked(d) { m("2B", "(d)", 0); Clock.advanceAll(); m("2B", "(d)", 1); Clock.advanceAll(); }
		/*Activity_3A*/async clocked(e) { m("3A", "(e)", 0); Clock.advanceAll(); m("3A", "(e)", 1); Clock.advanceAll(); }
		/*Activity_3B*/async clocked(e) { m("3B", "(e)", 0); Clock.advanceAll(); m("3B", "(e)", 1); Clock.advanceAll(); }
		/*Activity_4A*/async clocked(c, d) { m("4A", "(c, d)", 0); Clock.advanceAll(); m("4A", "(c, d)", 1); Clock.advanceAll(); }
		/*Activity_4B*/async clocked(c, d) { m("4B", "(c, d)", 0); Clock.advanceAll(); m("4B", "(c, d)", 1); Clock.advanceAll(); }
		/*Activity_5A*/async clocked(c, e) { m("5A", "(c, e)", 0); Clock.advanceAll(); m("5A", "(c, e)", 1); Clock.advanceAll(); }
		/*Activity_5B*/async clocked(c, e) { m("5B", "(c, e)", 0); Clock.advanceAll(); m("5B", "(c, e)", 1); Clock.advanceAll(); }
		/*Activity_6A*/async clocked(d, e) { m("6A", "(d, e)", 0); Clock.advanceAll(); m("6A", "(d, e)", 1); Clock.advanceAll(); }
		/*Activity_6B*/async clocked(d, e) { m("6B", "(d, e)", 0); Clock.advanceAll(); m("6B", "(d, e)", 1); Clock.advanceAll(); }
		/*Activity_7A*/async clocked(c, d, e) { m("7A", "(c, d, e)", 0); Clock.advanceAll(); m("7A", "(c, d, e)", 1); Clock.advanceAll(); }
		/*Activity_7B*/async clocked(c, d, e) { m("7B", "(c, d, e)", 0); Clock.advanceAll(); m("7B", "(c, d, e)", 1); Clock.advanceAll(); }
		async clocked(c, d, e) finish async x10.io.Console.OUT.println("Parent activity in phase 0 of (c,d,e)");
		Clock.advanceAll();
		async clocked(e, c, d) finish async x10.io.Console.OUT.println("Parent activity in phase 1 of (c,d,e)");
		Clock.advanceAll();
		return true;
	}

	static def m(var a: String, var clocks: String, var phase: long): void {
		x10.io.Console.OUT.println("Actitivity "+a+" in phase "+phase+" of clocks "+clocks);
	}

	public static def main(var args: Rail[String]): void {
		new ClockTest5().executeAsync();
	}
}
