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
 * Test assigments and coercions.
 *
 * @author nystrom 9/2008
 */
public class Assign2 extends x10Test {
	public def run(): boolean = {
                val a = Rail.make[String](5, (int)=>"hi");
                var i:Settable[int,String]! = a;
                i.set("bye", 1);

                return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Assign2().execute();
	}
}

