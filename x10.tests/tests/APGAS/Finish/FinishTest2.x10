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
 * Checks if finish also consideres grand-children.
 * @author Christoph von Praun
 */
public class FinishTest2 extends x10Test {

	var flag: boolean;
	var foo: int;

	public def run(): boolean = {
		atomic flag = false;
		finish {
			async  {
				atomic foo = 123n;
				async  {
					atomic foo = 42n;
					x10.io.Console.OUT.print("waiting ...");
					System.sleep(2000);
					x10.io.Console.OUT.println("done.");
					atomic flag = true;
				}
			}
		}
		var b: boolean;
		atomic b = flag;
		x10.io.Console.OUT.println("The flag is b = " + b + " (should be true).");
		return (b == true);
	}

	public static def main(var args: Rail[String]): void = {
		new FinishTest2().execute();
	}
}
