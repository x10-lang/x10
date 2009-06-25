/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a boxed primitive is an instanceof this same primitive type .
 * @author vcave
 **/
public class ObjectToPrimitive2 extends x10Test {
	 
	public def run(): boolean = {
		var array: Rail[X10DepTypeClassOne] 
		= Rail.makeVar[X10DepTypeClassOne](1, (nat):X10DepTypeClassOne=>null);
		var var: x10.lang.Object = array(0);
		return !(var instanceof Int);
	}
	
	public static def main(var args: Rail[String]): void = {
		new ObjectToPrimitive2().execute();
	}
}
