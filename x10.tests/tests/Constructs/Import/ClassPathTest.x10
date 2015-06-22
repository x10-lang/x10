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

import testPackage.*;
import harness.x10Test;

/**
 * Testing if -classpath './x10lib' is recognized
 * testPackage is a package in ./x10lib
 *
 * @author kemal
 */
public class ClassPathTest extends x10Test {

	public def run(): boolean {
		return T1.m1(49n);
	}

	public static def main(var args: Rail[String]): void {
		new ClassPathTest().execute();
	}
}
