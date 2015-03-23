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
 *
 * @author vj
 */
public class DoubleLit extends x10Test {
    def m():double(1.0D)=1.0D;
    
	public def run()=true;
	public static def main(var args: Rail[String]): void {
		new DoubleLit().execute();
	}
}
