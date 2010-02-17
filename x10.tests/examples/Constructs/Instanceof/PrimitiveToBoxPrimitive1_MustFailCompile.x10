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
 * Purpose: Checks both literal is directly converted to its Boxed representation
 *          and is an instanceof its Boxed primitive type.
 * @author vcave
 **/
public class PrimitiveToBoxPrimitive1_MustFailCompile extends x10Test {
	 
	public def run()=3 instanceof Box[int];
	
	public static def main(var args: Rail[String]): void = {
		new PrimitiveToBoxPrimitive1_MustFailCompile().execute();
	}
}
