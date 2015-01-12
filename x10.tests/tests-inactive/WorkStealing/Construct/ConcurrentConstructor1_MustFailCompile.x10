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
//OPTIONS: -WORK_STEALING=true


/*
 * Test constructor with concurrent constructs. Cannot pass WS Compile.
 */
public class ConcurrentConstructor1_MustFailCompile {
	
	var value:int;
	
	public def this(){
		finish {
			async value = 1;
		}
	}
	
	public def run():boolean {
		Console.OUT.println("ConcurrentConstructor1: value = " + value);
		return value == 1;
	}
	
	public static def main(Rail[String]) {
        val r = new ConcurrentConstructor1_MustFailCompile().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
	}
}