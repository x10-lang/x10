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
* Purpose: Checks boolean comparison type checking is valid.
* @author vcave,11/09/2006
*
*/
public class EqualityInteger extends x10Test {
  
	public def run(): boolean = {
		var un: int{self==1n} = 1n;
		var deux: int{self==2n} = 2n;
		
		return !(un == deux) && (un != deux);
	}

	public static def main(var args: Rail[String]): void = {
		new EqualityInteger().execute();
	}
}
