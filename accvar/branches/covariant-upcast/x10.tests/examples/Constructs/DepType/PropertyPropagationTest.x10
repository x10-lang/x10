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
 * Check that if D is of type Dist(R), and E of type Dist(R2), and R and R2
   have the same rank, then D and E have the same rank.
   @author vj 09/2008
 */
public class PropertyPropagationTest extends x10Test {
	public def run(): boolean = {
		val R = [1..10, 1..10] as Region;
	    val R2 = [1..101, 1..101] as Region;
		val D = Dist.makeCyclic(R);
		val E = Dist.makeCyclic(R2);
		val F = D || E;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new PropertyPropagationTest().execute();
	}


}
