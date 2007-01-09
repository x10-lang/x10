/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test harness runner.
 */
public class TestHarness {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("No test to run");
			x10Test.success();
			return;
		}
		try {
			x10Test t = (x10Test) Class.forName(args[0]).newInstance();
			t.execute();
		} catch (ClassNotFoundException e) {
			System.err.println("Test class "+args[0]+" not found: "+e);
		} catch (IllegalAccessException e) {
			System.err.println("Cannot access the constructor for "+args[0]+": "+e);
		} catch (InstantiationException e) {
			System.err.println("Cannot construct an instance of "+args[0]+": "+e);
		}
	}
}

