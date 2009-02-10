/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Testing future of x10.compilergenerated.BoxedInteger.
 * This should not be visible to user.
 */
public class Future0Boxed extends x10Test {
	public def run(): boolean = {
	    val fx = future 42 as Box[Int];
		return fx() == 42;
	}

	public static def main(var args: Rail[String]): void = {
		new Future0Boxed().execute();
	}
}
