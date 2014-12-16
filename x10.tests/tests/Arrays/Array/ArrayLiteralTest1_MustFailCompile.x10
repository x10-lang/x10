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

/**
 * Rail Literal test. Same as ArrayLiteralTest except that the declared type of the rail is more precise.
 */
public class ArrayLiteralTest1_MustFailCompile extends x10Test {

    public def run(): boolean = {
        val r = [1n, 2n, 3n];
        val a = [0n as Int{self!=0n}, // ERR: Cannot cast expression to type
                 1n as Int{self!=0n},2n as Int{self!=0n},3n as Int{self!=0n}];
        var sumr:int=0n;
        var suma:int=0n;
        for (i in a) suma += i;
        for (i in r) sumr +=i;
        return suma==6n && sumr==6n;
    }


	def test() {
		// size&dimension is inferred correctly for: [YYY]
		val a10 = [1,2,3];
		val a11:Rail[Long] = [1,2,3];
		val a12:Rail[Long] = [1,2,3];
		val a13:Rail[Long]{size==3L} = [1,2,3];
		val a2:Rail[A]{size==3} = [1,2,3]; // ERR: Cannot assign expression to target.
		val a4:Rail[Long]{size==4} = [1,2,3]; // ERR: Cannot assign expression to target.

		val b:Rail[A{self!=null}] = [new A()];
		val b1:Rail[A{self!=null}]{size==1} = [new A()];
	}

	static class A {}


    public static def main(Rail[String]): void = {
        new ArrayLiteralTest1_MustFailCompile().execute();
    }
}
