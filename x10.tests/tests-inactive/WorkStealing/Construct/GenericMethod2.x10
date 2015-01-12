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
public class GenericMethod2 {

    var flag: boolean = false;

    class A[U] {
        var value:U;
    
        def this(u:U){
        	value = u;
        }
    
        def m[T](u:U,t:T) {
        	val tmp:T;
        	finish{
        		async tmp = t;
        		value = u;
        	}
        	return tmp;
        }
    }

	public def run() {
		var passed:boolean = true;
		
		val aStr = new A[String](null);
		val i = aStr.m[int]("a", 1);
		passed &= (i == 1);
		passed &= (aStr.value.equals("a"));
		Console.OUT.print("aIntString.m[int] = " + i);
		Console.OUT.println("; aStr.value = " + aStr.value);
		
		return passed;
	}

	public static def main(Rail[String]) {
        val r = new GenericMethod2().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
	}
	
	
}
