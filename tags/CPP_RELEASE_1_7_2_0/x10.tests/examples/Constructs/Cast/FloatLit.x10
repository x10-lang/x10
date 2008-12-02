/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a float literal can be cast to float.
 */
public class FloatLit extends x10Test {
	public def run(): boolean = {
		var f: float = 0.001 to float;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new FloatLit().execute();
	}


}
