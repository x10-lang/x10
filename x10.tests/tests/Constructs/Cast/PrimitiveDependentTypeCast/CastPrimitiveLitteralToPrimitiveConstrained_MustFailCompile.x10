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
 * Purpose: Checks dependent type constraint information are propagated along with the variable.
 * Issue: Constant to promotedoes not meet constraint of targeted type.
 * @author vcave
 **/
public class CastPrimitiveLitteralToPrimitiveConstrained_MustFailCompile extends x10Test {

	public def run(): boolean {
		
		try { 
           val j: int(0n) = 1n; // ERR
		}catch(e: Exception) {
			return false;
		}

		return true;
	}

	public static def main(args: Rail[String]): void {
		new CastPrimitiveLitteralToPrimitiveConstrained_MustFailCompile().execute();
	}

}
 
