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
* Checks boolean comparison type checking is valid.
* @author vcave,11/09/2006
*
*/
public class EqualityBoolean extends x10Test {
   
	public def run(): boolean = {
		var vrai: boolean = true;
		var faux: boolean = false;		

		var c1: boolean = (vrai == true);
		var c2: boolean = (true == vrai);
		var c3: boolean = (faux == false);
		var c4: boolean = (false == faux);
		return true;
	}
	
	public static def main(var args: Rail[String]): void = {
		new EqualityBoolean().execute();
	}
}
