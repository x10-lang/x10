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
 * Purpose: Checks a boxed primitive is an instanceof this same primitive type .
 * @author vcave
 **/
public class ObjectToPrimitive2 extends x10Test {
	 
	public def run(): boolean = {
		var array: Rail[X10DepTypeClassOne]!
		= Rail.make[X10DepTypeClassOne](1, (int):X10DepTypeClassOne=>null);
		var var: x10.lang.Any = array(0);
		return !(var instanceof X10DepTypeClassOne);
	}
	
	public static def main(Rail[String])  {
		new ObjectToPrimitive2().execute();
	}
}
