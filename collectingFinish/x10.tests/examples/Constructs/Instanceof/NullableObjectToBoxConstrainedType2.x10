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

import x10.util.Box;
import harness.x10Test;

/**
 * Purpose: Test covariance of Box. The instanceof test is permitted statically. The runtime
 * test will need to examine the element has the given property.
 * @author vcave
 **/
public class NullableObjectToBoxConstrainedType2 extends x10Test {
	 
	public def run(): boolean = {
		var nullableVarNotNull: Box[ValueClass] = ValueClass(1);
		return nullableVarNotNull instanceof Box[ValueClass{p==1}];
	}
	
	public static def main(Rail[String]) {
		new NullableObjectToBoxConstrainedType2().execute();
	}
}
