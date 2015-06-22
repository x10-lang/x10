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

import x10.util.Box;
import harness.x10Test;

/**
 * Purpose: Checks a null value is an instance of a constrained nullable type.
 * @author vcave
 **/
public class NullToBoxConstrainedType extends x10Test {
	 
	public def run()=!(null instanceof Box[ValueClass{p==1}]);
	
	public static def main(var args: Rail[String]): void {
		new NullToBoxConstrainedType().execute();
	}
}
