/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */

import harness.x10Test;

/**
 * Check that the compiler can accept int lits in hex.
 *
 * @author vj 1/2006
 */
public class IntLitHex extends x10Test {

	public def run(): boolean = {
		x10.io.Console.OUT.println(0xABCABC);
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new IntLitHex().execute();
	}

	
}
