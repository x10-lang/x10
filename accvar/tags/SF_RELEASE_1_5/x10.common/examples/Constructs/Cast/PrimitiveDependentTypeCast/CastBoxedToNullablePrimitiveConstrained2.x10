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

	public boolean run() {
		try {
			// transformed to (nullable<int(:self==3)>) ((BoxedInteger) obj).intValue();
			// which means the actual value of the boxed integer will be checked.			
			nullable<int(:self==3)> i = (nullable<int(:self==3)>) mth();
		} catch(ClassCastException e) {
			return true;
		}
		return false;
	}
	
	public x10.lang.Object mth() {
		// boxed to BoxedInteger(5);		
		return 5;
	}
	public static void main(String[] args) {
		new CastBoxedToNullablePrimitiveConstrained2().execute();
	}
}