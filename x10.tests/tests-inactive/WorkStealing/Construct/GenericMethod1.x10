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
 * A simple Generic concurrent method
 */
public class GenericMethod1 {

    var flag: boolean = false;

    def m[T](t:T) {
    	val v:T;
    	finish{
    		async v = t;
    	}
    	return v;
    }

	public def run() {
		var passed:boolean = true;
		
		val aInt = m[int](1);
		passed &= (aInt == 1);
		
		val aStr = m[String]("a");
		passed &= (aStr.equals("a"));

		Console.OUT.print("aInt = " + aInt);
		Console.OUT.println("; aStr = " + aStr);
		
		return passed;
	}

	public static def main(Rail[String]) {
        val r = new GenericMethod1().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
	}
	
	
}
