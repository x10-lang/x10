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
 * Simple test to check that List compiles and runs.
This produces an infinite loop for the type checker, on 06/25/06
*/

public class ListTest extends x10Test {
	public static class List {
		public val n: int;
		protected var value: Int;
		protected var tail: List!;
  
		public def this(o: Int, t: List!) {
			n=t.n+1;
			tail = t;
			value = o;
		}
		public def this() {
			n=0;
			value=0;
			tail=null;
		}
		public def append(l: List!):List! = {
			return (n==0)? l : new List(value, tail.append(l)); 
		}
		public def nth(k: int): Int = {
			return k==1 ? value : tail.nth(k-1);
     
		}
 
		public def gen(k: int) = {
			return k==0 ? new List() : new List(k, gen(k-1));
		}
	}
		public def run(): boolean = {
			a: List = new List(1, new List(2, new List()));
			b: Int = a.nth(2);
			return b==2;
		}
	
		public static def main(var args: Rail[String]): void = {
			new ListTest().execute();
		}
}
