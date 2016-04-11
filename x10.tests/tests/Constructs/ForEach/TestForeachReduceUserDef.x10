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
import x10.util.foreach.*;
import harness.x10Test;

/**
 * Tests the parallel reduction patterns in the Foreach compiler class.
 */
public class TestForeachReduceUserDef(N:Long) extends x10Test {
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

        val sum = ((a:Double,b:Double) => a+b);
	val sumAndSave = (res:Cell[Double]) => {
	    return (a:Double,b:Double) => {
		val s = a+b;
		res() = s;
		s
	    };
	};

        val sequential = Sequential.operator for(0..(N-1), sum, 0.0, body);
        chk(sequential == singleThreadedResult);

        val sequentialResult = new Cell[Double](0.0);
	Sequential.for(i:Long in 0..(N-1), sumAndSave(sequentialResult), 0.0) {
	    body(i)
	}
        chk(sequentialResult() == singleThreadedResult);


        val sequentialReduce = new Sequential.Reducer(sum, 0.0);
	sequentialReduce.for (i: Long in 0..(N-1)) {
	    body(i)
	}
        chk(sequentialReduce.value() == singleThreadedResult);

        val block = Block.operator for(0..(N-1), sum, 0.0, body);
        chk(block == singleThreadedResult);

        val blockResult = new Cell[Double](0.0);
	Block.for(i:Long in 0..(N-1), sumAndSave(blockResult), 0.0) {
	    body(i)
	}
        chk(blockResult() == singleThreadedResult);

	val blockReduce = new Block.Reducer(sum, 0.0);
	blockReduce.for (i: Long in 0..(N-1)) {
	    body(i)
	}
        chk(blockReduce.value() == singleThreadedResult);

        val cyclic = Cyclic.operator for(0..(N-1), sum, 0.0, body);
        chk(cyclic == singleThreadedResult);

        val cyclicResult = new Cell[Double](0.0);
	Cyclic.for(i:Long in 0..(N-1), sumAndSave(cyclicResult), 0.0) {
	    body(i)
	}
        chk(cyclicResult() == singleThreadedResult);

	val cyclicReduce = new Cyclic.Reducer(sum, 0.0);
	cyclicReduce.for (i: Long in 0..(N-1)) {
	    body(i)
	}
        chk(cyclicReduce.value() == singleThreadedResult);


        val bisect = Bisect.operator for(0..(N-1), sum, 0.0, body);
        chk(bisect == singleThreadedResult);

        val bisectResult = new Cell[Double](0.0);
	Bisect.for(i:Long in 0..(N-1), sumAndSave(bisectResult), 0.0) {
	    body(i)
	}
        chk(bisectResult() == singleThreadedResult);

	val bisectReduce = new Bisect.Reducer(sum, 0.0);
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
	new TestForeachReduceUserDef(size).execute();
    }
}
