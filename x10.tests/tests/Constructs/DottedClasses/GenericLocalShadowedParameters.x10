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

/**
 * Simple test that generic local classes can compile.
 * Contains an extra twist that the type parameters on
 * the method and the outer class have the same name.
 * @author vj
 */
public class GenericLocalShadowedParameters extends x10Test {

	public class Hello[X] {
		def m[X]() {
			class Local[A]{}
			return new Local[X]();
		}

	}
	public def run()=true;
	
	public static def main(Rail[String]){
		new GenericLocalShadowedParameters().execute();
	}
}
