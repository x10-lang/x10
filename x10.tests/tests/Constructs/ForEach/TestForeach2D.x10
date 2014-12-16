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
import x10.array.Array_2;
import x10.compiler.Foreach;
import harness.x10Test;

/**
 * Tests the 2D parallel iteration patterns in the Foreach compiler class.
 */
public class TestForeach2D(N:Long) extends x10Test {
    public def this(N:Long) {
        property(N);
    }

	public def run() {
        // compute matrix identity product: y = I * x
        val identity = new Array_2[Double](N,N,(i:Long,j:Long)=>(i==j)?1.0:0.0);
        val x = new Array_2[Double](N,N,(i:Long,j:Long)=>(i*i+j) as Double);
        val y = new Array_2[Double](N,N);

        val body = (i:Long, j:Long) => {
            var y_ij:Double = 0.0;
            for (k in 0..(N-1)) {
                y_ij += identity(i,k) * x(k,j);
            }
            y(i,j) = y_ij;
        };

        Foreach.basic(0, N-1, 0, N-1, body);
        checkSame(x,y);

        Foreach.block(0, N-1, 0, N-1, body);
        checkSame(x,y);

        Foreach.bisect(0, N-1, 0, N-1, body);
        checkSame(x,y);

        return true;
	}

    public def checkSame(x:Array_2[Double],y:Array_2[Double]) {
        for (i in 0..(N-1)) {
            for (j in 0..(N-1)) {
                chk(y(i,j) == x(i,j));
            }
        }
    }

	public static def main(args:Rail[String]): void = {
        var size:Long = 143;
        var print:Boolean = false;
        if (args.size > 0) {
            size = Long.parse(args(0));
        }
		new TestForeach2D(size).execute();
	}
}
