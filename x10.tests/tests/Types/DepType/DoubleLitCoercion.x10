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
 * 
 * 1.0 should be coerced to 1.0D in expressions and type expansions.
 * @author vj
 */
public class DoubleLitCoercion extends x10Test {
    def m():double(1.0)=1.0;
    
	public def run()=true;
	public static def main(var args: Rail[String]): void {
		new DoubleLitCoercion().execute();
	}
}
