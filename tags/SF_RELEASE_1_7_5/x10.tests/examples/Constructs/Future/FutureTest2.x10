/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Minimal test for future.
 */
public class FutureTest2 extends x10Test {

	public def run(): boolean = {
		val ret = future (here) { this.m() };
		return ret();
	}

	def m() = true;

	public static def main(var args: Rail[String]): void = {
		new FutureTest2().execute();
	}
}
