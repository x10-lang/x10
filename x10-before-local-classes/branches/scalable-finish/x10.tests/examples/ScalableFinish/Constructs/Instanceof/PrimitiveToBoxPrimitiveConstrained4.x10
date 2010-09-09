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

import x10.util.Box;
 

/**
 * Purpose: Use Box 
 * @author vcave
 **/
public class PrimitiveToBoxPrimitiveConstrained4   {
	 
	public def run(): boolean = {
		val a:Any = 3;
		return !(a instanceof Box[Int(4)]);
	}
	
	public static def main(var args: Rail[String]): void = {
		new PrimitiveToBoxPrimitiveConstrained4().run ();
	}
}
