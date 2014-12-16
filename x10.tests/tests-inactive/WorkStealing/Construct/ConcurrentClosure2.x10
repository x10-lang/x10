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
 * Test closure with concurrent method call.
 * Closure is in the method body, and the closure calls a parallel method.
 */
public class ConcurrentClosure2 {
	
	var value:int;
	
	public def this(){
	}
	
	public def foo(i:int):int{
		var result:int = 0;
		finish {
			async result = i;
		}
		return result;
	}
	
	public def run():boolean {
		val f:(int)=>int = (i:int) => foo(i);

		value = f(1);
		
		Console.OUT.println("ConcurrentClosure2: value = " + value);
		return true;
	}
	
	public static def main(Rail[String]) {
        val r = new ConcurrentClosure2().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
	}
}