/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks regular java cast works.
 * @author vcave
 **/
public class CastLitteralToPrimitive extends x10Test {

	public def run(): boolean = {
		var boolt: boolean = true to boolean;
		var boolf: boolean = false to boolean;
		var b: byte = 1 to byte;
		var i: int = 1 to int;
		var s: short = 1 to short;
		var l: long = 1 to long;
		var d: double = 1.0 to double;
		var f: float = 1.0 to float;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new CastLitteralToPrimitive().execute();
	}
}
