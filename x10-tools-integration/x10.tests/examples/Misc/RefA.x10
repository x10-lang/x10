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
	
public class RefA(f0: RefB{f2.f1==this.f1}, f1:int) extends x10Test {
	public def this(f0: RefB{self.f2.f1==f1}, f1: int): RefA { 
		// should give an error the type of an arg to a constructor
		// cannot reference this -- there is no this to refer to!!
                property(f0, f1);
	}
	public def run(): boolean {
		return true;
	}
	public static def main(var args: Rail[String]): void {
		new RefA(null, 1).execute();
	}
}
