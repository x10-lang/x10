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
 * Purpose: Checks a null value is an instanceof a nullable type 
 * Issue: 
 * @author vcave
 * Converted as Box by vj.
 **/
public class NullObjectToBox extends x10Test {
	 
    public def run(): boolean = {
	var array: Rail[Box[ValueClass]] = new Rail[Box[ValueClass]](1);
	var v: Box[ValueClass] = array(0);
	var nullableVarNull: Box[ValueClass] = null;

	// array[0] is null hence it is not an instance of targeted non nullable type
	val res1 = !(array(0) instanceof Box[ValueClass]);
		
	// var is null hence it is not an instance of targeted non nullable type
	val res2 = !(v instanceof Box[ValueClass]);
		
	// nullableVarNull is null hence it is an instance of the nullable type
	val res3 = !(nullableVarNull instanceof Box[ValueClass]);
		
	// getNullNullable() is null hence it is an instance of the nullable type
	var res4: boolean = !(getNullNullable() instanceof Box[ValueClass]);

	return res1 && res2 && res3 && res4;
    }
	
    public def getNullNullable() = null;

	
    public static def main(Rail[String]) = {
	new NullObjectToBox().execute();
    }
}
