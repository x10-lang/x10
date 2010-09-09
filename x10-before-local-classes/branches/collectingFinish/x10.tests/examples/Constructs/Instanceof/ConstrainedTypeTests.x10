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
 * Purpose: Check that type tests work
 * @author igorp
 **/
public class ConstrainedTypeTests extends x10Test {

	public def run(): boolean = {
		var res1: boolean = X10DepTypeClassOne == X10DepTypeClassOne;
		var res2: boolean = X10DepTypeSubClassOne <: X10DepTypeClassOne;
		var res3: boolean = !(X10DepTypeSubClassOne <: Int);
		var res4: boolean = !(X10DepTypeSubClassOne{a==2} <: X10DepTypeClassOne{p==2});
		var res5: boolean = !(X10DepTypeSubClassOne{a==3} <: X10DepTypeClassOne{p==2});
		var res6: boolean = X10DepTypeSubClassOne{p==2} <: X10DepTypeClassOne{p==2 && p > 1 && p < 10};
		x10.io.Console.OUT.println("X10DepTypeSubOne == X10DepTypeClassOne -> "+res1);
		x10.io.Console.OUT.println("X10DepTypeSubClassOne <: X10DepTypeClassOne -> "+res2);
		x10.io.Console.OUT.println("X10DepTypeSubClassOne <: Int -> "+(!res3));
		x10.io.Console.OUT.println("X10DepTypeSubClassOne{a==2} <: X10DepTypeClassOne{p==2} -> "+(!res4));
		x10.io.Console.OUT.println("X10DepTypeSubClassOne{a==3} <: X10DepTypeClassOne{p==2} -> "+(!res5));
		x10.io.Console.OUT.println("X10DepTypeSubClassOne{p==2} <: X10DepTypeClassOne{p==2} -> "+res6);
		
		return (res1 && res2 && res3 && res4 && res5 && res6);
	}

	public static def main(var args: Rail[String]): void = {
		new ConstrainedTypeTests().execute();
	}

}
