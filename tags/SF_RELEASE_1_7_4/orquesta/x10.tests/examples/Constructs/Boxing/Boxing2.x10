/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests interaction of parentheses and boxing
 */
public class Boxing2 extends x10Test {

	public def run(): boolean = {
		var x: String = "The number is "+(X.five()*2);
		if (!x.equals("The number is 10")) return false;
		var y: String = "The number is "+(200+X.five()*2);
		if (!y.equals("The number is 210")) return false;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Boxing2().execute();
	}

	static class X {
		public static def five(): int = { return 5; }
	}
}
