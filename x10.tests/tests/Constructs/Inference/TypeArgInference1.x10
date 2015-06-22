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
 * Inference for return types.
 *
 * @author bard 5/2010
 */
public class TypeArgInference1 extends x10Test {
        def choose[T] (a:T, b:T):T = a;

	public def run(): boolean {
           chk( HashSet[Int] <: Collection[Int], "pre-1");
           chk( ArrayList[Int] <: Collection[Int], "pre-2");
           chk( Sub <: Super, "pre-3");
           val intSet = new HashSet[Int]();
           val intList = new ArrayList[Int]();
           val y = choose[Collection[Int]](intSet, intList);
           val yy : Collection[Int] = y;
           return true;
	}

	public static def main(var args: Rail[String]): void {
		new TypeArgInference1().execute();
	}
    
   private static interface Collection[T] {}
   private static class HashSet[T] implements Collection[T] {}
   private static class ArrayList[T] implements Collection[T] {}

   private static class Super {}
   private static class Sub extends Super{}

}



