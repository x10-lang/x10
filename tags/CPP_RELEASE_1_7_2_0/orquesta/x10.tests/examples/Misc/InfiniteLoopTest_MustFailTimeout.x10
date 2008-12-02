/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Test that loops forever (or until it times out).
 * Useful for testing the time limit feature.
 * This test is supposed to fail after the time limit elapses.
 */
public class InfiniteLoopTest_MustFailTimeout extends x10Test {

	var flag: boolean = true;
	public def run(): boolean = {
		while (flag) ;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new InfiniteLoopTest_MustFailTimeout().execute();
	}
}
