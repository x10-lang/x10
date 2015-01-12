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
 * The test checks that an argument of type Test(:self.i==j), where j is a local variable, 
   cannot be passed as an argument to a method which requires Test(:self.i==self.j).
 *
 * @author vj
 */
public class CheckEqualTypes extends x10Test {
    class Test(i:int, j:int) {
       public def this(i:int, j:int):Test{self.i==i&&self.j==j} = {
        property(i,j);}
    }

    public def m(var t: Test{i==j}): boolean = { // the type is Test(:self.i==self.j).
      return true;
    }
	public def run(): boolean = {
	    val j = 0n;
	    var t: Test{i==j&&self.j==j} = new Test(0n,0n); 
	    // should compile since the type entails Test{i==j}.
	    return m(t); 
	}
	public static def main(var args: Rail[String]): void = {
		new CheckEqualTypes().execute();
	}
}
