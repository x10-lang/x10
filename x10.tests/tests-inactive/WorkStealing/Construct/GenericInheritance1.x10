//LIMITATION: Not support Inheritance call graph building in WS right now
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
 * A simple Generic cast
 */
public class GenericInheritance1 {
	
    interface I[T] {
        def m(T):int;
    }
    
    class A implements I[int] {
        public def m(v:int) {
        	var tmp:int = 0;
        	finish {
        		async tmp = v;
        	}
            return tmp;
        }

    }
	
	var flag: boolean = false;

	public def run() {
		var passed:boolean = true;
	
        val a = new A();
        val value = a.m(2);
        
		passed &= (value == 2);
		Console.OUT.println("a.m(2) = " + value);
		
		return passed;
	}

	public static def main(Rail[String]) {
        val r = new GenericInheritance1().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
	}
	
	
}
