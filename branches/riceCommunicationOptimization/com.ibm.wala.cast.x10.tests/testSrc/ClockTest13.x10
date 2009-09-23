//OPTIONS: -PLUGINS=x10.klock.plugin.KlockPlugin
//CLASSPATH: ../classes/klock.jar
/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */


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
public class ClockTest13 {

	const int N = 20; // total clock periods per activity
	const int M = N/2; // when to change from forward to reverse
	const int chainLength = 3;
	int phaseA = 0;
	int phaseB = 0;
	int phaseC = 0;
	int phaseD = 0;

	public boolean run() {
		finish async {
			final clock a = clock.factory.clock();
			final clock b = clock.factory.clock();
			final clock c = clock.factory.clock();
			async clocked(a) taskA(a);
			async clocked(a, b) taskB(a, b);
			async clocked(b, c) taskC(b, c);
			async clocked(c) taskD(c);
		}
		return true;
	}

	void taskA(final clock a) {
		for (point [k]: [1:N]) {
			System.out.println(k+" A new phase");
			atomic phaseA++;
			System.out.println(k+" A resuming a");
			a.resume();
			if (k <= M-chainLength) {
				System.out.println(k+" Waiting for forward phase shift");
				when (phaseB == phaseA+1 &&
						phaseC == phaseB+1 &&
						phaseD == phaseC+1)
				{
					System.out.println(k+" Max forward phase shift reached");
				}
			}
			next;
		}
	}
	void taskB(final clock a, final clock b) {
		for (point [k]: [1:N]) {
			System.out.println(k+" B new phase");
			atomic phaseB++;
			System.out.println(k+" B resuming a");
			a.resume();
			System.out.println(k+" B resuming b");
			b.resume();
			System.out.println(k+" B before next");
			next;
		}
	}
	void taskC(final clock b, final clock c) {
		for (point [k]: [1:N]) {
			System.out.println(k+" C new phase");
			atomic phaseC++;
			System.out.println(k+" C resuming b");
			b.resume();
			System.out.println(k+" C resuming c");
			c.resume();
			System.out.println(k+" C before next");
			next;
		}
	}
	void taskD(final clock c) {
		for (point [k]: [1:N]) {
			System.out.println(k+" D new phase");
			atomic phaseD++;
			System.out.println(k+" D resuming c");
			c.resume();
			if (k >= M && k <= N-chainLength) {
				System.out.println(k+" Waiting for reverse phase shift");
				when (phaseC == phaseD+1 &&
						phaseB == phaseC+1 &&
						phaseA == phaseB+1)
				{
					System.out.println(k+" Max reverse phase shift reached");
				}
			}
			System.out.println(k+" D before next");
			next;
		}
	}

	public static void main(String[] args) {
		new ClockTest13().run();
	}
}

