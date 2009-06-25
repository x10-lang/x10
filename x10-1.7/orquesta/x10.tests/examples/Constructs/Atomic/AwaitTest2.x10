/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Minimal test for await.
 */
public class AwaitTest2 extends x10Test {

	var val: int = 42;

	public def run(): boolean = {
		await(val == 42);
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new AwaitTest2().execute();
	}
}
