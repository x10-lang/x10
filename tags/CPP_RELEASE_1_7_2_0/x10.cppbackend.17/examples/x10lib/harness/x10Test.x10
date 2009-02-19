/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package harness;

/**
 * Test harness abstract class.
 */
public abstract class x10Test extends x10.lang.Object {

	/**
	 * The body of the test.
	 * @return true on success, false on failure
	 */
	protected abstract boolean run();

	public void executeAsync() {
		execute();
	}

	public void execute() {
		boolean b = false;
		try {
			finish b = this.run();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
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
}
