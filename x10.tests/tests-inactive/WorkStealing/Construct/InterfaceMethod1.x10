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
 * A method implements an interface, and the method contains concurrent
 */
public class InterfaceMethod1 {

	class A implements ITest{
		
	    public def set(v:int):int{
	        val value:int;
		    finish{
			    async value = v;
		    }
		    return value;
	    }
	}
	
	
	public def run() {
		var passed:boolean = true;
		
		val a:ITest = new A();
		val r = a.set(1);
		passed &= (r == 1);
		Console.OUT.println("r  = " + r);
		
		return passed;
	}

	public static def main(Rail[String]) {
	    val r = new InterfaceMethod1().run();
	    if (r) {
	        x10.io.Console.OUT.println("++++++Test succeeded.");
	    }
	}
}

interface ITest{
	def set(v:int):int;
}



