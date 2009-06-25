/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

public class MissingReturn_MustFailCompile extends x10Test {

	def foo(): boolean = {
	}

	public def run(): boolean = {
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new MissingReturn_MustFailCompile().execute();
	}
}
