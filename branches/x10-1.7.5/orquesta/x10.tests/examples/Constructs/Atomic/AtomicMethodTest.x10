/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Minimal test for atomic method qualifier.
 */
public class AtomicMethodTest extends x10Test {

	var val: long = 0;
	public const N: int = 1000;
	var startCount: long = 0;
	var endCount: long = 0;
	safe def body(): void = {
		startCount = this.val;
		for (var i: int = 0; i < N; i++) this.val++;
		endCount = this.val;
	}

	public def run(): boolean = {
		async(this) body();
		for (var i: long = 0; i < N*100; i++) {
			var b: boolean; // temp
			atomic { this.val = i; b = (endCount != 0); }
			if (b) break;
		}
		// assuming atomics follow program order
		var b: boolean; // temp;
		atomic { b = (startCount + N == endCount); }
		return b;
	}

	public static def main(var args: Rail[String]): void = {
		new AtomicMethodTest().execute();
	}
}
