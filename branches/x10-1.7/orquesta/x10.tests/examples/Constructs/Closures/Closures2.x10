/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test closures.
 *
 * @author nystrom 8/2008
 */
public class Closures2 extends x10Test {
	public def run(): boolean = {
                val j = (() => 7)();
                return j == 7;
	}

	public static def main(var args: Rail[String]): void = {
		new Closures2().execute();
	}
}

