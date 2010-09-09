/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test that does nothing.  Useful to test the testing
 * harness.
 */
public class NopTest extends x10Test {

	public def run(): boolean = {
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new NopTest().execute();
	}
}
