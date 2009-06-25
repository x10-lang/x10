/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */

import harness.x10Test;

/**
 * An error must be thrown by the compiler on encountering a lit with type int and the value
 of the literal cannot be stored in an int. 
 *
 * @author vj 1/2006
 */
public class IntLitOutOfRange_MustFailCompile extends x10Test {

	public def run(): boolean = {
		x10.io.Console.OUT.println(2147493647);
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new IntLitOutOfRange_MustFailCompile().execute();
	}

	
}
