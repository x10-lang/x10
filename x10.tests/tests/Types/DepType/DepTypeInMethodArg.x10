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
 * Check that a method can have a deptype argument and it is checked properly.
 *
 * @author vj
 */
 public class DepTypeInMethodArg extends x10Test {
     class Test(i:int, j:int) {
	 public def this(i:int, j:int):Test{self.i==i&&self.j==j} {
	     property(i,j);
	 }
     }
     def m(t1:Test, t2:Test{i==t1.i})=true;
     public def run():boolean {
	 val x:Test{i==j} = new Test(1n,1n);
	 return true;
     }
     public static def main(var args: Rail[String]): void {
	 new DepTypeInMethodArg().execute();
     }
 }
