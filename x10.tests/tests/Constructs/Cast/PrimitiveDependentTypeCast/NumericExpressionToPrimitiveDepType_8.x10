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
 * Purpose: Checks the numeric expression is not evaluated several time while checking for constraint
 * Note: The cast should not be inlined to avoid several execution of j*=2
 * Note: Compiler stores constants in float
 * @author vcave
 **/
public class NumericExpressionToPrimitiveDepType_8 extends x10Test {

	public def run(): boolean {
		var j: double = 0.01;
		var i: double{self == 0.02} = 0.02;
		i = (j *= 2) as double{self==0.02};
		return ((j==0.02) && (i==0.02));
	}

	public static def main(var args: Rail[String]): void {
		new NumericExpressionToPrimitiveDepType_8().execute();
	}

}
