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

//OPTIONS: -STATIC_CHECKS

import harness.x10Test;

/**
 * Check that a method can have a deptype argument and it is checked properly.
 *
 * @author vj
 */
public class DepTypeInMethodArg_MustFailCompile extends x10Test {
    class Test(i:int, j:int) {
       public def this(i:int,j:int):Test{self.i==i&&self.j==j} = { 
	   property(i,j);
       }
    }
    public def m(t: Test{i==j}) = true; 

    public def run2(): boolean = {
	val x = new Test(1n,1n);
	return m(x); 
    }
    public def run(): boolean = {
	// should fail because the declared type of the variable is just Test.
	val x: Test = new Test(1n,1n); 
	return m(x); // ERR
    }
    public static def main(var args: Rail[String]): void = {
	new DepTypeInMethodArg_MustFailCompile().execute();
    }
}
