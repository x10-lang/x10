/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The argument to await must be boolean
 * @author vj  9/2006
 */
public class ArgMustBeBoolean_MustFailCompile extends x10Test {

        var b: int;

	public def run(): boolean = {
		await b; // must fail at compile time.
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ArgMustBeBoolean_MustFailCompile().execute();
	}

	
}
