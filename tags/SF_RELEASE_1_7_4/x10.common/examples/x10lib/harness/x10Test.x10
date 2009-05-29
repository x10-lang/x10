/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package harness;

import java.util.Random;

/**
 * Test harness abstract class.
 * FIXME: remove the ugly hack of relying on Java Threads
 */
public abstract class x10Test  extends x10.lang.Object {

	/**
	 * The body of the test.
	 * @return true on success, false on failure
	 */
	protected abstract boolean run();

	public void executeAsync() {
		final boolean b[] = new boolean[] { false };
		Thread timer = startTimeoutTimer();
		try {
			finish async(this) b[0] = this.run();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		timer.interrupt();
		reportResult(b[0]);
	}

	public void execute() {
		boolean b = false;
		Thread timer = startTimeoutTimer();
		try {
			finish b = this.run();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		timer.interrupt();
		reportResult(b);
	}

	public static final String PREFIX = "++++++ ";
	public static void success() {
		System.out.println(PREFIX+"Test succeeded.");
		x10.lang.Runtime.setExitCode(0);
	}
	public static void failure() {
		System.out.println(PREFIX+"Test failed.");
		x10.lang.Runtime.setExitCode(1);
	}
	protected static void reportResult(boolean b) {
		if (b) success(); else failure();
	}

	/**
	 * Check if a given condition is true, and throw an error if not.
	 */
	public static safe void chk(boolean b) {
		if (!b) throw new Error();
	}

	/**
	 * Check if a given condition is true, and throw an error with a given
	 * message if not.
	 */
	public static safe void chk(boolean b, String s) {
		if (!b) throw new Error(s);
	}

	private Random myRand = new Random(1L);

	/**
	 * Return a random integer between lb and ub (inclusive)
	 */
	protected int ranInt(int lb, int ub) {
		return lb + myRand.nextInt(ub-lb+1);
	}

	/**
	 * Start the timeout timer for the number of seconds specified in the
	 * "x10test.timeout" system property (default is 300).
	 * @return the timer thread.
	 */
	private static Thread startTimeoutTimer() {
		final int seconds = java.lang.Integer.getInteger("x10test.timeout", 300).intValue();
		// Cannot use async -- have to force a real Thread
		Thread timer = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(seconds*1000);
					x10.lang.Runtime.exit(128);
				} catch (InterruptedException e) {}
			}
		});
		timer.start();
		return timer;
	}
}
