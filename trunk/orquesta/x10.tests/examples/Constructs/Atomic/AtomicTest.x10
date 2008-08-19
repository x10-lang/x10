/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Minimal test for atomic.  If the atomic code is not executed atomically
 * the other activity can check that the value difference is not N.
 */
public class AtomicTest extends x10Test {

	var val: long = 0;
	const N: long = 1000;
	var startCount: long = 0;
	var endCount: long = 0;

	public def run(): boolean = {
		var b: boolean; // temp
		async(this) {
			atomic {
				startCount = val;
				for (var i: int = 0; i < N; i++) val++;
				endCount = val;
			}
		}
		for (var i: long = 0; i < N*100; i++) {
			atomic { val = i; b = (endCount != 0); }
			if (b) break;
		}
		// need a memory fence here
		atomic { b = (startCount + N == endCount); }
		return b;
	}

	public static def main(var args: Rail[String]): void = {
		new AtomicTest().execute();
	}
}
