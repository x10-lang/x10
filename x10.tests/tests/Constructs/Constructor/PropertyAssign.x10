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
 * Check that x=y implies x.f=y.f.
 *
 * @author vj
 */
public class PropertyAssign extends x10Test {
	class Prop(i: int, j: int) {
		public def this(i: int, j: int): Prop = {
			property(i,j);
		}
        }
  
	public def run(): boolean = {
                var p: Prop = new Prop(1n,2n);
	        return true;
	}

	public static def main(var args: Rail[String]): void = {
		new PropertyAssign().execute();
	}
}
