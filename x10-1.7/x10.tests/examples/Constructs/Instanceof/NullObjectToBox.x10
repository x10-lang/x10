/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a null value is an instanceof a nullable type 
 * Issue: 
 * @author vcave
 * Converted as Box by vj.
 **/
public class NullObjectToBox extends x10Test {
	 
    public def run(): boolean = {
	var array: Rail[Box[ValueClass]] 
	  = Rail.makeVar[Box[ValueClass]](1, (int):Box[ValueClass]=>null);
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
