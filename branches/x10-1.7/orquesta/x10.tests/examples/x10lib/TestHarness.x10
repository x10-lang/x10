/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

import java.lang.Class;
import java.lang.ClassNotFoundException;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import x10.lang.System;

/**
 * Test harness runner.
 */
public class TestHarness {

	public static def main(var args: Rail[String]): void = {
		if (args.length < 1) {
			System.err.println("No test to run");
			x10Test.success();
			return;
		}
		try {
			var t: x10Test = (x10Test) Class.forName(args(0)).newInstance();
			t.execute();
		} catch (var e: ClassNotFoundException) {
			System.err.println("Test class "+args(0)+" not found: "+e);
		} catch (var e: IllegalAccessException) {
			System.err.println("Cannot access the constructor for "+args(0)+": "+e);
		} catch (var e: InstantiationException) {
			System.err.println("Cannot construct an instance of "+args(0)+": "+e);
		}
	}
}
