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
public class Future1Boxed extends x10Test {
	public def run(): boolean = {
	    val x:Box[Int] = 42;
	    val fx = future (here) x;
		return fx() == 42;
	}

	public static def main(var args: Rail[String]): void = {
		new Future1Boxed().execute();
	}
}
