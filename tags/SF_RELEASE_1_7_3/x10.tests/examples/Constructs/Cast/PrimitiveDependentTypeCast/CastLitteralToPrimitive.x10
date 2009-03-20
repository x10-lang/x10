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
		var boolt: boolean = true as boolean;
		var boolf: boolean = false as boolean;
		var b: byte = 1 as byte;
		var i: int = 1 as int;
		var s: short = 1 as short;
		var l: long = 1 as long;
		var d: double = 1.0 as double;
		var f: float = 1.0 as float;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new CastLitteralToPrimitive().execute();
	}
}
