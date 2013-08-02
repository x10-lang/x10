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
 * Purpose: Checks both inlined and "side effect aware" cast checking code
 *          works correctly
 * Note: This test try instanceof on several local objects and method call returned one.
 *       It also checks that method call (which may have side effects) are only called once per invocation.
 * @author vcave
 **/
public class InstanceofDownCast extends x10Test {
	 // method calls invocation counters
	 private var counter1: int = 0n;
	 private var counter2: int = 0n;
	 private var counter3: int = 0n;
	 
	public def run(): boolean = {
		var upcast1: Any = new X10DepTypeClassOneB(1n);
		var upcast2: Any = new X10DepTypeSubClassOneB(1n,2n);
		var upcast3: X10DepTypeClassOneB = new X10DepTypeSubClassOneB(2n,3n);

		// instance of checked against an object
		var res1: boolean = upcast1 instanceof X10DepTypeClassOneB{p==1n};
		
		var res2: boolean = upcast2 instanceof X10DepTypeClassOneB{p==1n};
		var res3: boolean = upcast2 instanceof X10DepTypeSubClassOneB{p==1n&&a==2n};

		var res4: boolean = upcast3 instanceof X10DepTypeClassOneB{p==2n};
		var res5: boolean = upcast3 instanceof X10DepTypeSubClassOneB{p==2n&&a==3n};

		// instance of checked against a method return
		var res6: boolean = this.getX10DepTypeClassOneB(1n) instanceof X10DepTypeClassOneB{p==1n};
		
		var res7: boolean = this.getX10DepTypeSubClassOneB(1n,2n) instanceof X10DepTypeClassOneB{p==1n};
		var res8: boolean = this.getX10DepTypeSubClassOneB(1n,2n) instanceof X10DepTypeSubClassOneB{p==1n&&a==2n};

		var res9: boolean = this.getX10DepTypeSubClassOneB_2(2n,3n) instanceof X10DepTypeClassOneB{p==2n};
		var res10: boolean = this.getX10DepTypeSubClassOneB_2(2n,3n) instanceof X10DepTypeSubClassOneB{p==2n&&a==3n};

		
		return (res1 && res2 && res3 && res4 && res5 && 
				res6 && res7 && res8 && res9 && res10 &&
				// checks methods have been called only once per invocation
				// by the dynamic checking code.
				(counter1==1n) && (counter2==2n) && (counter3==2n));
	}
	
	private def getX10DepTypeClassOneB(var c1: int): Any = {
		counter1++;
		return new X10DepTypeClassOneB(c1);
	}

	private def getX10DepTypeSubClassOneB(var c1: int, var c2: int): Any = {
		counter2++;
		return new X10DepTypeSubClassOneB(c1, c2);
	}

	private def getX10DepTypeSubClassOneB_2(var c1: int, var c2: int): X10DepTypeClassOneB = {
		counter3++;
		return new X10DepTypeSubClassOneB(c1, c2);
	}

	public static def main(var args: Rail[String]): void = {
		new InstanceofDownCast().execute();
	}

}
