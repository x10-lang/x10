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
 * Inference of least common ancestor type.
 *
 * @author vj 2/29/2010
 */
public class LCA1 extends x10Test {
	public def run() {
	  val x:double = 1.2345678;
      val y:int = 2n;
	// should succeed. LCA of double and int is Any.
      val z:Rail[Any] = [x,y];
	  return true;
	}

	public static def main(Rail[String]) {
		new LCA1().execute();
	}
}

