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

        val sequential = Foreach.Sequential.reduce(0..(N-1), body, sum, 0.0);
        chk(sequential == singleThreadedResult);

        val sequentialReduce = new Foreach.Sequential.Reducer(sum, 0.0);
	sequentialReduce.for (i: Long in 0..(N-1)) {
	    body(i)
	}
        chk(sequentialReduce.value() == singleThreadedResult);

        val block = Foreach.Block.reduce(0..(N-1), body, sum, 0.0);
        chk(block == singleThreadedResult);

	val blockReduce = new Foreach.Block.Reducer(sum, 0.0);
	blockReduce.for (i: Long in 0..(N-1)) {
	    body(i)
	}
        chk(blockReduce.value() == singleThreadedResult);

        val cyclic = Foreach.Cyclic.reduce(0..(N-1), body, sum, 0.0);
        chk(cyclic == singleThreadedResult);

	val cyclicReduce = new Foreach.Cyclic.Reducer(sum, 0.0);
	cyclicReduce.for (i: Long in 0..(N-1)) {
	    body(i)
	}
        chk(cyclicReduce.value() == singleThreadedResult);


        val bisect = Foreach.Bisect.reduce(0..(N-1), body, sum, 0.0);
        chk(bisect == singleThreadedResult);

	val bisectReduce = new Foreach.Bisect.Reducer(sum, 0.0);
	bisectReduce.for (i: Long in 0..(N-1)) {
	    body(i)
	}
        chk(bisectReduce.value() == singleThreadedResult);

        return true;
	}

	public static def main(args:Rail[String]): void {
        var size:Long = 1017;
        var print:Boolean = false;
        if (args.size > 0) {
            size = Long.parse(args(0));
        }
		new TestForeachReduce(size).execute();
	}
}
