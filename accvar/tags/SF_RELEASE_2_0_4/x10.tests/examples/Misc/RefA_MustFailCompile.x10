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
	
public class RefA(f0: RefB{self.f2.f1==this.f1}, f1:int) extends x10Test {
	public def this(f0_: RefB{self.f2.f1==f1}, f1_: int): RefA { 
		// should give an error the type of an arg to a constructor
		// cannot reference this -- there is no this to refer to!!
		// And indeed it gives that error:
		// \x10.tests\examples\Misc\RefA.x10:15: Cannot access a non-static field or method, or refer to "this" or "super" from a static context.
                property(f0_, f1_);
	}
	public def run(): boolean {
		return true;
	}
	public static def main(var args: Rail[String]): void {
		new RefA(null, 1).execute();
	}
}
