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
 * 3 is an int, hence a struct, hence has no home.
 *
 * @author vj, igor 09/06
 */
public class PrimitiveLiteralHasLocation_MustFailCompile extends x10Test {

    public def run(): boolean = {
	return 3.home==null;
    }

	public static def main(Rail[String]) {
	    new PrimitiveLiteralHasLocation_MustFailCompile().execute();
	}
}
