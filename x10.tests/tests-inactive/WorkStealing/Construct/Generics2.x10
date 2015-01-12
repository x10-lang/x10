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
public class Generics2 {

    var flag: boolean = false;

    class A[T,U] {
    	var value1:T;
        var value2:U;
    
        def this(v1:T, v2:U){
        	value1 = v1;
        	value2 = v2;
        }
    
        def conSet(v1:T, v2:U){
        	finish{
        		async value1 = v1;
        		value2 = v2;
        	}
        }
    }

	public def run() {
		var passed:boolean = true;
		
		val aIntString = new A[int, String](0, null);
		aIntString.conSet(1,"a");
		passed &= (aIntString.value1 == 1);
		passed &= (aIntString.value2.equals("a"));
		Console.OUT.print("aIntString.value1 = " + aIntString.value1);
		Console.OUT.println("; aIntString.value2 = " + aIntString.value2);
		
		return passed;
	}

	public static def main(Rail[String]) {
	    val r = new Generics2().run();
	    if (r) {
		 x10.io.Console.OUT.println("++++++Test succeeded.");
	    }
	}
}
