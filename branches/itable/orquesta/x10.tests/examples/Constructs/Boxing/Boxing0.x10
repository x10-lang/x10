/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Automatic boxing and unboxing of a final value class
 * during up-cast and down-cast.
 */
public class Boxing0 extends x10Test {

	public def run(): boolean = {
		var o: x10.lang.Object = X.five();
		var i: int = (o to int) + 1;
		return (i==6);
	}

	public static def main(var args: Rail[String]): void = {
		new Boxing0().execute();
	}

	static class X {
		public static def five(): int = { return 5; }
	}
}
