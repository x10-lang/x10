/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that an int lit generates the correct dep clause.
 */
public class LongLitDepType extends x10Test {
	public def run(): boolean = {
		var f: long(100L) = 100L;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new LongLitDepType().execute();
	}


}
