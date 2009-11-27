/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Minimal test for atomic method qualifier.
 */
public class AtomicMethodTest extends x10Test {

    var val: long = 0;
    public const N: int = 1000;
    var startCount: long = 0;
    var endCount: long = N;
    atomic def body() {
	startCount = this.val;
	for (var i: int = 0; i < N; i++) this.val++;
	endCount = this.val;
    }

    public def run()  {
	async(this) body();
	for (var i: long = 0; i < N*N; i++) {
	    var b: boolean; // temp
	    atomic { this.val = i; b = (endCount != 0); }
	    if (b) break;
	}
	// assuming atomics follow program order
	var b: boolean; // temp;
	atomic { b = (startCount + N == endCount); }
	return b;
    }

	public static def main(Rail[String]) {
	    new AtomicMethodTest().execute();
	}
}
