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
//OPTIONS: -WORK_STEALING=true

/*
 * Test constructor with concurrent procedure. Cannot pass WS Compile.
 */
public class ConcurrentConstructor2_MustFailCompile {
	
	var value:int;
	
	public def this(){
		foo();
	}
	
	private final def foo(){
		finish {
			async value = 1;
		}
	}
	
	public def run():boolean {
		Console.OUT.println("ConcurrentConstructor2: value = " + value);
		return value == 1;
	}
	
	public static def main(Rail[String]) {
        val r = new ConcurrentConstructor2_MustFailCompile().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
	}
}