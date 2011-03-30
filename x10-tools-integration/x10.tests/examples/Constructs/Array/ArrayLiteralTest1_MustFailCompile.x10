/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * Array Literal test. Same as ArrayLiteralTest except that the declared type of the array is more precise.
 */

public class ArrayLiteralTest1_MustFailCompile extends x10Test {

    public def run(): boolean = {

        val e = 0..9;
        val r = [1, 2, 3];
        val a = new Array[Int{self!=0}][
                                        0, // ERR: The literal is not of the given type
                                        1,2,3];
        var sumr:int=0, suma:int=0;
        for (i in a.values()) suma += i;
        for (i in r.values()) sumr +=i;
        return suma==6 && sumr==6;
    }


	def test() {
		// size&dimension is inferred correctly for: new Array[XX][YYY]
		val a10 = new Array[Int][1,2,3];
		val a11:Array[Int] = new Array[Int][1,2,3];
		val a12:Array[Int](1) = new Array[Int][1,2,3];
		val a13:Array[Int](1){size==3} = new Array[Int][1,2,3];
		val a2:Array[A](1){size==3} = new Array[Int][1,2,3]; // ERR: Cannot assign expression to target.
		val a3:Array[Int](2){size==3} = new Array[Int][1,2,3]; // ERR: Cannot assign expression to target.
		val a4:Array[Int](1){size==4} = new Array[Int][1,2,3]; // ERR: Cannot assign expression to target.

		val b:Array[A] = new Array[A][new A()];
		val b1:Array[A](1){size==1} = new Array[A][new A()];
	}	
	def test2() {
		// size&dimension is inferred correctly for: [YYY]
		val a10 = [1,2,3];
		val a11:Array[Int] = [1,2,3];
		val a12:Array[Int](1) = [1,2,3];
		val a13:Array[Int](1){size==3} = [1,2,3];
		val a2:Array[A](1){size==3} = [1,2,3]; // ERR: Cannot assign expression to target.
		val a3:Array[Int](2){size==3} = [1,2,3]; // ERR: Cannot assign expression to target.
		val a4:Array[Int](1){size==4} = [1,2,3]; // ERR: Cannot assign expression to target.

		val b:Array[A{self!=null}] = [new A()];
		val b1:Array[A{self!=null}](1){size==1} = [new A()];		
	}

	static class A {}


    public static def main(Array[String](1)): void = {
        new ArrayLiteralTest1_MustFailCompile().execute();
    }
}
