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
 * Purpose: Checks the numeric expression is not evaluated several time while checkink for constraint
 * Note: The cast should not be inlined to avoid several execution of j+=1
 * @author vcave
 **/
public class NumericExpressionToPrimitiveDepType_3 extends x10Test {

	public def run(): boolean {
		var j: int = -1n;
		var i: int{self == 0n} = 0n;
		i = (j+=1) as int{self == 0n};
		return ((j==0n) && (i==0n));
	}

	public static def main(var args: Rail[String]): void {
		new NumericExpressionToPrimitiveDepType_3().execute();
	}

}
