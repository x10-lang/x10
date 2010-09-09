/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package harness;

import java.util.Random;
import java.lang.Thread;
import java.lang.Runnable;

/**
 * Test harness abstract class.
 * FIXME: remove the ugly hack of relying on Java Threads
 */
abstract public class x10Test {

	/**
	 * The body of the test.
	 * @return true on success, false on failure
	 */
	abstract protected def run(): boolean;

	public def executeAsync(): void = {
		val b: Rail[boolean] = [ false ]; // use a rail until we have shared locals working
		val timer: Thread = startTimeoutTimer();
		try {
			finish async(this) b(0) = this.run();
		} catch (e: Throwable) {
			e.printStackTrace();
		}
		timer.interrupt();
		reportResult(b(0));
	}

	public def execute(): void = {
		var b: boolean = false;
		var timer: Thread = startTimeoutTimer();
		try {
			finish b = this.run();
		} catch (e: Throwable) {
			e.printStackTrace();
		}
		timer.interrupt();
		reportResult(b);
	}

	public const PREFIX: String = "++++++ ";
	public static def success(): void = {
		System.out.println(PREFIX+"Test succeeded.");
		x10.lang.Runtime.setExitCode(0);
	}
	public static def failure(): void = {
		System.out.println(PREFIX+"Test failed.");
		x10.lang.Runtime.setExitCode(1);
	}
	protected static def reportResult(b: boolean): void = {
		if (b) success(); else failure();
	}

	/**
	 * Check if a given condition is true, and throw an error if not.
	 */
	public static def chk(b: boolean): void = {
		if (!b) throw new Error();
	}

	/**
	 * Check if a given condition is true, and throw an error with a given
	 * message if not.
	 */
	public static def chk(b: boolean, s: String): void = {
		if (!b) throw new Error(s);
	}

	private var myRand: Random = new Random(1L);

	/**
	 * Return a random integer between lb and ub (inclusive)
	 */
	protected def ranInt(lb: int, ub: int): int = {
		return lb + myRand.nextInt(ub-lb+1);
	}

	/**
	 * Start the timeout timer for the number of seconds specified in the
	 * "x10test.timeout" system property (default is 300).
	 * @return the timer thread.
	 */
	private static def startTimeoutTimer(): Thread = {
		val seconds: int = Int.getInteger("x10test.timeout", 300);
		// Cannot use async -- have to force a real Thread
		var timer: Thread = new Thread(new Runnable() {
			public def run(): void = {
				if (x10.lang.Runtime.sleep(seconds*1000)) {
					x10.lang.Runtime.exit(128);
				}
			}
		});
		timer.start();
		return timer;
	}
}
