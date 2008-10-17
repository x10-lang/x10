/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */

import harness.x10Test;;

/**
 * An error must be thrown by the compiler on encountering a lit with type int and the value
 of the literal cannot be stored in an int. 
 *
 * @author vj 1/2006
 */
public class IntLitOct extends x10Test {

	public def run(): boolean = {
		System.out.println(01124563);
		return 01124563==305523;
	}

	public static def main(var args: Rail[String]): void = {
		new IntLitOct().execute();
	}

	
}
