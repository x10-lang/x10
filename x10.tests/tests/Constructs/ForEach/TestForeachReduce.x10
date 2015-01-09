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
 * Tests the parallel reduction patterns in the Foreach compiler class.
 */
public class TestForeachReduce(N:Long) extends x10Test {
    public def this(N:Long) {
        property(N);
    }

	public def run() {
        val x = new Rail[Double](N, (i:Long) => -i as Double);
        val y = new Rail[Double](N, (i:Long) => i as Double);

        // compute a vector dot product
        val body = (i:Long) => {
            x(i) * y(i)
        };

        var singleThreadedResult:Double = 0.0;
        for (i in 0..(N-1)) {
            singleThreadedResult += body(i);
        }

        val sum = (a:Double,b:Double) => a+b;

        val sequential = Foreach.sequentialReduce(0, N-1, body, sum, 0.0);
        chk(sequential == singleThreadedResult);

        val block = Foreach.blockReduce(0, N-1, body, sum, 0.0);
        chk(block == singleThreadedResult);

        val cyclic = Foreach.cyclicReduce(0, N-1, body, sum, 0.0);
        chk(cyclic == singleThreadedResult);

        val bisect = Foreach.bisectReduce(0, N-1, body, sum, 0.0);
        chk(bisect == singleThreadedResult);

        return true;
	}

	public static def main(args:Rail[String]): void = {
        var size:Long = 1017;
        var print:Boolean = false;
        if (args.size > 0) {
            size = Long.parse(args(0));
        }
		new TestForeachReduce(size).execute();
	}
}
