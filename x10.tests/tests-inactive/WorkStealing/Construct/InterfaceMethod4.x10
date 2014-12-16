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
public class InterfaceMethod4 {

	def getITest():ITest4{
		return new AAA();
	}
	
	public def run() {
		var passed:boolean = true;
		
		val a:ITest4 = getITest();
		val r = a.set(2);
		passed &= (r == 2);
		Console.OUT.println("r  = " + r);
		
		return passed;
	}

	public static def main(Rail[String]) {
	    val r = new InterfaceMethod4().run();
	    if (r) {
	        x10.io.Console.OUT.println("++++++Test succeeded.");
	    }

	    val x = new B4().set(1);
	}
}

interface ITest4{
	def set(v:int):int;
}

class A4 implements ITest4{

    public def set(v:int):int{
    	return -1;
    }
}

class B4 implements ITest4{

    public def set(v:int):int{
    	return -1;
    }
}

class AA extends A4{

    public def set(v:int):int{
        val value:int;
        finish {
            async value = v;
        }
        return value;
    }
}

class AAA extends AA{
	public def foo2(){
		val aaaa = new AAAA();
		aaaa.foo();
	}
}

class AAAA extends AAA{

	public def foo(){
		set(-1); //dead code, need transform, too
	}
	
    public def set(v:int):int{
    	return -1;
    }
}

class AB extends A4{

    public def set(v:int):int{
    	return -1;
    }
}

