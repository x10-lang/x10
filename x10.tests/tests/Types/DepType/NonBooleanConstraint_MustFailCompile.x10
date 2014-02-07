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

/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that a nonboolean constraint is rejected.
 *
 * @author pvarma
 */
public class NonBooleanConstraint_MustFailCompile(i:int, j:int)
    {this.i} // ERR ERR: The type of the constraint NonBooleanConstraint_MustFailCompile.this.i must be boolean, not x10.lang.Int{self==NonBooleanConstraint_MustFailCompile#this.i}.
    extends x10Test {

	public def this(k:int):NonBooleanConstraint_MustFailCompile = {
	    property(k,k);
	}
	public def run()=true;
	
	public static def main(Rail[String])  = {
		new NonBooleanConstraint_MustFailCompile(2n).execute();
	}
}
