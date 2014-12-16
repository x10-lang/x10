/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;
import x10.array.*;

// NUM_PLACES: 4

/**
 * @author bdlucas
 */
public class ArrayReduce2 extends x10Test {

    public static N: long = 9;

    public def run(): boolean {
        val a = new DistArray_Block_1[Double](10, (i:long)=>(i as double));

        val sum = a.reduce((a:double,b:double) => a+b, 0.0);
        Console.OUT.println("sum: " + sum);
	chk(precision.is_equal(sum, 45.0));

        val min = a.reduce((a:double,b:double) => Math.min(a,b), double.POSITIVE_INFINITY);
        Console.OUT.println("min: " + min);
	chk(precision.is_equal(min, 0.0));

        val max = a.reduce((a:double,b:double) => Math.max(a,b), double.NEGATIVE_INFINITY);
        Console.OUT.println("max: " + max);
	chk(precision.is_equal(max, 9.0));

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayReduce2().execute();
    }
}
