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

import x10.util.Box;
import harness.x10Test;

/**
 * Purpose: 
 * @author vcave
 **/
public class ObjectToNullablePrimitiveConstrained1 extends x10Test {
	 
	public def run(): boolean = {
		var primitive: x10.lang.Any = 3;
		return (primitive instanceof Long{self==3});
	}
	
	public static def main(Rail[String])  {
		new ObjectToNullablePrimitiveConstrained1().execute();
	}
}
