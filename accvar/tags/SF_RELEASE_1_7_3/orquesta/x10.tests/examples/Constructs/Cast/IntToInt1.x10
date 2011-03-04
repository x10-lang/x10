/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that dep clauses are cheked when checking statically if a cast can be valid at runtime.
 */
public class IntToInt1 extends x10Test {
	public def run(): boolean = {
		var zero: int{self==0} = 0;
		var one: int{self==1} = 1;
		var i: int = one as int;
		one = i as int{self==1};
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new IntToInt1().execute();
	}


}
