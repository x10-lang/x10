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
import x10.util.Box;

/**
 * Purpose: Checks both literal is directly converted to its Boxed representation
 *          and is an instanceof its Boxed primitive type.
 * @author vcave
 **/
public class PrimitiveToBoxPrimitive1_MustFailCompile extends x10Test {
	 
	public def run()=
	    3n instanceof Box[int]; // ERR: Left operand of instanceof must be castable to right type.
    def test() {
        val y = 3n instanceof Box[int]; // ERR: Left operand of instanceof must be castable to right type.
        val x = 3n as Box[int]; // ERR: Box is no longer covariant
    }
	
	public static def main(var args: Rail[String]): void = {
		new PrimitiveToBoxPrimitive1_MustFailCompile().execute();
	}
}
