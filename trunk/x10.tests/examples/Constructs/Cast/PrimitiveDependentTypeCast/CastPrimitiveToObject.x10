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
 * Purpose: Checks explicit boxing from a litteral to Object is working.
 * @author vcave
 **/
 public class CastPrimitiveToObject extends x10Test {

	public def run(): boolean = {
		var obj: x10.lang.Object = 3 as x10.lang.Object;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new CastPrimitiveToObject().execute();
	}
}
