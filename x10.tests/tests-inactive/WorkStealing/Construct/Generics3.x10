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
 * A simple Generic concurrent method, Generic object in current method.
 */
public class Generics3 {

    var flag: boolean = false;

    class A[T] {
    	var value:T;
    
        def this(v:T){
        	value = v;
        }
    
        def set(v:T){
        	value = v;
        }
    }
    
    var aInt:A[Int];
    
    public def conFoo(a:A[Int]):A[Int]{
    	finish{
    		async aInt = a;
    	}
    	return aInt;
    }

	public def run() {
		var passed:boolean = true;

		val a = new A[Int](1);
		val aRet = conFoo(a);
		passed &= (a.value == aRet.value);
		passed &= (a.value == aInt.value);
		Console.OUT.print("a.value = " + a.value);
		Console.OUT.print("; aRet.value = " + aRet.value);
		Console.OUT.println("; aInt.value = " + aInt.value);

		
		return passed;
	}

	public static def main(Rail[String]) {
            val r = new Generics3().run();
            if (r) {
                x10.io.Console.OUT.println("++++++Test succeeded.");
            }
	}
}
