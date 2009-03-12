/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Minimal busy-waiting style test for async.
 * Written in this particular way to
 * a) always terminate
 * b) not use any other x10 construct
 */
public class AsyncTest extends x10Test {

	var flag: boolean = false;
	const N: long = 1000000000;

	public def run(): boolean = {
		var b: boolean = false;
		async ( here ) { atomic { this.flag = true; } }
		for (var i: long = 0; i < N*100; i++) {
			atomic { b = flag; }

			if (b) break;
		}
		return b;
	}

	public static def main(var args: Rail[String]): void = {
		new AsyncTest().execute();
	}
}
