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
 * Purpose: Checks assignment of primitive to constrained primitives.
 * @author vcave
 **/
public class AssignmentLiteralPrimitiveToPrimitive extends x10Test {

	 public def run(): boolean = {
	        /* See XTENLANG-1129 */
	        /*
		var bb: byte{self==1} = 1;
		var ss: short{self==10} = 10;
		*/
		var ii: int{self==20} = 20;
		var iii: int{self==-2} = -2;
		var ll: long{self==30L} = 30L;
		var ff: float{self==0.001F} = 0.001F;
		var i: double{self == 0.001} = 0.001;
		var cc: char{self=='c'} = 'c';
		
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new AssignmentLiteralPrimitiveToPrimitive().execute();
	}
}
