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
import x10.regionarray.*;

/**
 * The test checks that information in the types of variables occuring in depclauses is used
 * during entailment. Here b is of type region(:rank==a.rank), so it would seem that it does
 * not entail self.rank==2. However, a is of type region(:rank==2), and so it is definitely the case
 * that a.rank==2. Hence self.rank==2, and the assignment should succeed.
 *
 * @author vj
 */
public class Transitive extends x10Test {
	
	public def run(): boolean {
	    val a: Region{rank==2} = Region.make(0..10, 0..10);
	    val b: Region{rank==a.rank} = a;
	    var c: Region{rank==2} = b;
	    return true;
	}
	public static def main(var args: Rail[String]): void {
		new Transitive().execute();
	}
}
