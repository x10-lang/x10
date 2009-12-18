/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a constrained cast leading to primitive unboxing works 
 *          actually checks the unboxed primitive.
 * Issue: Constraint is not meet when unboxing the primitive.
 * @author vcave
 **/
 public class CastBoxedToNullablePrimitiveConstrained2 extends x10Test {

	public def run(): boolean = {
		try {
			// transformed to (nullable<int(:self==3)>) ((BoxedInteger) obj).intValue();
			// which means the actual value of the boxed integer will be checked.			
			var i: Box[int{self==3}] = mth() as Box[int{self==3}];
		} catch(e: ClassCastException) {
			return true;
		}
		return false;
	}
	
	public def mth(): x10.lang.Object = {
		// boxed as BoxedInteger(5);		
		return 5;
	}
	public static def main(var args: Rail[String]): void = {
		new CastBoxedToNullablePrimitiveConstrained2().execute();
	}
}
