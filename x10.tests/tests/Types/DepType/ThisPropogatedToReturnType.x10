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

/** Tests that if t is of type Test(:i==1), and test() on Test returns int(:self==this.i),
* then t.test() returns int(:self==1). That is, information is propagating
* correctly from the type of the receiver into the return type of a method.
* @author vj
*/
public class ThisPropogatedToReturnType extends x10Test {

	 class Test(i:int, j:int) {
		def this(i:int, j:int):Test{self.i==i&&self.j==j} = { 
			property(i,j);
			}
		
		def test():int{self==this.i} = {
			return  i;
		}
	}
	
	public def run(): boolean = {
		var t: Test{i==1n} = new Test(1n,2n);
		var one: int{self==1n} = t.test();
		return true;
	}
	public static def main(Rail[String]): void = {
		 new ThisPropogatedToReturnType().execute();
	}
}
