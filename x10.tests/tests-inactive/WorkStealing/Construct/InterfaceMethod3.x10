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
 * A method implements an interface, and the method contains concurrent
 */
public class InterfaceMethod3 {

	def getITest3(flag:double):ITest3{
	    val r:ITest3;
	    if (flag > 0.5) {
	        r = new A3();
	    }
	    else {
	        r = new B();
	    }
	    return r;
	}
	
	public def run() {
		val random = new x10.util.Random();
		var passed:boolean = true;
		
		val flag:double = random.nextDouble();
		val a = getITest3(flag);
		val r = a.set(2);
		if (flag > 0.5) {
			passed &= (r == 2);
		}
		else {
			passed &= (r == -1);
		}
		Console.OUT.println("r  = " + r);
		
		return passed;
	}

	public static def main(Rail[String]) {
	    val r = new InterfaceMethod3().run();
	    if (r) {
	         x10.io.Console.OUT.println("++++++Test succeeded.");
	    }
	}
}

interface ITest3{
	def set(v:int):int;
}

class A3 implements ITest3{

    public def set(v:int):int{
        val value:int;
        finish{
    	    async value = v;
        }
        return value;
    }
}

class B implements ITest3{

    public def set(v:int):int{
    	return -1;
    }
}

