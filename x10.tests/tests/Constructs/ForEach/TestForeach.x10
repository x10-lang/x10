/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 */
import x10.compiler.Foreach;
import harness.x10Test;

/**
 * Tests the parallel iteration patterns in the Foreach compiler class.
 */
public class TestForeach(N:Long) extends x10Test {
    val alpha = 2.72;

    public def this(N:Long) {
        property(N);
    }

	public def run() {
        val x = new Rail[Double](N);
        val y = new Rail[Double](N, (i:Long) => i as Double);

        val body = (i:Long) => {
            x(i) = alpha * x(i) + y(i);
        };

        reset(x);
        Foreach.basic(0, N-1, body);
        check(x);

        reset(x);
        Foreach.block(0, N-1, body);
        check(x);

        reset(x);
        Foreach.cyclic(0, N-1, body);
        check(x);

        reset(x);
        Foreach.bisect(0, N-1, body);
        check(x);

        return true;
	}

    public def check(x:Rail[Double]) {
        for (i in 0..(N-1)) {
            chk(x(i) == i*alpha+i);
        }
    }

    public def reset(x:Rail[Double]) {
        for (i in 0..(N-1)) {
            x(i) = i as Double;
        }
    }

	public static def main(args:Rail[String]): void = {
        var size:Long = 1000;
        var print:Boolean = false;
        if (args.size > 0) {
            size = Long.parse(args(0));
        }
		new TestForeach(size).execute();
	}
}
