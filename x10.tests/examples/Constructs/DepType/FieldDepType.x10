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
import x10.array.Array;

/**
 *
 * Check that a field can be declared at a deptype.
 *
 */
public class FieldDepType extends x10Test {
    var f: Array[double](0..10)
	= Array.make[double](0..10, (i: Point)=> (10-i(0)) as double);
	
	def m(a: Array[double]{rank==1&&rect&&zeroBased}): void = {
	}
	public def run(): boolean = {
		m(f);
		return f(0)==10.0D;
	}
	public static def main(Rail[String]): void = {
		new FieldDepType().execute();
	}
}
