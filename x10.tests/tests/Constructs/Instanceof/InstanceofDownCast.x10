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
 * Purpose: Checks both inlined and "side effect aware" cast checking code
 *          works correctly
 * Note: This test try instanceof on several local objects and method call returned one.
 *       It also checks that method call (which may have side effects) are only called once per invocation.
 * @author vcave
 **/
public class InstanceofDownCast extends x10Test {
	 // method calls invocation counters
	 private var counter1: long = 0;
	 private var counter2: long = 0;
	 private var counter3: long = 0;
	 
	public def run(): boolean = {
		var upcast1: Any = new X10DepTypeClassOneB(1);
		var upcast2: Any = new X10DepTypeSubClassOneB(1,2);
		var upcast3: X10DepTypeClassOneB = new X10DepTypeSubClassOneB(2,3);

		// instance of checked against an object
		var res1: boolean = upcast1 instanceof X10DepTypeClassOneB{p==1};
		
		var res2: boolean = upcast2 instanceof X10DepTypeClassOneB{p==1};
		var res3: boolean = upcast2 instanceof X10DepTypeSubClassOneB{p==1&&a==2};

		var res4: boolean = upcast3 instanceof X10DepTypeClassOneB{p==2};
		var res5: boolean = upcast3 instanceof X10DepTypeSubClassOneB{p==2&&a==3};

		// instance of checked against a method return
		var res6: boolean = this.getX10DepTypeClassOneB(1) instanceof X10DepTypeClassOneB{p==1};
		
		var res7: boolean = this.getX10DepTypeSubClassOneB(1,2) instanceof X10DepTypeClassOneB{p==1};
		var res8: boolean = this.getX10DepTypeSubClassOneB(1,2) instanceof X10DepTypeSubClassOneB{p==1&&a==2};

		var res9: boolean = this.getX10DepTypeSubClassOneB_2(2,3) instanceof X10DepTypeClassOneB{p==2};
		var res10: boolean = this.getX10DepTypeSubClassOneB_2(2,3) instanceof X10DepTypeSubClassOneB{p==2&&a==3};

		
		return (res1 && res2 && res3 && res4 && res5 && 
				res6 && res7 && res8 && res9 && res10 &&
				// checks methods have been called only once per invocation
				// by the dynamic checking code.
				(counter1==1) && (counter2==2) && (counter3==2));
	}
	
	private def getX10DepTypeClassOneB(var c1: long): Any = {
		counter1++;
		return new X10DepTypeClassOneB(c1);
	}

	private def getX10DepTypeSubClassOneB(var c1: long, var c2: long): Any = {
		counter2++;
		return new X10DepTypeSubClassOneB(c1, c2);
	}

	private def getX10DepTypeSubClassOneB_2(var c1: long, var c2: long): X10DepTypeClassOneB = {
		counter3++;
		return new X10DepTypeSubClassOneB(c1, c2);
	}

	public static def main(var args: Rail[String]): void = {
		new InstanceofDownCast().execute();
	}

}
