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
public class InterfaceMethod2 {

	def getITest2():ITest2{
		return new A();
	}
	
	public def run() {
		var passed:boolean = true;
		
		val a:ITest2 = getITest2();
		val r = a.set(2);
		passed &= (r == 2);
		Console.OUT.println("r  = " + r);
		
		return passed;
	}

	public static def main(Rail[String]) {
	    val r = new InterfaceMethod2().run();
	    if (r) {
	        x10.io.Console.OUT.println("++++++Test succeeded.");
	    }
	}
}

interface ITest2{
	def set(v:int):int;
}

class A implements ITest2{

    public def set(v:int):int{
        val value:int;
	    finish{
		    async value = v;
	    }
	    return value;
    }
}

