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
 * Purpose: Illustrates various scenario where constraints may causes problems with short.
 * Issue: Contraint value is stored as an integer
 * @author vcave
 **/
public class AssignmentIntegerLitteralToConstrainedShort_MustFailCompile extends x10Test {

	public def run2() = {
		val constraint = 0s;
		var i: short{self == constraint} = 0;
    }
	public def run3() = {
		val constraint:short{self==0s} = 0s;
		var i: short{self == constraint} = 0s;
    }
	public def run(): boolean = {
		val constraint: short = 0;
		var i: short{self == constraint} = 0; // ERR: should fail because constraint: short, not short{self==0}
		return false;
	}

	public static def main(var args: Array[String](1)): void = {
		new AssignmentIntegerLitteralToConstrainedShort_MustFailCompile().execute();
	}

}
