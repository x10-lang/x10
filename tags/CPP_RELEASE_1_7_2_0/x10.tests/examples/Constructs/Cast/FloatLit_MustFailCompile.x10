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
public class FloatLit_MustFailCompile extends x10Test {
	public def run(): boolean = {
		var f: float = 0.001 as float; // error: should use 'to'
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new FloatLit_MustFailCompile().execute();
	}


}
