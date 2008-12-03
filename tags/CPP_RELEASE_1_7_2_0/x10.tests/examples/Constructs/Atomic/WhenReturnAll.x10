/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Returns from all branches of a "when" should work.
 *
 * @author igor, 2/2006
 */
public class WhenReturnAll extends x10Test {

	def test(): int = {
		var ret: int = 0;
		when (X.t()) {
			return 1;
		} or (X.t()) {
			return 2;
		}
	}

	public def run(): boolean = {
		var x: int = test();
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new WhenReturnAll().execute();
	}

	static class X {
		static def t(): boolean = { return true; }
	}
}
