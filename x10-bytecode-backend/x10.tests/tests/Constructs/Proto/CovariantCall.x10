/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * It is ok to send in a value of type proto B when the method expects a proto A, if 
 * B <: A.
 * @author vj
 */
public class CovariantCall extends x10Test {

    class A {
    	var x: int;
    }
    class B extends A{
    	def this() {
    		CovariantCall.m(this);
    	}
    }
    	
    static def m(a: proto A) {
    	a.x = 5;
    }
    
    
    public def run() {
    	var b:B = new B();
    	return true;
    }

    public static def main(Rail[String])  {
	new CovariantCall().execute();
    }
}
