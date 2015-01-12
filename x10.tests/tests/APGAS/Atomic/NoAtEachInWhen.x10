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

import harness.x10Test;
import x10.regionarray.*;

/**
 * An ateach statement cannot occur in a when statement.
 * @vj
 */
public class NoAtEachInWhen extends x10Test {

	var b: boolean;
	
	public def run(): boolean = {
			b=true;
			try { 
		      when (b==true) 
		        ateach (p in Region.make(1,10) -> here) 
		           Console.OUT.println("Cannot reach this point.");
			} catch (IllegalOperationException) {
				return true;
			}
		 
		  return false;
	}

	public static def main(Rail[String]) {
		new NoAtEachInWhen().execute();
	}
}
