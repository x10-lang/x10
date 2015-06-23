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
 * Block used inside an expression is considered as a closure without argument.
 *
 * @author lmandel 06/2015
 */

public class ClosureBlockExpression2a_MustFailCompile extends x10Test {

    public def run(): boolean {
	if (true) { return "Not closure";  };
	return true;
    }

    public static def main(var args: Rail[String]): void {
        new ClosureBlockExpression2a_MustFailCompile().execute();
    }
}
