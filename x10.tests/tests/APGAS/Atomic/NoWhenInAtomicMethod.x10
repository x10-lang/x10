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

import harness.x10Test;

/**
 * A when statement cannot occur in a method called from within an atomic.
 * @vj
 */
public class NoWhenInAtomicMethod extends x10Test {
	
	public def run(): boolean = {
        try {
        	var lock1:boolean=false;
			atomic 
			  m(lock1);
		} catch (IllegalOperationException) {
			return true;
		} 
		return false;
	}
	def m(var b:boolean) {
		when (!b) 
		   b = true;
	}

	public static def main(Rail[String]){
		new NoWhenInAtomicMethod().execute();
	}
}
