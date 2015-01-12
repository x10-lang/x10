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

/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Checks that no property must be left uninitialized by a constructor.
 *
 * @author pvarma
 */
public class UninitializedProperty_MustFailCompile(i:int, j:int) extends x10Test {

	public def this(i:int, j:int):UninitializedProperty_MustFailCompile = {
	    property(i); // ERR
	}
	public def run()=true;
	
	public static def main(Rail[String]):void = {
		new UninitializedProperty_MustFailCompile(2n,3n).execute();
	}
}
