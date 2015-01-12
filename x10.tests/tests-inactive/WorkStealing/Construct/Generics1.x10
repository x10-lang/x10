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
public class Generics1 {

    var flag: boolean = false;

    class A[T] {
    	var value1:T;
        var value2:T;
    
        def this(v:T){
        	value1 = v;
        	value2 = v;
        }
    
        def conSet(v1:T, v2:T){
        	finish{
        		async value1 = v1;
        		value2 = v2;
        	}
        }
    }

	public def run() {
		var passed:boolean = true;
		
		val aInt = new A[int](0);
		aInt.conSet(1,2);
		passed &= (aInt.value1 == 1);
		passed &= (aInt.value2 == 2);
		Console.OUT.print("aInt.value1 = " + aInt.value1);
		Console.OUT.println("; aInt.value2 = " + aInt.value2);
		
		val aStr = new A[String](null);
		aStr.conSet("a", "b");
		passed &= (aStr.value1.equals("a"));
		passed &= (aStr.value2.equals("b"));
		Console.OUT.print("aStr.value1 = " + aStr.value1);
		Console.OUT.println("; aStr.value2 = " + aStr.value2);
		
		return passed;
	}

	public static def main(Rail[String]) {
	    val r = new Generics1().run();
	    if (r) {
		 x10.io.Console.OUT.println("++++++Test succeeded.");
	    }
	}
}
