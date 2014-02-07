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
 * Purpose: Illustrates float dependent type usage
 * Note: Append an 'F' forces constraint representation to be a float.
 * @author vcave
 **/
public class Float_ConstraintDeclaredAsFloat extends x10Test {

	public def run(): boolean = {
		var j: float = 0.00001F;
		// the constraint is represented as a long
		var i: float{self == 0.00002F} = 0.00002F;
		i = (j*2) as float{self == 0.00002F};
		return ((j == 0.00001F) && (i==0.00002F));
	}

	public static def main(var args: Rail[String]): void = {
		new Float_ConstraintDeclaredAsFloat().execute();
	}

}
