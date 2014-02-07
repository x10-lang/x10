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
 * Inference of least common ancestor type.
 *
 * @author vj 2/29/2010
 */
public class LCAClassAStructB extends x10Test {
	class A {}
		
	// static because currently non-static struct members are not permitted.
	// XTENLANG-929 
	static struct B { } 
	public def run() {
	  val x = new A();
      val y = B();
	// should succeed. LCA of A and B is Any
      val z:Rail[Any] = [x,y];
	  return true;
	}

	public static def main(Rail[String]) {
		new LCAClassAStructB().execute();
	}
}

