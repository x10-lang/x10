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

public class CheckDescendEntails extends x10Test {
	class Prop(i:int, j:int) {
		public def this(i: int, j: int): Prop{self.i==i&&self.j==j} = {
			property(i,j);
		}
		}
    class Test(a:Prop, b:Prop) {
        public def this(a:Prop, b:Prop): Test{self.a==a&&self.b==b} = {
        	property(a,b);
        }
     }
  
	public def run(): boolean = {
	   val p: Prop = new Prop(1n,2n);	
	   var t: Test{a == b} = new Test(p,p);
	   var u: Test{a.i == b.i} = t;
	   return true;
	}
	public static def main(var args: Rail[String]): void = {
		new CheckDescendEntails().execute();
	}
}
