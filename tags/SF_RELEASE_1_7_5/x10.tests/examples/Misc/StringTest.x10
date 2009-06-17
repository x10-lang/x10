/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing quotes in strings.
 */
public class StringTest extends x10Test {

	public var v: int;
	public def this(): StringTest = {
		v = 10;
	}

	public def run(): boolean = {
		var foo: String = "the number is "+v;
		if (!(v == 10 && foo.equals("the number is "+"10"))) return false;
		if (foo.charAt(2) != 'e') return false;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new StringTest().execute();
	}
}
