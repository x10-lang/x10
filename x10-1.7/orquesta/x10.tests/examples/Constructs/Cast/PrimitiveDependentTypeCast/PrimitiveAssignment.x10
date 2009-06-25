/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Purpose: Checks primitive type assignment of java.
 * @author vcave
 **/
public class PrimitiveAssignment extends x10Test {
	public def run(): boolean = {
		var b: byte = 2;
		var c: char = 'c';
		var s: short = 10;
		var j: int = 124;
		var l: long = 1;
		var f: float = 0;
		var d: double = 0.001;

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new PrimitiveAssignment().execute();
	}
}
