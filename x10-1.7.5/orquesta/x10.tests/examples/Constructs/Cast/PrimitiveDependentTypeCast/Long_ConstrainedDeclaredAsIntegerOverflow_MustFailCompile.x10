/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Purpose: Shows a constraint value may be overflowed.
 * Issue: Declared constraint is an overflowed integer, which makes assignment fail at runtime.
 * @author vcave, vj
 **/
public class Long_ConstraintDeclaredAsIntegerOverflow_MustFailCompile extends x10Test {

	 private val overIntMax: long = (x10.lang.Int.MAX_VALUE as long) + 10000;
	 
	 public def run(): boolean = {
		try {
			// This value cannot fit in an integer, so the compiler must flag an error.
			var l2: long{self==2147493647} = overIntMax as long{self==2147493647};
		} catch (var e: ClassCastException) {
			return true;
		}
		return false;
	}

	public static def main(var args: Rail[String]): void = {
		new  Long_ConstraintDeclaredAsIntegerOverflow_MustFailCompile().execute();
	}

}
