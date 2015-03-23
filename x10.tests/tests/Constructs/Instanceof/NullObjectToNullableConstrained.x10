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

import x10.util.Box;
import harness.x10Test;

/**
 * Purpose: 
 * Issue: 
 * @author vcave
 **/
public class NullObjectToNullableConstrained extends x10Test {
	 
	public def run(): boolean {
		var array: Rail[X10DepTypeClassOneB] = new Rail[X10DepTypeClassOneB](1);
		var var_: X10DepTypeClassOneB = array(0);
		var nullableVarNull: Box[X10DepTypeClassOneB] = null;
		
		// array[0] is null hence it is not an instance of targeted non nullable type
		var res1: boolean = !(array(0) instanceof  X10DepTypeClassOneB{p==1});
		
		// var is null hence it is not an instance of targeted non nullable type
		var res2: boolean = !(var_ instanceof  X10DepTypeClassOneB{p==1});
		
		// nullableVarNull is null hence it is not an instance of targeted non nullable type
		// var res3: boolean = !(nullableVarNull instanceof  Box[X10DepTypeClassOneB{p==1}]);
		// this will statically fail.
	
		// nullableVarNull is null hence it is an instance of the nullable type
		//var res4: boolean = !(getNullNullable() instanceof Box[X10DepTypeClassOneB{p==1}]); // requires Box to be covariant, and variance was removed

		return res1 && res2;
	}
	
	public def getNullNullable(): Box[X10DepTypeClassOneB] = null;
	
	public static def main(var args: Rail[String]): void {
		new NullObjectToNullableConstrained().execute();
	}
}
