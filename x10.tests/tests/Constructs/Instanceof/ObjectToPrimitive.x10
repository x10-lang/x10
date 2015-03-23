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
 * Purpose: Checks a boxed primitive is an instanceof this same primitive type .
 * @author vcave
 **/
public class ObjectToPrimitive extends x10Test {
	 
	public def run(): boolean {
		var primitive: x10.lang.Any = 3n;
		return (primitive instanceof Int);
	}
	
	public static def main(Rail[String])  {
		new ObjectToPrimitive().execute();
	}
}
