/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;



/**
 * @author bdlucas 8/2008
 */

public class GenericOverloading12_MustFailCompile extends GenericTest {

    class A[T] {
        def m(T) = 0;
    }

    class B[T,U] extends A[T] {
        def m(U) = 1;
    }                           
	def test1(a:B[String,Long]) {
		val x:long{self==0} = a.m("a");
		val y:long{self==1} = a.m(1);
	}
	def test2(a:B[String,String]) {
		a.m("a"); // one overrides the other
	}
	def test3(a:B[String,String{self!=null}]) {
		a.m("a"); // ERR
	}

    public def run(): boolean = true;

    public static def main(var args: Rail[String]): void = {
        new GenericOverloading12_MustFailCompile().execute();
    }
}
