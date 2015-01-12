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
 * Purpose: Checks the numeric expression is not evaluated several
 *          time while checking for constraint.
 * Note : Tricky test case where 'this.incr().j' is of type field,
 * @author vcave
 **/
public class NumericExpressionToPrimitiveDepType_1 extends x10Test {
	public var j: int = -1n;
	
	public def run(): boolean = {
		var i: int{self == 0n} = 0n;
		i = incr().j as int{self == 0n};

		return j == 0n;
	}
	
	private def incr(): NumericExpressionToPrimitiveDepType_1 = {
		j++;
		return this;
	}

	public static def main(var args: Rail[String]): void = {
		new NumericExpressionToPrimitiveDepType_1().execute();
	}

}
