/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Purpose: Checks constraint's variable are resolved.
 * @author vcave
 **/
public class Integer_ConstraintWithVariable extends x10Test {

	public def run(): boolean = {
		val iconstraint: int{self==0} = 0;
		// constraint's variable must be final
		// hence these two types should be equivalent
		var i1: int{self == iconstraint} = 0;
		var i2: int{self==iconstraint} = 0 as int{self==iconstraint};
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Integer_ConstraintWithVariable().execute();
	}


}
