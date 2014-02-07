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
 * Out of phase clocks due to resume:
 *
 * Given clocks a,b,c, and activities A,B,C,D registered with these
 * clocks as follows:
 *
 * <code>
   a   b   c   clocks
  / \ / \ / \
  A  B   C   D activities
 * </code>
 *
 * This test first forces D to move 3 clock periods ahead of A and
 * then forces A to move 3 clock periods ahead of D.
 *
 * Example script of parallel activities:
 * <code>
  a, b, etc. = a.resume(), b.resume()
  -- n ( n'th next un-blocked here)

  A  B  C  D
  a  a
  b  b
  c  c
  ---------    1
  a              A waits for D's period 3
  b  b
  c  c
  ------    2

  b
  c  c
  ---    3

  c        D ack period 3 to A's period 0
  -               1
  a
  ----            2
  a  a
  b
  -------         3
  a  a
  b  b
  c
  ------------    4
  c  c
  b  b
  a  a
  -------         5
  c           D waits for A's period 7
  b  b
  a  a
  ----            6

  b
  a  a
  -               7

  a                 A ack period 7 to D's period 4
 * </code>
 *
 * Desired behavior: should not deadlock
 *
 * @author kemal 4/2005
 */
public class ClockTest13 extends x10Test {

	public static N: long = 20; // total clock periods per activity
	public static M: long = N/2; // when to change from forward to reverse
	public static chainLength: long = 3;
	var phaseA: long = 0;
	var phaseB: long = 0;
	var phaseC: long = 0;
	var phaseD: long = 0;

	public def run(): boolean = {
		finish async {
			val a = Clock.make();
			val b = Clock.make();
			val c = Clock.make();
			async clocked(a) taskA(a);
			async clocked(a, b) taskB(a, b);
			async clocked(b, c) taskC(b, c);
			async clocked(c) taskD(c);
		}
		return true;
	}

	def taskA(val a: Clock): void = {
		for (k in 1..N) {
			x10.io.Console.OUT.println(""+k+" A new phase");
			atomic phaseA++;
			x10.io.Console.OUT.println(""+k+" A resuming a");
			a.resume();
			if (k <= M-chainLength) {
				x10.io.Console.OUT.println(""+k+" Waiting for forward phase shift");
				when (phaseB == phaseA+1 &&
						phaseC == phaseB+1 &&
						phaseD == phaseC+1)
				{
					x10.io.Console.OUT.println(""+k+" Max forward phase shift reached");
				}
			}
			Clock.advanceAll();
		}
	}
	def taskB(val a: Clock, val b: Clock): void = {
		for (k in 1..N) {
			x10.io.Console.OUT.println(""+k+" B new phase");
			atomic phaseB++;
			x10.io.Console.OUT.println(""+k+" B resuming a");
			a.resume();
			x10.io.Console.OUT.println(""+k+" B resuming b");
			b.resume();
			x10.io.Console.OUT.println(""+k+" B before next");
			Clock.advanceAll();
		}
	}
	def taskC(val b: Clock, val c: Clock): void = {
		for (k in 1..N) {
			x10.io.Console.OUT.println(""+k+" C new phase");
			atomic phaseC++;
			x10.io.Console.OUT.println(""+k+" C resuming b");
			b.resume();
			x10.io.Console.OUT.println(""+k+" C resuming c");
			c.resume();
			x10.io.Console.OUT.println(""+k+" C before next");
			Clock.advanceAll();
		}
	}
	def taskD(val c: Clock): void = {
		for (k in 1..N) {
			x10.io.Console.OUT.println(""+k+" D new phase");
			atomic phaseD++;
			x10.io.Console.OUT.println(""+k+" D resuming c");
			c.resume();
			if (k >= M && k <= N-chainLength) {
				x10.io.Console.OUT.println(""+k+" Waiting for reverse phase shift");
				when (phaseC == phaseD+1 &&
						phaseB == phaseC+1 &&
						phaseA == phaseB+1)
				{
					x10.io.Console.OUT.println(""+k+" Max reverse phase shift reached");
				}
			}
			x10.io.Console.OUT.println(""+k+" D before next");
			Clock.advanceAll();
		}
	}

	public static def main(Rail[String]) {
		new ClockTest13().execute();
	}
}
