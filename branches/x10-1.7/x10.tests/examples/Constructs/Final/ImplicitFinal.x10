/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Simple array test #1.
 */
public class ImplicitFinal extends x10Test {

	public def run(): boolean = {
		var p: Point = [1, 2, 3];
		var r: Region = [10..10];
		var p1: Point = [1+1, 2+2, 3+3];
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ImplicitFinal().execute();
	}
}
