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
/**
 * Purpose: Check regular java instance of works correctly
 * @author vcave
 **/
public class ReferenceToReference extends x10Test {

	public def run(): boolean = {
		var identity: X10DepTypeClassOneB = new X10DepTypeClassOneB(1);
		var upcast: Any = new X10DepTypeClassOneB(1);
		var downcast: X10DepTypeClassOneB = new X10DepTypeSubClassOneB(1,2);
		
		var res1: boolean = identity instanceof X10DepTypeClassOneB;
		var res2: boolean = upcast instanceof X10DepTypeClassOneB;
		var res3: boolean = downcast instanceof X10DepTypeClassOneB;
		
		return (res1 && res2 && res3);
	}

	public static def main(var args: Rail[String]): void = {
		new ReferenceToReference().execute();
	}

}
