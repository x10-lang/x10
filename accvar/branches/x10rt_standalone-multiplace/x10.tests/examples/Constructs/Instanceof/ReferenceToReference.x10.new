/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;
/**
 * Purpose: Check regular java instance of works correctly
 * @author vcave
 **/
public class ReferenceToReference extends x10Test {

	public def run(): boolean = {
		var identity: X10DepTypeClassOne = new X10DepTypeClassOne(1);
		var upcast: java.lang.Object = new X10DepTypeClassOne(1);
		var downcast: X10DepTypeClassOne = new X10DepTypeSubClassOne(1,2);
		
		var res1: boolean = identity instanceof X10DepTypeClassOne;
		var res2: boolean = upcast instanceof X10DepTypeClassOne;
		var res3: boolean = downcast instanceof X10DepTypeClassOne;
		
		return (res1 && res2 && res3);
	}

	public static def main(var args: Rail[String]): void = {
		new ReferenceToReference().execute();
	}

}
