/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Assigning a char literal to a char array.
 */
public class AssignIntToChar extends x10Test {

	/**
	 * Testing comments for run
	 */
	public def run(): boolean = {
	    val a = Rail.make[char](4, (x:int)=>0 as char);
	    var bit1: boolean = true;
	    var bit2: boolean = false;
	    a(1) = (bit2 ? 'A' : 'C') ;
	    return true;
	}

	/**
	 * Testing comments for main
	 */
	public static def main(var args: Rail[String]): void = {
		new AssignIntToChar().execute();
	}
}
