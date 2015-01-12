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
import x10.regionarray.*;

/**
 * Simple array test #1.
 */
public class ImplicitFinal extends x10Test {

	public def run(): boolean = {
		var p: Point = [1, 2, 3];
		var r: Region = Region.make(10,10);
		var p1: Point = [1+1, 2+2, 3+3];
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ImplicitFinal().execute();
	}
}
