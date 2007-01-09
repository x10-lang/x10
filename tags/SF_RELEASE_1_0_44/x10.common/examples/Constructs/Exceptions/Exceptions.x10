/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test for x10 exceptions.
 */
public class Exceptions extends x10Test {
	boolean ok;
	public boolean run() {
		ok = true;
		try {
			finish {
				async(here) {
					if (true) throw new x10.lang.Exception();
				}
			}
			System.out.println("Exception missed!");
			return false;
		} catch (x10.lang.Exception e) {
			//System.out.println("Async exception caught!");
			// expected
		}

		try {
			finish {
				future<int> ret
					= future(here) { m() };
			}
			//System.out.println("Exception in future missed!");
			// expected
		} catch (x10.lang.MultipleExceptions me) {
			System.out.println("Multiple Exception caught where there should be none!");
			return false;
		} catch (x10.lang.Exception e) {
			System.out.println("Caught future exception without force!");
			return false;
		}

		try {
			finish {
				try {
					future(here) { m() }.force();
					System.out.println("Force not thrown!");
					ok = false;
				} catch (x10.lang.Exception e) {
					// System.out.println("Force-caught!");
					// expected
				}
			}
		} catch (x10.lang.Exception e) {
			System.out.println("Force-rethrow caught!");
			return false;
		}

		try {
			finish {
				future(here) { m() }.force();
				System.out.println("Force did not throw!");
				ok = false;
			}
			System.out.println("Force not propagated!");
			return false;
		} catch (x10.lang.MultipleExceptions me) {
			System.out.println("Multiple Exception (force + async) caught, single expected!");
			return false;
		} catch (x10.lang.Exception e) {
			//System.out.println("Caught single exception transitively!");
			// expected
		}

		try {
			finish {
				async(here) { m(); };
				future(here) { m() }.force();
				System.out.println("Force did not throw!");
				ok = false;
			}
			System.out.println("Force not propagated!");
			return false;
		} catch (x10.lang.MultipleExceptions me) {
			//System.out.println("Multiple Exception (force + async) caught!");
			// expected
		} catch (x10.lang.Exception e) {
			System.out.println("Caught single exception where multiple was expected!");
			return false;
		}

		return ok;
	}

	static int m() {
		if (true) throw new x10.lang.Exception();
		return 42;
	}

	public static void main(String[] args) {
		new Exceptions().execute();
	}
}

