/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Future test.
 */
public class Future1 extends x10Test {
	public def run(): boolean = {
		val x  = future  41 ;
		return x()+1 == 42;
	}

	public static def main(var args: Rail[String]): void = {
		new Future1().execute();
	}
}
